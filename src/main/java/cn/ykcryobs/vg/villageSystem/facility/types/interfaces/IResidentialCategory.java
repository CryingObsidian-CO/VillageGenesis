package cn.ykcryobs.vg.villageSystem.facility.types.interfaces;

/**
 * 住宅设施分类接口
 *
 * @author llykff
 */
public interface IResidentialCategory extends IFacilityCategory {

    @Override
    default String getFacilityCategoryName() {
        return "residential";
    }

    /**
     * 是否为多住宅设施分类
     *
     * @return 是否为多住宅设施分类
     */
    boolean isMultiResidential();

    /**
     * 是否能够生产婴儿
     *
     * @return 是否能够生产婴儿
     */
    boolean canBirthBaby();
}
