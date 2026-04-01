package cn.ykcryobs.vg.villageSystem.economy.tradeRoute;

/**
 * 交易类型枚举，区分村庄内交易和村庄间交易
 *
 * @author llykff
 */
public enum TradeType {

    /**
     * 村庄内交易
     */
    IN_VILLAGE,

    /**
     * 村庄间交易
     */
    INTER_VILLAGE,

    /**
     * 自由交易，适用于村民没有所属村庄的情况
     */
    FREE,

    /**
     * 任意交易类型，根据卖家可用性自动选择
     */
    ANY;

    /**
     * 判断是否允许村庄间交易
     *
     * @return true 如果允许村庄间交易
     */
    public boolean allowsInterVillage() {
        return this == INTER_VILLAGE || this == ANY;
    }

    /**
     * 判断是否允许村庄内交易
     *
     * @return true 如果允许村庄内交易
     */
    public boolean allowsInVillage() {
        return this == IN_VILLAGE || this == ANY;
    }
}
