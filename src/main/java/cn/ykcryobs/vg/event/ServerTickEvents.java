package cn.ykcryobs.vg.event;

import cn.ykcryobs.vg.VillageGenesis;
import cn.ykcryobs.vg.villageSystem.VillageManager;
import cn.ykcryobs.vg.villageSystem.interVillage.InterVillageManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = VillageGenesis.MOD_ID)
public class ServerTickEvents {

    @SubscribeEvent
    private static void onServerTick(ServerTickEvent.Post event) {
        VillageManager.getInstance().tick();
        InterVillageManager.getInstance().tick();
    }
}