package cn.ykcryobs.vg.villageSystem.facility.interfaces;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * @author llykff
 */
public interface IFacilityType {

    Logger LOGGER = LogUtils.getLogger();
    Map<String, MapCodec<? extends IFacilityType>> SUB_INTERFACE_CODECS = new HashMap<>();
    Codec<IFacilityType> DISPATCH_CODEC = Codec.STRING.partialDispatch("type",
            IFacilityType::getIdentifier, IFacilityType::getCodecByType

    );

    /**
     * 获取设施类型的Codec
     *
     * @param facilityType 设施类型
     * @return 设施类型的Codec
     */
    private static DataResult<? extends MapCodec<? extends IFacilityType>> getCodecByType(
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
    private static DataResult<String> getIdentifier(IFacilityType facilityType) {
        if (facilityType == null) {
            LOGGER.warn("facilityType is null");
            return DataResult.error(() -> "facilityType is null");
        }
        return DataResult.success(facilityType.getFacilityType());
    }

    /**
     * 注册设施类型的Codec
     *
     * @param typeIdentifier 设施类型的标识符
     * @param codec          设施类型的Codec
     */
    static void registerCodec(String typeIdentifier, MapCodec<? extends IFacilityType> codec) {
        SUB_INTERFACE_CODECS.put(typeIdentifier, codec);
    }

    /**
     * 获取设施类型的名称
     *
     * @return 设施类型的名称
     */
    String getFacilityType();

    /**
     * 获取设施类型的显示名称键
     *
     * @return 设施显示名称键
     */
    default Component getDisplayKey() {
        return Component.translatable("village.village_genesis.facility." + this.getFacilityType());
    }

    /**
     * 获取设施类型的描述
     *
     * @return 设施描述
     */
    default Component getDescriptionKey() {
        return Component.translatable(this.getDisplayKey().getString() + ".description");
    }
}
