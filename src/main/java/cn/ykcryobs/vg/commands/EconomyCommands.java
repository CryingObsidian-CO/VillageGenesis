package cn.ykcryobs.vg.commands;

import cn.ykcryobs.vg.commands.economy.SupplyAndDemand;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

/**
 * @author llykff
 */
public class EconomyCommands {

    public static LiteralArgumentBuilder<CommandSourceStack> build(CommandBuildContext context) {
        return Commands.literal("economy").then(SupplyAndDemand.build(context));
    }
}
