package cn.ykcryobs.vg.init;

import cn.ykcryobs.vg.VillageGenesis;
import cn.ykcryobs.vg.villageSystem.VillageManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

/**
 * 数据保存类注册
 *
 * @author llykff
 */

@EventBusSubscriber(modid = VillageGenesis.MOD_ID)
public class ModDataSave {

    @SubscribeEvent
    private static void register(ServerStartingEvent event) {
        ServerLevel overworld = event.getServer().overworld();
        overworld.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(VillageManager::getInstance, VillageManager::load),
                "village_data");
    }
}
