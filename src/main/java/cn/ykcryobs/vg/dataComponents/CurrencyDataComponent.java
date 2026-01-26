package cn.ykcryobs.vg.dataComponents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

/**
 * 货币数据组件 用于存储货币的发行村庄ID和发行建筑ID
 *
 * @author llykff
 */
public record CurrencyDataComponent(UUID issuingVillageId, UUID issuingBuildingId) {

    /**
     * UUID编解码器
     */
    private static final Codec<UUID> UUID_CODEC = Codec.STRING.xmap(UUID::fromString, UUID::toString);

    /**
     * 编解码器，用于序列化和反序列化货币数据组件
     */
    public static final Codec<CurrencyDataComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            UUID_CODEC.fieldOf("issuingVillageId").forGetter(CurrencyDataComponent::issuingVillageId),
                            UUID_CODEC.fieldOf("issuingBuildingId")
                                    .forGetter(CurrencyDataComponent::issuingBuildingId))
                    .apply(instance, CurrencyDataComponent::new));

    /**
     * UUID流编解码器
     */
    private static final StreamCodec<ByteBuf, UUID> UUID_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, UUID::toString, UUID::fromString);

    /**
     * 网络流编解码器，用于网络传输
     */
    public static final StreamCodec<ByteBuf, CurrencyDataComponent> STREAM_CODEC = StreamCodec.composite(
            UUID_STREAM_CODEC, CurrencyDataComponent::issuingVillageId, UUID_STREAM_CODEC,
            CurrencyDataComponent::issuingBuildingId, CurrencyDataComponent::new);

    /**
     * 创建货币数据组件的静态方法
     *
     * @param issuingVillageId  发行村庄ID
     * @param issuingBuildingId 发行建筑ID
     * @return 货币数据组件实例
     */
    public static CurrencyDataComponent of(UUID issuingVillageId, UUID issuingBuildingId) {
        return new CurrencyDataComponent(issuingVillageId, issuingBuildingId);
    }
}
