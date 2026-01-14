package cn.ykcryobs.vg.networking.payload;

import cn.ykcryobs.vg.VillageGenesis;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * 用于服务器向客户端发送村庄基础信息
 *
 * @author llykff
 */
public record VillageBaseInfoPayload(Component villageName, int villageLevel, int villageExp,
                                     int nextLevelExp, int population, long createdTime,
                                     Component villageStatus, int facilityCount) implements
        CustomPacketPayload {

    public static final CustomPacketPayload.Type<VillageBaseInfoPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(VillageGenesis.MOD_ID, "village_base_info"));


    public static final StreamCodec<RegistryFriendlyByteBuf, VillageBaseInfoPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(@NotNull RegistryFriendlyByteBuf byteBuf,
                @NotNull VillageBaseInfoPayload villageBaseInfoPayload) {
            ComponentSerialization.STREAM_CODEC.encode(byteBuf, villageBaseInfoPayload.villageName());
            ByteBufCodecs.VAR_INT.encode(byteBuf, villageBaseInfoPayload.villageLevel());
            ByteBufCodecs.VAR_INT.encode(byteBuf, villageBaseInfoPayload.villageExp());
            ByteBufCodecs.VAR_INT.encode(byteBuf, villageBaseInfoPayload.nextLevelExp());
            ByteBufCodecs.VAR_INT.encode(byteBuf, villageBaseInfoPayload.population());
            ByteBufCodecs.VAR_LONG.encode(byteBuf, villageBaseInfoPayload.createdTime());
            ComponentSerialization.STREAM_CODEC.encode(byteBuf, villageBaseInfoPayload.villageStatus());
            ByteBufCodecs.VAR_INT.encode(byteBuf, villageBaseInfoPayload.facilityCount());
        }

        @Override
        public @NotNull VillageBaseInfoPayload decode(@NotNull RegistryFriendlyByteBuf byteBuf) {
            Component villageName = ComponentSerialization.STREAM_CODEC.decode(byteBuf);
            int villageLevel = ByteBufCodecs.VAR_INT.decode(byteBuf);
            int villageExp = ByteBufCodecs.VAR_INT.decode(byteBuf);
            int nextLevelExp = ByteBufCodecs.VAR_INT.decode(byteBuf);
            int population = ByteBufCodecs.VAR_INT.decode(byteBuf);
            long createdTime = ByteBufCodecs.VAR_LONG.decode(byteBuf);
            Component villageStatus = ComponentSerialization.STREAM_CODEC.decode(byteBuf);
            int facilityCount = ByteBufCodecs.VAR_INT.decode(byteBuf);
            return new VillageBaseInfoPayload(villageName, villageLevel, villageExp, nextLevelExp, population,
                    createdTime, villageStatus, facilityCount);
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
