package cn.ykcryobs.vg.villageSystem.facility.types;

import cn.ykcryobs.vg.villageSystem.facility.types.interfaces.IResidentialCategory;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

/**
 * 草屋设施类型，代表村庄中的草屋建筑 实现了住宅设施分类接口，具有住宅相关的属性和功能
 *
 * @author llykff
 */
public class ThatchedHutType extends FacilityType implements IResidentialCategory {

    public static final MapCodec<ThatchedHutType> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(Codec.STRING.fieldOf("type_identifier")
                                    .forGetter(ThatchedHutType::getFacilityTypeIdentifier),
                            Codec.STRING.fieldOf("facility_type").forGetter(ThatchedHutType::getFacilityTypeName),
                            Codec.INT.fieldOf("base_capacity").forGetter(ThatchedHutType::getBaseCapacity),
                            Codec.INT.fieldOf("max_level").forGetter(ThatchedHutType::getMaxLevel),
                            Codec.INT.fieldOf("required_village_level")
                                    .forGetter(ThatchedHutType::getRequiredVillageLevel),
                            Codec.INT.fieldOf("build_time").forGetter(ThatchedHutType::getBuildTime),
                            Codec.INT.listOf().fieldOf("max_durability").forGetter(ThatchedHutType::getMaxDurability),
                            Codec.BOOL.optionalFieldOf("multi_residential", false)
                                    .forGetter(ThatchedHutType::isMultiResidential))
                    .apply(instance, ThatchedHutType::new));

    // 是否为多家庭住宅
    protected boolean multiResidential;

    /**
     * 构造函数，创建新的草屋设施类型
     *
     * @param facilityTypeIdentifier 设施类型标识符（仅作 dispatchCodec 的标识，无意义）
     * @param facilityTypeName       设施类型名称
     * @param baseCapacity           基础容量
     * @param maxLevel               最大等级
     * @param requiredVillageLevel   所需村庄等级
     * @param buildTime              建造时间
     * @param multiResidential       是否为多家庭住宅
     */
    protected ThatchedHutType(String facilityTypeIdentifier, String facilityTypeName, int baseCapacity,
            int maxLevel, int requiredVillageLevel, int buildTime, List<Integer> maxDurability,
            boolean multiResidential) {
        super(facilityTypeIdentifier, baseCapacity, maxLevel, requiredVillageLevel, buildTime,
                facilityTypeName, maxDurability);
        this.multiResidential = multiResidential;
    }

    @Override
    public String getFacilityTypeIdentifier() {
        return "thatched_hut";
    }

    @Override
    public boolean isMultiResidential() {
        return this.multiResidential;
    }

    @Override
    public boolean canBirthBaby() {
        return false;
    }

}