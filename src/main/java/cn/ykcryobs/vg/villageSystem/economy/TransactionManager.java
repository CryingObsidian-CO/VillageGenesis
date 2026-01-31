package cn.ykcryobs.vg.villageSystem.economy;

import cn.ykcryobs.vg.config.ServerConfig;
import cn.ykcryobs.vg.event.InquiryBroadcastEvent;
import cn.ykcryobs.vg.villageSystem.economy.payment.IPayment;
import cn.ykcryobs.vg.villageSystem.economy.payment.PaymentMethod;
import cn.ykcryobs.vg.villageSystem.economy.transaction.Inquiry;
import cn.ykcryobs.vg.villageSystem.economy.transaction.PreliminaryQuote;
import cn.ykcryobs.vg.villageSystem.economy.transaction.QuoteCollector;
import cn.ykcryobs.vg.villageSystem.economy.transaction.Transaction;
import cn.ykcryobs.vg.villageSystem.economy.transaction.TransactionResult;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import org.slf4j.Logger;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 交易管理器
 *
 * @author llykff
 */
public class TransactionManager {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static Executor INQUIRY_EXECUTOR;
    private static IEventBus eventBus;

    private TransactionManager() {
    }

    public static void register(IEventBus eventBus) {
        TransactionManager.eventBus = eventBus;
    }

    public static void setup() {

        INQUIRY_EXECUTOR = Executors.newFixedThreadPool(ServerConfig.transactionThreadCount.getAsInt());
    }

    private static List<PreliminaryQuote> initiateInquiry(Inquiry inquiry, int minRequiredQuotes,
            long timeoutMs) {
        // 创建收集器
        QuoteCollector collector = new QuoteCollector(minRequiredQuotes, timeoutMs);
        // 广播询价事件
        eventBus.post(new InquiryBroadcastEvent(collector, inquiry));
        // 阻塞等待结果
        return collector.await();
    }


    /**
     * 选择最佳卖家
     *
     * @param inquiry 询价单
     * @param quotes  报价单列表
     * @return 最佳卖家的报价单（如果存在）
     */
    // TODO 引入基于村民好感的修正
    private static Optional<PreliminaryQuote> selectBestSeller(Inquiry inquiry,
            List<PreliminaryQuote> quotes) {
        return quotes.stream()
                .filter(quote -> hasCommonPaymentMethod(quote.getSeller().getSupportedPaymentMethods(),
                        inquiry.buyer().getSupportedPaymentMethods()))
                .max(Comparator.comparingDouble(quote -> calculateTotalScore(quote, inquiry.targetAmount())));
    }

    /**
     * 生成交易
     *
     * @param inquiry 询价单
     * @param quote   选中的报价单
     * @return 交易结果
     */
    private static TransactionResult generateTransaction(Inquiry inquiry, PreliminaryQuote quote) {
        int tradeAmount = Math.min(inquiry.targetAmount(), quote.getAvailableAmount());
        IPayment payment = negotiatePayment(inquiry.buyer(), quote.getSeller(),
                tradeAmount * quote.getUnitWorkPoints());
        if (payment == null) {
            LOGGER.warn("Cant negotiate payment for inquiry: {}", inquiry);
            return TransactionResult.FAIL;
        }

        Transaction transaction = new Transaction.Builder().buyer(inquiry.buyer()).seller(quote.getSeller())
                .tradeItem(new ItemStack(inquiry.targetItem(), tradeAmount)).payment(payment).build();

        TransactionResult result = transaction.execute();
        if (!(result == TransactionResult.FAIL)) {
            result = inquiry.targetAmount() == tradeAmount ? TransactionResult.SUCCESS
                    : TransactionResult.PARTIAL_SUCCESS;
        }
        return result;
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

        payment.createPayment(buyer, seller, totalWorkPoint);
        return payment;
    }

    /**
     * 发布询价单
     *
     * @param inquiry 询价单
     * @return 交易结果的CompletableFuture
     */
    // TODO 见面交易得先见面吧
    public static CompletableFuture<TransactionResult> postInquiryAsync(Inquiry inquiry) {
        return postInquiryAsync(inquiry, 5, 5000);
    }

    /**
     * 发布询价单
     *
     * @param inquiry           询价单
     * @param minRequiredQuotes 最小报价数
     * @param timeoutMs         超时时间（毫秒）
     * @return 交易结果的CompletableFuture
     */
    // TODO 见面交易得先见面吧
    public static CompletableFuture<TransactionResult> postInquiryAsync(Inquiry inquiry,
            int minRequiredQuotes, long timeoutMs) {
        CompletableFuture<List<PreliminaryQuote>> quotesFuture = CompletableFuture.supplyAsync(
                () -> initiateInquiry(inquiry, minRequiredQuotes, timeoutMs), INQUIRY_EXECUTOR);
        CompletableFuture<Optional<PreliminaryQuote>> bestQuoteFuture = quotesFuture.thenApplyAsync(
                quotes -> selectBestSeller(inquiry, quotes), INQUIRY_EXECUTOR);

        return bestQuoteFuture.thenComposeAsync(bestQuote -> {
            if (bestQuote.isEmpty()) {
                LOGGER.warn("No suitable seller found for inquiry: {}", inquiry);
                return CompletableFuture.completedFuture(TransactionResult.FAIL);
            }

            PreliminaryQuote selectedQuote = bestQuote.get();
            TransactionResult result = generateTransaction(inquiry, selectedQuote);
            LOGGER.debug("Transaction result: {}", result);

            if (result == TransactionResult.PARTIAL_SUCCESS) {
                Inquiry remainingInquiry = new Inquiry(inquiry.buyer(), inquiry.targetItem(),
                        inquiry.targetAmount() - selectedQuote.getAvailableAmount());

                return postInquiryAsync(remainingInquiry, minRequiredQuotes, timeoutMs).thenApply(
                        recursiveResult -> recursiveResult == TransactionResult.SUCCESS
                                ? TransactionResult.SUCCESS : TransactionResult.PARTIAL_SUCCESS);
            }
            return CompletableFuture.completedFuture(result);
        }, INQUIRY_EXECUTOR);
    }

    /**
     * 检查报价单是否支持目标支付方法
     *
     * @param quoteMethods  报价单支持的支付方法
     * @param targetMethods 目标支付方法
     * @return 如果报价单支持目标支付方法，则返回true；否则返回false
     */
    private static boolean hasCommonPaymentMethod(final Set<PaymentMethod> quoteMethods,
            final Set<PaymentMethod> targetMethods) {
        return quoteMethods.stream().anyMatch(targetMethods::contains);
    }

    private static double calculateTotalScore(PreliminaryQuote quote, int need) {
        float workPoints = quote.getUnitWorkPoints();
        int availableAmount = quote.getAvailableAmount();

        // 规则1 unitWorkPoints（工分越低，得分越高）
        // 规则2 可用数量（越接近need，得分越高）
        double amountScore = 100;
        if (availableAmount < need) {
            amountScore = (double) availableAmount / need * 100;
        }

        // NOTE 对于村民更倾向于多次交易还是更便宜的单价 可以更改计算系数
        double totalScore = (amountScore * 0.4) - (workPoints * 0.7);
        LOGGER.debug("Calculated total score: {}", totalScore);
        return totalScore;
    }
}
