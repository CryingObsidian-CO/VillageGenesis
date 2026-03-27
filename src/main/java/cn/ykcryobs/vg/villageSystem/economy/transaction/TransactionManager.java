package cn.ykcryobs.vg.villageSystem.economy.transaction;

import cn.ykcryobs.vg.VillageGenesis;
import cn.ykcryobs.vg.config.ServerConfig;
import cn.ykcryobs.vg.init.ModRegistries;
import cn.ykcryobs.vg.item.ITradableItem;
import cn.ykcryobs.vg.utils.MapUtils;
import cn.ykcryobs.vg.villageSystem.VillageManager;
import cn.ykcryobs.vg.villageSystem.economy.payment.IPayment;
import cn.ykcryobs.vg.villageSystem.economy.payment.PaymentMethod;
import cn.ykcryobs.vg.villageSystem.economy.tax.tradeTax.TradeTaxCalculator;
import cn.ykcryobs.vg.villageSystem.economy.tradeRoute.TradeType;
import cn.ykcryobs.vg.villageSystem.economy.trader.ITrader;
import cn.ykcryobs.vg.villageSystem.economy.trader.TraderSnapshot;
import cn.ykcryobs.vg.villageSystem.economy.trader.sellerFilter.ISellerFilter;
import com.mojang.logging.LogUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

/**
 * 交易管理器，负责处理交易请求，包括筛选卖家、计算交易税、执行交易等。
 *
 * <p><b>线程安全：</b>所有对游戏对象的操作都在主线程上执行，
 * 评分计算等纯计算任务可以异步执行。</p>
 *
 * @author llykff
 */
public class TransactionManager {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static Executor TRANSACTION_EXECUTOR;
    private static List<ISellerFilter> filters;
    private static TradeTaxCalculator taxCalculator;

    private TransactionManager() {
    }

    /**
     * 初始化交易管理器
     */
    public static void setup() {
        int threadCount = ServerConfig.transactionThreadCount.getAsInt();

        TRANSACTION_EXECUTOR = Executors.newFixedThreadPool(threadCount);

        filters = ModRegistries.SELLER_FILTER_REGISTRY.stream()
                .sorted(Comparator.comparingInt(ISellerFilter::getPriority)).toList();
        taxCalculator = new TradeTaxCalculator();

        LOGGER.info("TransactionManager initialized with {} threads and {} filters", threadCount,
                filters.size());
    }

    /**
     * 尝试以任何交易类型购买物品
     *
     * @param item     要购买的物品
     * @param amount   要购买的物品数量
     * @param buyer    购买者
     * @param callback 交易结果回调，第一个参数为交易结果，第二个参数为实际购买的物品数量
     */
    public static void tryToBuy(Item item, int amount, ITrader buyer,
            BiConsumer<TransactionResult, Integer> callback) {
        tryToBuyWithType(item, amount, buyer, TradeType.ANY, callback);
    }

    /**
     * 尝试以指定交易类型购买物品
     *
     * @param item          要购买的物品
     * @param amount        要购买的物品数量
     * @param buyer         购买者
     * @param preferredType 首选交易类型
     * @param callback      交易结果回调，第一个参数为交易结果，第二个参数为实际购买的物品数量
     */
    public static void tryToBuyWithType(Item item, int amount, ITrader buyer, TradeType preferredType,
            BiConsumer<TransactionResult, Integer> callback) {
        MinecraftServer server = VillageGenesis.getLevel().getServer();
        server.execute(() -> {
            tryToBuyOnMainThread(item, amount, buyer, preferredType, callback);
        });
    }

    /**
     * 在主线程上执行购买逻辑
     *
     * @param item          要购买的物品
     * @param amount        要购买的物品数量
     * @param buyer         购买者
     * @param preferredType 首选交易类型
     * @param callback      交易结果回调
     */
    private static void tryToBuyOnMainThread(Item item, int amount, ITrader buyer, TradeType preferredType,
            BiConsumer<TransactionResult, Integer> callback) {
        LOGGER.debug(
                "TransactionManager: Starting purchase on main thread: item={}, amount={}, buyer={}, type={}",
                item, amount, buyer.getTraderId(), preferredType);

        ITradableItem tradableItem = (ITradableItem) item;
        if (!tradableItem.isTradable()) {
            LOGGER.warn("TransactionManager: Try to buy an untradable item {} from trader {}", item,
                    buyer.getTraderId());
            callback.accept(TransactionResult.INVALID_ITEM, 0);
            return;
        }

        TransactionContext context = buildTransactionContext(item, amount, buyer, preferredType);

        if (context.getSellerSnapshots().isEmpty()) {
            LOGGER.info("TransactionManager: No sellers found for item={}", item);
            callback.accept(TransactionResult.NO_SELLER, 0);
            return;
        }

        CompletableFuture<UUID> bestSellerFuture = selectBestSellerAsync(context);

        bestSellerFuture.thenAccept(bestSellerId -> {
            if (bestSellerId == null) {
                LOGGER.info("TransactionManager: No suitable seller found for item={}", item);
                callback.accept(TransactionResult.NO_SELLER, 0);
                return;
            }

            executeTransactionOnMainThread(context, bestSellerId, amount, callback);
        });
    }

    /**
     * 构建交易上下文（在主线程上执行）
     *
     * @param item          交易物品
     * @param amount        请求数量
     * @param buyer         购买者
     * @param preferredType 首选交易类型
     * @return 交易上下文
     */
    private static TransactionContext buildTransactionContext(Item item, int amount, ITrader buyer,
            TradeType preferredType) {
        Set<ITrader> sellers = VillageManager.findItemSeller(item);

        TransactionContext.Builder builder = TransactionContext.builder().item(item).requestedAmount(amount)
                .buyer(buyer).preferredType(preferredType);

        for (ITrader seller : sellers) {
            builder.addSeller(seller);
        }

        return builder.build();
    }

    /**
     * 异步选择最佳卖家
     *
     * @param context 交易上下文
     * @return 最佳卖家ID的CompletableFuture
     */
    private static CompletableFuture<UUID> selectBestSellerAsync(TransactionContext context) {
        return CompletableFuture.supplyAsync(() -> {
            List<TraderSnapshot> sellers = context.getSellerSnapshots();
            ITradableItem item = context.getTradableItem();
            int requestedAmount = context.getRequestedAmount();
            TradeType preferredType = context.getPreferredType();

            Map<UUID, Double> scoredSellers = new HashMap<>();
            filters.forEach(filter -> {
                Map<UUID, Double> scores = filter.normalizedScores(sellers, item, requestedAmount,
                        preferredType, taxCalculator);
                MapUtils.merge(scores, scoredSellers, Double::sum);
            });

            if (scoredSellers.isEmpty()) {
                LOGGER.info("TransactionManager: No sellers passed all filters for item={}", item);
                return null;
            }

            Map.Entry<UUID, Double> bestSeller = scoredSellers.entrySet().stream()
                    .max(Comparator.comparingDouble(Map.Entry::getValue)).get();
            LOGGER.debug("TransactionManager: Selected best seller {} with totalScore={}",
                    bestSeller.getKey(), bestSeller.getValue());

            return bestSeller.getKey();
        }, TRANSACTION_EXECUTOR);
    }

    /**
     * 在主线程上执行交易
     *
     * @param context      交易上下文
     * @param bestSellerId 最佳卖家ID
     * @param amount       请求数量
     * @param callback     回调函数
     */
    private static void executeTransactionOnMainThread(TransactionContext context, UUID bestSellerId,
            int amount, BiConsumer<TransactionResult, Integer> callback) {
        TraderSnapshot sellerSnapshot = context.getSellerSnapshots().stream()
                .filter(s -> s.getTraderId().equals(bestSellerId)).findFirst().orElse(null);

        if (sellerSnapshot == null) {
            LOGGER.warn("TransactionManager: Seller snapshot not found for id={}", bestSellerId);
            callback.accept(TransactionResult.NO_SELLER, 0);
            return;
        }

        ITrader buyer = context.getBuyer();
        ITrader seller = context.getSeller(bestSellerId);

        if (seller == null) {
            LOGGER.warn("TransactionManager: Seller not found for id={}", bestSellerId);
            callback.accept(TransactionResult.NO_SELLER, 0);
            return;
        }

        ITradableItem tradableItem = context.getTradableItem();

        ValidationResult validation = validateTransaction(buyer, seller, sellerSnapshot, tradableItem,
                amount);
        if (!validation.valid()) {
            LOGGER.info("TransactionManager: Transaction validation failed: {}", validation.reason());
            callback.accept(validation.result(), 0);
            return;
        }

        int actualAmount = validation.actualAmount();
        ItemStack actualStack = new ItemStack(context.getItem(), actualAmount);

        float totalWorkPoint = sellerSnapshot.getUnitWorkPoint() * actualAmount;

        IPayment payment = negotiatePayment(buyer, seller, totalWorkPoint);

        if (payment == null) {
            LOGGER.info("TransactionManager: No compatible payment method between buyer and seller");
            callback.accept(TransactionResult.NO_PAYMENT_METHOD, 0);
            return;
        }

        Transaction transaction = new Transaction(buyer, seller, actualStack, payment);
        TransactionResult result = transaction.execute();

        if (result == TransactionResult.SUCCESS) {
            result = actualAmount < amount ? TransactionResult.PARTIAL_SUCCESS : TransactionResult.SUCCESS;
        }

        LOGGER.debug("Transaction completed: result={}, actualAmount={}", result, actualAmount);

        if (result == TransactionResult.PARTIAL_SUCCESS) {
            int remainingAmount = amount - actualAmount;
            LOGGER.debug("Partial purchase completed, remaining={}, continuing purchase", remainingAmount);
            tryToBuyWithType(context.getItem(), remainingAmount, buyer, context.getPreferredType(),
                    (innerResult, innerActualAmount) -> {
                        int totalAmount = actualAmount + innerActualAmount;
                        if (innerResult == TransactionResult.SUCCESS) {
                            callback.accept(TransactionResult.SUCCESS, totalAmount);
                        } else {
                            callback.accept(TransactionResult.PARTIAL_SUCCESS, totalAmount);
                        }
                    });
        } else {
            callback.accept(result, actualAmount);
        }
    }

    /**
     * 验证交易有效性
     *
     * @param buyer           购买者
     * @param seller          卖家
     * @param sellerSnapshot  卖家快照
     * @param item            交易物品
     * @param requestedAmount 请求数量
     * @return 验证结果
     */
    private static ValidationResult validateTransaction(ITrader buyer, ITrader seller,
            TraderSnapshot sellerSnapshot, ITradableItem item, int requestedAmount) {
        int currentAvailable = seller.getAvailableItemCount(item);
        if (currentAvailable <= 0) {
            return new ValidationResult(false, TransactionResult.NO_ITEM, 0, "Seller has no available items");
        }

        int actualAmount = Math.min(requestedAmount, currentAvailable);
        if (actualAmount <= 0) {
            return new ValidationResult(false, TransactionResult.NO_ITEM, 0, "No items available");
        }

        float totalWorkPoint = sellerSnapshot.getUnitWorkPoint() * actualAmount;
        PaymentMethod paymentMethod = buyer.getSupportedPaymentMethods().stream()
                .filter(seller.getSupportedPaymentMethods()::contains).findFirst().orElse(null);

        if (paymentMethod == null) {
            return new ValidationResult(false, TransactionResult.NO_PAYMENT_METHOD, 0,
                    "No compatible payment method");
        }

        IPayment payment = PaymentMethod.fromId(paymentMethod);
        if (payment == null || !payment.canPay(buyer, seller)) {
            return new ValidationResult(false, TransactionResult.INSUFFICIENT_FUNDS, 0, "Insufficient funds");
        }

        return new ValidationResult(true, TransactionResult.SUCCESS, actualAmount, "Validation passed");
    }

    /**
     * 协商支付方式
     *
     * @param buyer          购买者
     * @param seller         卖家
     * @param totalWorkPoint 总工分
     * @return 支付对象
     */
    private static IPayment negotiatePayment(ITrader buyer, ITrader seller, float totalWorkPoint) {
        // TODO: 实现支付方式协商逻辑
        PaymentMethod paymentMethod = buyer.getSupportedPaymentMethods().stream()
                .filter(seller.getSupportedPaymentMethods()::contains).findFirst().orElse(null);

        if (paymentMethod == null) {
            return null;
        }

        IPayment payment = PaymentMethod.fromId(paymentMethod);
        if (payment != null) {
            return payment.createPayment(buyer, seller, totalWorkPoint) ? payment : null;
        }

        return null;
    }

    /**
     * 获取交易税计算器
     *
     * @return 交易税计算器
     */
    public static TradeTaxCalculator getTaxCalculator() {
        return taxCalculator;
    }

    /**
     * 获取过滤器列表
     *
     * @return 过滤器列表
     */
    public static List<ISellerFilter> getFilters() {
        return filters;
    }

    /**
     * 获取交易执行器
     * 
     * <p>用于异步执行交易相关的计算任务。</p>
     *
     * @return 交易执行器实例
     */
    public static Executor getExecutor() {
        return TRANSACTION_EXECUTOR;
    }

    /**
     * 验证结果内部类
     */
    private record ValidationResult(boolean valid, TransactionResult result, int actualAmount,
                                    String reason) {

    }
}
