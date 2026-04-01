package cn.ykcryobs.vg.villageSystem.economy.tradeRoute;

/**
 * 村庄关系类型
 *
 * @author llykff
 */
public enum TradeRelationType {

    /**
     * 敌对关系 - 交易被禁止或极高的税率
     */
    HOSTILE(0, 20, 0.5f, false, "hostile"),

    /**
     * 冷淡关系 - 税率较高
     */
    COLD(20, 40, 0.25f, true, "cold"),

    /**
     * 中立关系 - 标准税率
     */
    NEUTRAL(40, 60, 0.0f, true, "neutral"),

    /**
     * 友好关系 - 税率较低
     */
    FRIENDLY(60, 80, -0.1f, true, "friendly"),

    /**
     * 结盟关系 - 税率较低
     */
    ALLIED(80, 100, -0.2f, true, "allied");

    private final int minRelationLevel;
    private final int maxRelationLevel;
    private final float taxModifier;
    private final boolean canTrade;
    private final String id;

    TradeRelationType(int minRelationLevel, int maxRelationLevel, float taxModifier, boolean canTrade,
            String id) {
        this.minRelationLevel = minRelationLevel;
        this.maxRelationLevel = maxRelationLevel;
        this.taxModifier = taxModifier;
        this.canTrade = canTrade;
        this.id = id;
    }

    public static TradeRelationType fromRelationLevel(int relationLevel) {
        relationLevel = Math.max(0, Math.min(100, relationLevel));
        for (TradeRelationType type : values()) {
            if (relationLevel >= type.minRelationLevel && relationLevel < type.maxRelationLevel) {
                return type;
            }
        }
        return ALLIED;
    }

    public int getMinRelationLevel() {
        return this.minRelationLevel;
    }

    public int getMaxRelationLevel() {
        return this.maxRelationLevel;
    }

    public float getTaxModifier() {
        return this.taxModifier;
    }

    public boolean canTrade() {
        return this.canTrade;
    }

    public String getId() {
        return this.id;
    }

    public TradeRelationType getNextBetterType() {
        int ordinal = this.ordinal();
        if (ordinal < values().length - 1) {
            return values()[ordinal + 1];
        }
        return this;
    }

    public TradeRelationType getNextWorseType() {
        int ordinal = this.ordinal();
        if (ordinal > 0) {
            return values()[ordinal - 1];
        }
        return this;
    }
}
