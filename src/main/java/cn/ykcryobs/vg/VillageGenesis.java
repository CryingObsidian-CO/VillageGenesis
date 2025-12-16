package cn.ykcryobs.vg;

import cn.ykcryobs.vg.config.ClientConfig;
import cn.ykcryobs.vg.config.CommonConfig;
import cn.ykcryobs.vg.config.ServerConfig;
import cn.ykcryobs.vg.init.ModAttachment;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(VillageGenesis.MOD_ID)
public class VillageGenesis {

    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "village_genesis";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public VillageGenesis(IEventBus modEventBus, ModContainer modContainer) {

        // 注册配置文件到ModContainer
        modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);

        ModAttachment.register(modEventBus);

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Common setup completed");
    }
}