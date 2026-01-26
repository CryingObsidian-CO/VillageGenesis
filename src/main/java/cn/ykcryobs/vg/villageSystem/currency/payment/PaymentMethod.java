package cn.ykcryobs.vg.villageSystem.currency.payment;

/**
 * 支付方式枚举 定义不同的支付方式
 *
 * @author llykff
 */
public enum PaymentMethod {
    /**
     * 物物交换
     */
    BARTER("barter", "物物交换"),

    /**
     * 实物货币支付
     */
    COMMODITY_PAYMENT("commodity_payment", "实物货币支付"),

    /**
     * 金属货币支付
     */
    METAL_PAYMENT("metal_payment", "金属货币支付"),

    /**
     * 纸币支付
     */
    PAPER_PAYMENT("paper_payment", "纸币支付"),

    /**
     * 电子货币支付
     */
    ELECTRONIC_PAYMENT("electronic_payment", "电子货币支付"),

    /**
     * 混合支付（多种货币组合）
     */
    MIXED_PAYMENT("mixed_payment", "混合支付");

    private final String id;
    private final String name;

    /**
     * 构造函数
     *
     * @param id   支付方式ID
     * @param name 支付方式名称
     */
    PaymentMethod(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * 根据支付方式枚举值获取对应的支付方式实现类实例
     *
     * @param method 支付方式枚举值
     * @return 对应的支付方式实现类实例
     */
    public static IPayment fromId(PaymentMethod method) {
        return switch (method) {
            case BARTER -> new BarterPayment();
            default -> null;
        };
    }

    /**
     * 获取支付方式ID
     *
     * @return 支付方式ID
     */
    public String getId() {
        return this.id;
    }

    /**
     * 获取支付方式名称
     *
     * @return 支付方式名称
     */
    public String getName() {
        return this.name;
    }
}