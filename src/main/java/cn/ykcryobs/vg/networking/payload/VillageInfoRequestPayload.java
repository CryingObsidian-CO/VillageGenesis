package cn.ykcryobs.vg.networking.payload;

import cn.ykcryobs.vg.VillageGenesis;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * 村庄信息请求包
 *
 * @author llykff
 */
public record VillageInfoRequestPayload(String infoCategory, String villageId) implements
        CustomPacketPayload {

    public static final CustomPacketPayload.Type<VillageInfoRequestPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(VillageGenesis.MOD_ID, "village_info"));

    public static final StreamCodec<ByteBuf, VillageInfoRequestPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, VillageInfoRequestPayload::infoCategory, ByteBufCodecs.STRING_UTF8,
            VillageInfoRequestPayload::villageId, VillageInfoRequestPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
