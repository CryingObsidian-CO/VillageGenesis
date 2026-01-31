package cn.ykcryobs.vg.init;

import cn.ykcryobs.vg.VillageGenesis;
import cn.ykcryobs.vg.commands.EconomyCommands;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * @author llykff
 */
@EventBusSubscriber(modid = VillageGenesis.MOD_ID)
public class CommandInit {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        var mainCommand = Commands.literal("vg").then(EconomyCommands.build(event.getBuildContext()));

        // 3. 将主命令注册到调度器，完成所有命令的加载
        dispatcher.register(mainCommand);
    }
}
