package cn.ykcryobs.vg.villageSystem.facility.types.interfaces;

/**
 * 基础设施分类接口
 *
 * @author llykff
 */
public interface IInfrastructureCategory extends IFacilityCategory {

    @Override
    default String getFacilityCategoryName() {
        return "infrastructure";
    }


}
