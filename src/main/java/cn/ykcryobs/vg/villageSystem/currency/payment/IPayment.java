package cn.ykcryobs.vg.villageSystem.currency.payment;

import cn.ykcryobs.vg.villageSystem.currency.ITrader;

/**
 * 支付接口 用于表示交易中的支付方式和支付者
 *
 * @author llykff
 */
public interface IPayment {

    /**
     * 完整的支付合法性校验
     *
     * @param payer 支付方（买方）
     * @param payee 收款方（卖方）
     * @return true=支付合法（资产充足+双方兼容），false=不合法
     */
    boolean canPay(ITrader payer, ITrader payee);

    /**
     * 从交易者扣除支付金额/物品
     *
     * @param ITrader 交易者
     */
    void deductFrom(ITrader ITrader);

    /**
     * 给交易者增加支付金额/物品
     *
     * @param ITrader 交易者
     */
    void addTo(ITrader ITrader);

    /**
     * 获取要支付的工分总价值
     *
     * @return 支付的工分总价值
     */
    double getTotalWorkPoints();

    /**
     * 获取支付使用的支付方法
     *
     * @return 支付使用的支付方法
     */
    PaymentMethod getPaymentMethod();

    /**
     * 初始化支付对象
     *
     * @param payer          支付方（买方）
     * @param payee          收款方（卖方）
     * @param totalWorkPoint 支付的工分总价值
     */
    void createPayment(ITrader payer, ITrader payee, float totalWorkPoint);
}
