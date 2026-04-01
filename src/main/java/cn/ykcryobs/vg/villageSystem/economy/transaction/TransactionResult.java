package cn.ykcryobs.vg.villageSystem.economy.transaction;

/**
 * 交易结果枚举
 *
 * @author llykff
 */
public enum TransactionResult {

    /**
     * 交易完全成功
     */
    SUCCESS,

    /**
     * 部分成功 - 买家请求数量大于卖家可用数量，实际按卖家可用数量成交
     */
    PARTIAL_SUCCESS,

    /**
     * 通用失败 - 未知原因
     */
    FAIL,

    /**
     * 无卖家 - 交易目标不存在
     */
    NO_SELLER,

    /**
     * 资金不足 - 买方支付能力不足，无法完成交易
     */
    INSUFFICIENT_FUNDS,

    /**
     * 无支付方式 - 买卖双方没有共同的支付方式
     */
    NO_PAYMENT_METHOD,

    /**
     * 无效物品 - 商品不可交易
     */
    INVALID_ITEM,

    /**
     * 无商品 - 卖家没有足够的商品
     */
    NO_ITEM,

    /**
     * 交易取消 - 用户主动取消
     */
    CANCELLED,

    /**
     * 无贸易路线 - 村庄之间不存在用于村际贸易的贸易路线
     */
    NO_TRADE_ROUTE;

    /**
     * 交易结果包装类，用于在异步回调中传递结果和实际成交数量
     */
    public record TransactionResultWrapper(TransactionResult result, int actualAmount) {

        public TransactionResultWrapper {
            if ((result == TransactionResult.SUCCESS || result == TransactionResult.PARTIAL_SUCCESS)
                    && actualAmount <= 0) {
                throw new IllegalArgumentException(
                        "actualAmount must be greater than 0 when result is SUCCESS or PARTIAL_SUCCESS");
            }
        }

        public TransactionResultWrapper(TransactionResult result) {
            this(result, 0);
        }
    }
}