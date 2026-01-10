package cn.ykcryobs.vg.villageSystem.facility.types;

import cn.ykcryobs.vg.villageSystem.facility.types.interfaces.IInfrastructureCategory;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * 村庄中心设施类型
 *
 * @author llykff
 */
public class VillageCenterType extends FacilityType implements IInfrastructureCategory {

    public static final MapCodec<VillageCenterType> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            Codec.STRING.fieldOf("type_identifier")
                                    .forGetter(VillageCenterType::getFacilityTypeIdentifier),
                            Codec.STRING.fieldOf("facility_type").forGetter(VillageCenterType::getFacilityTypeName),
                            Codec.INT.fieldOf("base_capacity").forGetter(VillageCenterType::getBaseCapacity),
                            Codec.INT.fieldOf("max_level").forGetter(VillageCenterType::getMaxLevel),
                            Codec.INT.fieldOf("required_village_level")
                                    .forGetter(VillageCenterType::getRequiredVillageLevel),
                            Codec.INT.fieldOf("build_time").forGetter(VillageCenterType::getBuildTime))
                    .apply(instance, VillageCenterType::new));


    /**
     * 构造函数，初始化设施类型的基本属性
     *
     * @param facilityTypeIdentifier 设施类型标识符（仅作 dispatchCodec 的标识，无意义）
     * @param facilityTypeName       设施类型名称
     * @param baseCapacity           基础容量
     * @param maxLevel               最大等级
     * @param requiredVillageLevel   所需村庄等级
     * @param buildTime              建造时间
     */
    protected VillageCenterType(String facilityTypeIdentifier, String facilityTypeName, int baseCapacity,
            int maxLevel,
            int requiredVillageLevel, int buildTime) {
        super(facilityTypeIdentifier, baseCapacity, maxLevel, requiredVillageLevel, buildTime,
                facilityTypeName, false);
    }

    @Override
    public String getFacilityTypeIdentifier() {
        return "village_center";
    }

}
