package cn.ykcryobs.vg.item.currency;

/**
 * 货币类型枚举 定义不同村庄阶段的货币类型
 *
 * @author llykff
 */
public enum CurrencyType {
    /**
     * 物物交换 - 原始部落阶段
     */
    BARTER("barter", "物物交换", 0),

    /**
     * 实物货币 - 农业村庄阶段
     */
    COMMODITY("commodity", "实物货币", 1),

    /**
     * 金属货币 - 手工业城镇阶段
     */
    METAL("metal", "金属货币", 2),

    /**
     * 纸币 - 商业都市阶段
     */
    PAPER("paper", "纸币", 3),

    /**
     * 电子货币 - 工业城市及以后阶段
     */
    ELECTRONIC("electronic", "电子货币", 4);

    private final String id;
    private final String name;
    private final int level;

    /**
     * 构造函数
     *
     * @param id    货币类型ID
     * @param name  货币类型名称
     * @param level 货币类型等级
     */
    CurrencyType(String id, String name, int level) {
        this.id = id;
        this.name = name;
        this.level = level;
    }

    /**
     * 根据村庄阶段获取对应的货币类型
     *
     * @param evolutionStage 村庄演进阶段
     * @return 对应的货币类型
     */
    public static CurrencyType getCurrencyTypeByStage(
            cn.ykcryobs.vg.villageSystem.VillageData.VillageEvolutionStage evolutionStage) {
        return switch (evolutionStage) {
            case PRIMITIVE -> BARTER;
            case AGRICULTURAL -> COMMODITY;
            case HANDICRAFT -> METAL;
            case COMMERCIAL -> PAPER;
            case INDUSTRIAL, MODERN -> ELECTRONIC;
        };
    }

    /**
     * 获取货币类型ID
     *
     * @return 货币类型ID
     */
    public String getId() {
        return this.id;
    }

    /**
     * 获取货币类型名称
     *
     * @return 货币类型名称
     */
    public String getName() {
        return this.name;
    }

    /**
     * 获取货币类型等级
     *
     * @return 货币类型等级
     */
    public int getLevel() {
        return this.level;
    }
}