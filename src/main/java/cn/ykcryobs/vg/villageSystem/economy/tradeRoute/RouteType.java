package cn.ykcryobs.vg.villageSystem.economy.tradeRoute;

/**
 * 贸易路线类型枚举
 *
 * @author llykff
 */
public enum RouteType {

    /**
     * 陆行路线 - 标准速度和成本
     */
    LAND(1.0f, 1.0f, 1.0f),

    /**
     * 水生路线 - 较慢但成本较低
     */
    WATER(0.7f, 1.5f, 0.6f),

    /**
     * 混合路线 - 综合计算
     */
    MIXED(0.85f, 1.25f, 0.9f);

    private final float speedFactor;
    private final float timeFactor;
    private final float costFactor;

    RouteType(float speedFactor, float timeFactor, float costFactor) {
        this.speedFactor = speedFactor;
        this.timeFactor = timeFactor;
        this.costFactor = costFactor;
    }

    public float getSpeedFactor() {
        return this.speedFactor;
    }

    public float getTimeFactor() {
        return this.timeFactor;
    }

    public float getCostFactor() {
        return this.costFactor;
    }

    public float calculateTransportTime(float baseTransportTime) {
        return baseTransportTime * this.timeFactor;
    }

    public float calculateShippingCost(float baseCost) {
        return baseCost * this.costFactor;
    }
}
