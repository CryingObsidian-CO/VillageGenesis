package cn.ykcryobs.vg.config;

import cn.ykcryobs.vg.VillageGenesis;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import org.slf4j.Logger;

/**
 * 配置事件监听器，处理配置变更和同步
 *
 * @author llykff
 */
@EventBusSubscriber(modid = VillageGenesis.MOD_ID)
public class ConfigEventListeners {

    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * 监听配置重载事件
     */
    @SubscribeEvent
    public static void onConfigReloading(ModConfigEvent.Reloading event) {
        ModConfig.Type type = event.getConfig().getType();
        switch (type) {
            case COMMON:
                LOGGER.info("Common config is reloading");
                break;
            case SERVER:
                LOGGER.info("Server config is reloading");
                break;
            case CLIENT:
                LOGGER.info("Client config is reloading");
                break;
        }
    }

    /**
     * 监听配置加载事件
     */
    @SubscribeEvent
    public static void onConfigLoading(ModConfigEvent.Loading event) {
        ModConfig.Type type = event.getConfig().getType();
        switch (type) {
            case COMMON:
                LOGGER.info("Common config loaded");
                break;
            case SERVER:
                LOGGER.info("Server config loaded");
                break;
            case CLIENT:
                LOGGER.info("Client config loaded");
                break;
        }
    }
}