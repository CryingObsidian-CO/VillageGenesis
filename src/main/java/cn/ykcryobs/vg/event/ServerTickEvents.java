package cn.ykcryobs.vg.event;

import cn.ykcryobs.vg.VillageGenesis;
import cn.ykcryobs.vg.villageSystem.VillageManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * 服务器Tick事件监听器，负责处理服务器每 tick 的逻辑
 *
 * @author llykff
 */
@EventBusSubscriber(modid = VillageGenesis.MOD_ID)
public class ServerTickEvents {

    /**
     * 处理服务器每 tick 事件，调用村庄管理器的 tick 方法
     *
     * @param event 服务器 tick 事件
     */
    @SubscribeEvent
    private static void onServerTick(ServerTickEvent.Post event) {
        VillageManager.getInstance().tick();
    }
}