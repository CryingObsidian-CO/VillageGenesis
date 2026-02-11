package cn.ykcryobs.vg.villageSystem.economy.transaction;

import cn.ykcryobs.vg.config.ServerConfig;
import cn.ykcryobs.vg.item.ITradableItem;
import cn.ykcryobs.vg.villageSystem.VillageManager;
import cn.ykcryobs.vg.villageSystem.economy.payment.IPayment;
import cn.ykcryobs.vg.villageSystem.economy.payment.PaymentMethod;
import cn.ykcryobs.vg.villageSystem.economy.trader.ITrader;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

/**
 * 交易管理器
 *
 * @author llykff
 */
public class TransactionManager {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static Executor TRANSACTION_EXECUTOR;

    private TransactionManager() {
    }

    public static void setup() {
        int threadCount = ServerConfig.transactionThreadCount.getAsInt();
        TRANSACTION_EXECUTOR = Executors.newFixedThreadPool(threadCount);
        LOGGER.info("TransactionManager initialized with {} threads", threadCount);
    }

    /**
     * 异步购买物品
     *
     * @param item     物品类型
     * @param amount   购买数量
     * @param buyer    买家
     * @param callback 交易结果回调 (result, actualAmount) - 实际成交数量
     */
    public static void tryToBuy(Item item, int amount, ITrader buyer,
            BiConsumer<TransactionResult, Integer> callback) {
        tryToBuyInternal(item, amount, buyer).whenComplete((wrapper, throwable) -> {
            if (throwable != null) {
                LOGGER.error("Transaction failed with exception", throwable);
                callback.accept(TransactionResult.FAIL, 0);
            } else if (wrapper.result() == TransactionResult.PARTIAL_SUCCESS) {
                int remainingAmount = amount - wrapper.actualAmount();
                LOGGER.debug("Partial purchase completed, remaining={}, continuing purchase",
                        remainingAmount);
                tryToBuy(item, remainingAmount, buyer, (innerResult, innerActualAmount) -> {
                    int totalAmount = wrapper.actualAmount() + innerActualAmount;
                    if (innerResult == TransactionResult.SUCCESS) {
                        callback.accept(innerResult, totalAmount);
                    } else {
                        callback.accept(TransactionResult.PARTIAL_SUCCESS, totalAmount);
                    }
                });
            } else {
                callback.accept(wrapper.result(), wrapper.actualAmount());
            }
        });
    }

    /**
     * 异步购买物品内部实现
     *
     * @param item   物品类型
     * @param amount 购买数量
     * @param buyer  买家
     * @return CompletableFuture 用于链式操作
     */
    private static CompletableFuture<TransactionResult.TransactionResultWrapper> tryToBuyInternal(Item item,
            int amount, ITrader buyer) {
        return CompletableFuture.supplyAsync(() -> {
            LOGGER.debug("TransactionManager: Starting async purchase: item={}, amount={}, buyer={}", item,
                    amount, buyer.getTraderId());
            ITradableItem tradeableItem = (ITradableItem) item;
            if (!(tradeableItem.isTradable())) {
                LOGGER.error("TransactionManager: Try to buy an untradable item {} from trader {}", item,
                        buyer.getTraderId());
                return new TransactionResult.TransactionResultWrapper(TransactionResult.INVALID_ITEM, 0);
            }

            Optional<ITrader> sellerOptional = VillageManager.findItemSeller(item).stream()
                    .min((a, b) -> Float.compare(a.getUnitWorkPoint(tradeableItem),
                            b.getUnitWorkPoint(tradeableItem)));

            if (sellerOptional.isEmpty()) {
                LOGGER.debug("TransactionManager: No available seller found for item={}", item);
                return new TransactionResult.TransactionResultWrapper(TransactionResult.NO_SELLER, 0);
            }

            ITrader seller = sellerOptional.get();

            int actualAmount = Math.min(amount, seller.getAvailableItemCount(tradeableItem));
            if (actualAmount <= 0) {
                LOGGER.warn("TransactionManager: Seller {} has no available item {}", seller.getTraderId(),
                        item);
                return new TransactionResult.TransactionResultWrapper(TransactionResult.NO_ITEM, 0);
            } else if (actualAmount < amount) {
                LOGGER.debug("TransactionManager: Partial purchase: requested={}, available={}", amount,
                        actualAmount);
            }

            ItemStack actualStack = new ItemStack(item, actualAmount);
            IPayment payment = negotiatePayment(buyer, seller,
                    seller.getUnitWorkPoint(tradeableItem) * actualAmount);

            if (payment == null) {
                LOGGER.debug("TransactionManager: No compatible payment method between buyer and seller");
                return new TransactionResult.TransactionResultWrapper(TransactionResult.NO_PAYMENT_METHOD, 0);
            }

            Transaction transaction = new Transaction(buyer, seller, actualStack, payment);
            TransactionResult result = transaction.execute();

            LOGGER.debug("Transaction completed: result={}, actualAmount={}", result, actualAmount);
            return new TransactionResult.TransactionResultWrapper(result, actualAmount);
        }, TRANSACTION_EXECUTOR);
    }

    /**
     * 协商支付方式
     *
     * @param buyer          买家
     * @param seller         卖家
     * @param totalWorkPoint 总工作点
     * @return 支付方式
     */
    private static IPayment negotiatePayment(ITrader buyer, ITrader seller, float totalWorkPoint) {
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
}