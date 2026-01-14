package cn.ykcryobs.vg.client.networking;

import cn.ykcryobs.vg.client.screen.VillageInfoPanelScreen;
import cn.ykcryobs.vg.networking.payload.VillageBaseInfoPayload;
import cn.ykcryobs.vg.networking.payload.VillageInfoRequestPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 客户端村庄信息包处理器
 *
 * @author llykff
 */
@OnlyIn(Dist.CLIENT)
public class ClientVillageInfoPayloadHandler {

    public static void handleVillageInfoRequest(final VillageInfoRequestPayload data,
            final IPayloadContext context) {

    }

    public static void handleVillageBaseInfo(final VillageBaseInfoPayload data,
            final IPayloadContext context) {
        if (Minecraft.getInstance().screen instanceof VillageInfoPanelScreen screen) {
            screen.clearInfoWidget();
            screen.addInfo(Component.translatable("village_info_panel.village_genesis.village_name",
                    data.villageName().getString()));
            screen.addInfo(Component.translatable("village_info_panel.village_genesis.village_level",
                    data.villageLevel()));
            screen.addInfo(Component.translatable("village_info_panel.village_genesis.village_exp",
                    data.villageExp()));
            screen.addInfo(Component.translatable("village_info_panel.village_genesis.village_next_exp",
                    data.nextLevelExp()));
            screen.addInfo(Component.translatable("village_info_panel.village_genesis.village_population",
                    data.population()));
            screen.addInfo(Component.translatable("village_info_panel.village_genesis.village_status",
                    data.villageStatus()));
            screen.addInfo(Component.translatable("village_info_panel.village_genesis.facility_count",
                    data.facilityCount()));
            screen.refreshDisplayInfo();
        }
    }

}
