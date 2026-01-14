package cn.ykcryobs.vg;

import cn.ykcryobs.vg.config.ClientConfig;
import cn.ykcryobs.vg.config.CommonConfig;
import cn.ykcryobs.vg.config.ServerConfig;
import cn.ykcryobs.vg.init.ModAttachment;
import cn.ykcryobs.vg.init.ModBlockEntities;
import cn.ykcryobs.vg.init.ModBlocks;
import cn.ykcryobs.vg.init.ModDataComponents;
import cn.ykcryobs.vg.init.ModItems;
import cn.ykcryobs.vg.init.ModStructurePoolRegistries;
import com.mojang.logging.LogUtils;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

/**
 * 村庄生成模组的主类，负责初始化模组的各项功能
 *
 * @author llykff
 */
@Mod(VillageGenesis.MOD_ID)
@EventBusSubscriber(modid = VillageGenesis.MOD_ID)
public class VillageGenesis {

    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "village_genesis";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public static Level level;

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public VillageGenesis(IEventBus modEventBus, ModContainer modContainer) {

        // 注册配置文件到ModContainer
        modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);

        ModAttachment.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModDataComponents.register(modEventBus);
        ModItems.register(modEventBus);
        ModStructurePoolRegistries.register(modEventBus);
    }

    public static String getModIdentifier(String name) {
        return MOD_ID + ":" + name;
    }

    /**
     * 获取当前游戏时间
     *
     * @return 当前游戏时间（以刻为单位）
     */
    public static long getGameTime() {
        try {
            return getLevel().getGameTime();
        } catch (IllegalStateException e) {
            return 0;
        }
    }

    /**
     * 获取当前世界实例
     *
     * @return 当前世界实例
     * @throws IllegalStateException 如果世界未被设置
     */
    public static Level getLevel() {
        if (level == null) {
            throw new IllegalStateException("Level is not set!");
        }
        return level;
    }

    /**
     * 服务器启动事件处理器，用于初始化世界实例
     *
     * @param event 服务器启动事件
     */
    @SubscribeEvent
    private static void serverStarting(ServerStartingEvent event) {
        level = event.getServer().overworld();
    }

}