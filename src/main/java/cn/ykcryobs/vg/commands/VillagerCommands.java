package cn.ykcryobs.vg.commands;

import cn.ykcryobs.vg.commands.villager.InventoryCommands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

/**
 * 村民命令
 *
 * @author llykff
 */
public class VillagerCommands {

    public static LiteralArgumentBuilder<CommandSourceStack> build(CommandBuildContext context) {
        return Commands.literal("villager").then(InventoryCommands.build(context));
    }
}
