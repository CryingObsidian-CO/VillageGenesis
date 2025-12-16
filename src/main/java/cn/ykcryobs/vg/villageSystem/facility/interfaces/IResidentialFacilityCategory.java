package cn.ykcryobs.vg.villageSystem.facility.interfaces;

/**
 * @author llykff
 */
public interface IResidentialFacilityCategory extends IFacilityCategory {

    /**
     * 获取住宅设施分类的名称
     *
     * @return 住宅设施分类的名称
     */
    @Override
    default String getFacilityCategory() {
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
