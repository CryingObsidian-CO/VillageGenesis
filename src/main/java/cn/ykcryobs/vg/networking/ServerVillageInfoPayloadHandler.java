package cn.ykcryobs.vg.networking;

import cn.ykcryobs.vg.client.screen.VillageInfoPanelScreen;
import cn.ykcryobs.vg.networking.payload.VillageBaseInfoPayload;
import cn.ykcryobs.vg.networking.payload.VillageInfoRequestPayload;
import cn.ykcryobs.vg.villageSystem.VillageData;
import cn.ykcryobs.vg.villageSystem.VillageManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * 服务器端村庄信息包处理器
 *
 * @author llykff
 */
public class ServerVillageInfoPayloadHandler {

    public static void handleVillageBaseInfo(final VillageBaseInfoPayload data,
            final IPayloadContext context) {

    }

    public static void handleVillageInfoRequest(final VillageInfoRequestPayload data,
            final IPayloadContext context) {

        UUID villageId = UUID.fromString(data.villageId());

        switch (VillageInfoPanelScreen.VillageInfoCategory.valueOf(data.infoCategory())) {
            case VillageInfoPanelScreen.VillageInfoCategory.BASIC_INFO -> {
                Optional<VillageData> villageDataOptional = VillageManager.getVillageData(villageId);
                if (villageDataOptional.isEmpty()) {
                    // TODO 发报错包
                    return;
                }
                VillageData villageData = villageDataOptional.get();
                PacketDistributor.sendToPlayer((ServerPlayer) context.player(),
                        getVillageBaseInfoPayload(villageData));
            }
            case VillageInfoPanelScreen.VillageInfoCategory.VILLAGER_LIST -> {
                // TODO 发村民列表包
            }
            case VillageInfoPanelScreen.VillageInfoCategory.FACILITY_LIST -> {
                // TODO 发设施信息包
            }
            default -> {
                // TODO 继续完善别的
            }
        }
    }

    private static @NotNull VillageBaseInfoPayload getVillageBaseInfoPayload(VillageData villageData) {
        return new VillageBaseInfoPayload(villageData.getVillageName(), villageData.getVillageLevel(),
                villageData.getVillageExp(), villageData.getRequiredExpForNextLevel(),
                villageData.getPopulation(), villageData.getCreatedTime(),
                villageData.getStatus().getDisplayName(),
                villageData.getFacilityManager().getFacilityCount());
    }

}
