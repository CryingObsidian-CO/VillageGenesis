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
import cn.ykcryobs.vg.villageSystem.VillageData;
import cn.ykcryobs.vg.villageSystem.economy.transaction.TransactionManager;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
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

    private static ServerLevel level;
    private static IEventBus eventBus;

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

        VillageData.register(modEventBus);
        eventBus = modEventBus;
    }

    /**
     * 获取一个资源定位符（ResourceLocation）
     *
     * @param path 资源路径
     * @return 对应的资源定位符
     */
    // TODO 把现有的 ResourceLocation.fromNamespaceAndPath 替换为这个方法
    public static ResourceLocation getIdentifier(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
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
    public static ServerLevel getLevel() {
        if (level == null) {
            throw new IllegalStateException("Level is not set!");
        }
        return level;
    }

    /**
     * 获取事件总线实例
     *
     * @return 事件总线实例
     */
    public static IEventBus getEventBus() {
        return eventBus;
    }

    public static <T extends Event> void postEvent(T event) {
        eventBus.post(event);
    }

    @SubscribeEvent
    private static void serverStarting(ServerStartingEvent event) {
        level = event.getServer().overworld();
        TransactionManager.setup();
    }

    @SubscribeEvent
    private static void onCommonSetup(FMLCommonSetupEvent event) {
    }
}