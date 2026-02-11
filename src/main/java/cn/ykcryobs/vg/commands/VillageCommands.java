package cn.ykcryobs.vg.commands;

import cn.ykcryobs.vg.commands.village.EvolutionStageCommands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

/**
 * 村庄命令
 *
 * @author llykff
 */
public class VillageCommands {

    public static LiteralArgumentBuilder<CommandSourceStack> build(CommandBuildContext context) {
        return Commands.literal("village").then(EvolutionStageCommands.build(context));
    }
}
