package cn.ykcryobs.vg.villageSystem.facility;

import cn.ykcryobs.vg.villageSystem.facility.interfaces.IFacilityType;
import cn.ykcryobs.vg.villageSystem.facility.interfaces.IResidentialFacilityCategory;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * @author llykff
 */
public class ThatchedHutType extends FacilityType implements IResidentialFacilityCategory {

    private static final MapCodec<ThatchedHutType> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            Codec.INT.fieldOf("baseCapacity").forGetter(ThatchedHutType::getBaseCapacity),
                            Codec.INT.fieldOf("maxLevel").forGetter(ThatchedHutType::getMaxLevel),
                            Codec.INT.fieldOf("requiredVillageLevel")
                                    .forGetter(ThatchedHutType::getRequiredVillageLevel),
                            Codec.INT.fieldOf("buildTime").forGetter(ThatchedHutType::getBuildTime),
                            Codec.BOOL.optionalFieldOf("multiResidential", false)
                                    .forGetter(ThatchedHutType::isMultiResidential),
                            Codec.BOOL.optionalFieldOf("canBirthBaby", true)
                                    .forGetter(ThatchedHutType::canBirthBaby))
                    .apply(instance, ThatchedHutType::new));

    static {
        IFacilityType.registerCodec("thatched_hut", CODEC);
    }

    // 是否可以孕育婴儿
    protected boolean canBirthBaby;
    // 是否为多家庭住宅
    protected boolean multiResidential;

    protected ThatchedHutType(int baseCapacity, int maxLevel, int requiredVillageLevel,
            int buildTime, boolean multiResidential, boolean canBirthBaby) {
        super(baseCapacity, maxLevel, requiredVillageLevel, buildTime);
        this.multiResidential = multiResidential;
        this.canBirthBaby = canBirthBaby;
    }

    @Override
    public String getFacilityType() {
        return "thatched_hut";
    }

    @Override
    public boolean isMultiResidential() {
        return this.multiResidential;
    }

    @Override
    public boolean canBirthBaby() {
        return this.canBirthBaby;
    }
}