package cn.ykcryobs.vg.villageSystem.facility;

import cn.ykcryobs.vg.villageSystem.facility.interfaces.IFacilityCategory;
import cn.ykcryobs.vg.villageSystem.facility.interfaces.IFacilityType;

/**
 * 设施类型接口
 *
 * @author llykff
 */
public abstract class FacilityType implements IFacilityType, IFacilityCategory {

    protected int baseCapacity;
    protected int maxLevel;
    protected int requiredVillageLevel;
    protected int buildTime;

    protected FacilityType(int baseCapacity, int maxLevel, int requiredVillageLevel,
            int buildTime) {
        this.baseCapacity = baseCapacity;
        this.maxLevel = maxLevel;
        this.requiredVillageLevel = requiredVillageLevel;
        this.buildTime = buildTime;
    }

    @Override
    public int getBaseCapacity() {
        return this.baseCapacity;
    }

    @Override
    public int getMaxLevel() {
        return this.maxLevel;
    }

    @Override
    public int getRequiredVillageLevel() {
        return this.requiredVillageLevel;
    }

    @Override
    public int getBuildTime() {
        return this.buildTime;
    }

    // 简化 HashMap 查找
    @Override
    public int hashCode() {
        return getFacilityType().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof IFacilityType other) {
            return getFacilityType().equals(other.getFacilityType());
        }
        return false;
    }

}