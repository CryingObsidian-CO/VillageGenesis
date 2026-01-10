package cn.ykcryobs.vg.villageSystem.facility.types;

import cn.ykcryobs.vg.villageSystem.facility.types.interfaces.IFacilityCategory;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * 设施类型抽象类，实现了IFacilityCategory接口 为各种具体的设施类型提供基础实现
 *
 * @author llykff
 */
public abstract class FacilityType implements IFacilityCategory {

    protected int baseCapacity;
    protected int maxLevel;
    protected int requiredVillageLevel;
    protected int buildTime;
    protected String facilityTypeName;

    /**
     * 构造函数，初始化设施类型的基本属性
     *
     * @param facilityTypeIdentifier 设施类型标识符（仅作 dispatchCodec 的标识，无意义）
     * @param baseCapacity           基础容量
     * @param maxLevel               最大等级
     * @param requiredVillageLevel   所需村庄等级
     * @param buildTime              建造时间
     * @param facilityTypeName       设施名称
     * @param isDurabilityAffected   是否受耐久影响
     */
    protected FacilityType(String facilityTypeIdentifier, int baseCapacity, int maxLevel,
            int requiredVillageLevel, int buildTime, String facilityTypeName, boolean isDurabilityAffected) {
        this.baseCapacity = baseCapacity;
        this.maxLevel = maxLevel;
        this.requiredVillageLevel = requiredVillageLevel;
        this.buildTime = buildTime;
        this.facilityTypeName = facilityTypeName;
    }

    public abstract String getFacilityTypeIdentifier();

    public String getFacilityTypeName() {
        return this.facilityTypeName;
    }

    public void tick() {
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

    @Override
    public int hashCode() {
        return getFacilityTypeIdentifier().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof FacilityType other) {
            return getFacilityTypeIdentifier().equals(other.getFacilityTypeIdentifier());
        }
        return false;
    }

    public static class FacilityTypeCodec {

        private static final Logger LOGGER = LogUtils.getLogger();
        private static final Map<String, MapCodec<? extends FacilityType>> SUB_INTERFACE_CODECS = new HashMap<>();
        public static Codec<FacilityType> DISPATCH_CODEC = Codec.STRING.partialDispatch("type_identifier",
                FacilityTypeCodec::getIdentifier, FacilityTypeCodec::getCodecByType);

        /**
         * 获取设施类型的Codec
         *
         * @param facilityType 设施类型
         * @return 设施类型的Codec
         */
        private static DataResult<? extends MapCodec<? extends FacilityType>> getCodecByType(
                String facilityType) {

            if (SUB_INTERFACE_CODECS.containsKey(facilityType)) {
                return DataResult.success(SUB_INTERFACE_CODECS.get(facilityType));
            }

            LOGGER.warn("No codec found for facility type: {}", facilityType);
            return DataResult.error(() -> "No codec found for facility type: " + facilityType);
        }

        /**
         * 获取设施类型的标识符
         *
         * @param facilityType 设施类型
         * @return 设施类型的标识符
         */
        private static DataResult<String> getIdentifier(FacilityType facilityType) {
            if (facilityType == null) {
                LOGGER.warn("facilityType is null");
                return DataResult.error(() -> "facilityType is null");
            }
            return DataResult.success(facilityType.getFacilityTypeIdentifier());
        }

        /**
         * 注册设施类型的Codec
         *
         * @param typeIdentifier 设施类型的标识符
         * @param codec          设施类型的Codec
         */
        public static void registerCodec(String typeIdentifier, MapCodec<? extends FacilityType> codec) {
            SUB_INTERFACE_CODECS.put(typeIdentifier, codec);
        }
    }
}