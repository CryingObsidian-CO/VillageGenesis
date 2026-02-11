package cn.ykcryobs.vg.commands.villager;

import cn.ykcryobs.vg.commands.CommandUtils;
import cn.ykcryobs.vg.villageSystem.economy.trader.ITrader;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

/**
 * @author llykff
 */
public class InventoryCommands {

    public static LiteralArgumentBuilder<CommandSourceStack> build(CommandBuildContext context) {
        return Commands.literal("inventory").then(Commands.literal("add")
                        .then(Commands.argument("villagerUUID", UuidArgument.uuid())
                                .suggests(CommandUtils::suggestVillagerUuids)
                                .then(Commands.argument("item", ItemArgument.item(context))
                                        .then(Commands.argument("count", IntegerArgumentType.integer(1))
                                                .executes(InventoryCommands::executeAdd)))))
                .then(Commands.literal("remove").then(Commands.argument("villagerUUID", UuidArgument.uuid())
                        .suggests(CommandUtils::suggestVillagerUuids)
                        .then(Commands.argument("item", ItemArgument.item(context))
                                .then(Commands.argument("count", IntegerArgumentType.integer(1))
                                        .executes(InventoryCommands::executeRemove)))));
    }

    private static int executeAdd(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        UUID villagerUUID = UuidArgument.getUuid(context, "villagerUUID");
        if (level.getEntity(villagerUUID) instanceof ITrader villager) {
            villager.addItem(new ItemStack(ItemArgument.getItem(context, "item").getItem(),
                    IntegerArgumentType.getInteger(context, "count")));

            return 1;
        }
        source.sendFailure(Component.translatable("commands.village_genesis.inventory.invalidVillager",
                villagerUUID.toString()));
        return 0;
    }

    private static int executeRemove(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        UUID villagerUUID = UuidArgument.getUuid(context, "villagerUUID");
        if (level.getEntity(villagerUUID) instanceof ITrader villager) {
            villager.removeItem(new ItemStack(ItemArgument.getItem(context, "item").getItem(),
                    IntegerArgumentType.getInteger(context, "count")));

            return 1;
        }
        source.sendFailure(Component.translatable("commands.village_genesis.inventory.invalidVillager",
                villagerUUID.toString()));
        return 0;
    }
}
