package cn.ykcryobs.vg.villageSystem.facility.types.interfaces;

/**
 * 设施分类接口，定义了设施的基本属性和行为
 *
 * @author llykff
 */
public interface IFacilityCategory {

    /**
     * 获取设施分类的名称
     *
     * @return 设施分类的名称
     */
    String getFacilityCategoryName();

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
     * 获取设施的建造时间（tick）
     *
     * @return 建造时间
     */
    int getBuildTime();

    /**
     * 获取设施的最大耐久度。 注意：如果设施不受耐久度影响，则返回的最大耐久度是不可信的
     *
     * @param level 设施等级
     * @return 最大耐久度
     * @see #isDurabilityAffected()
     */
    int getMaxDurability(int level);


    /**
     * 获取设施升级所需时间（tick）
     *
     * @return 升级时间
     */
    default int getUpgradeTime() {
        return this.getBuildTime() / 2;
    }

    /**
     * 高等级村庄是否可以建造此设施
     *
     * @return 是否可以在高等级建造
     */
    default boolean isAvailableAtHighLevel() {
        return false;
    }

    /**
     * 是否受到耐久度影响
     *
     * @return 是否受到耐久度影响
     */
    default boolean isDurabilityAffected() {
        return true;
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
