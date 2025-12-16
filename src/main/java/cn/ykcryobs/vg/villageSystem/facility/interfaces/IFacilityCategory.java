package cn.ykcryobs.vg.villageSystem.facility.interfaces;

/**
 * @author llykff
 */
public interface IFacilityCategory {

    /**
     * 获取设施分类的名称
     *
     * @return 设施分类的名称
     */
    String getFacilityCategory();

    /**
     * 获取设施的基础容量
     */
    int getBaseCapacity();

    /**
     * 获取设施的最大等级（默认5级）
     */
    int getMaxLevel();

    /**
     * 获取建造此设施需要的村庄等级
     *
     * @return 所需村庄等级
     */
    int getRequiredVillageLevel();

    /**
     * 高等级村庄是否可以建造此设施
     *
     * @return 是否可以在高等级建造
     */
    default boolean isAvailableAtHighLevel() {
        return false;
    }

    /**
     * 获取设施的建造时间（tick）
     *
     * @return 建造时间
     */
    int getBuildTime();

    /**
     * 获取设施升级所需时间（tick）
     *
     * @return 升级时间
     */
    default int getUpgradeTime() {
        return this.getBuildTime() / 2;
    }

    /**
     * 检查设施是否可以升到下一级
     *
     * @param nowLevel     当前等级
     * @param villageLevel 村庄等级
     * @return 是否可以升级
     */
    default boolean canUpgradeNext(int nowLevel, int villageLevel) {
        // 1. 当前等级 < 最大等级
        // 2. 村庄等级 >= 所需等级
        // 3. （村庄等级 == 所需等级） 或 （高等级可用）
        return nowLevel < getMaxLevel() && villageLevel >= this.getRequiredVillageLevel() && (
                villageLevel == this.getRequiredVillageLevel() || this.isAvailableAtHighLevel());
    }
}
