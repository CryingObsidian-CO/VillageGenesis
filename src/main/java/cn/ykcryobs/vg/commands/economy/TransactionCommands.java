package cn.ykcryobs.vg.commands.economy;

import cn.ykcryobs.vg.commands.CommandUtils;
import cn.ykcryobs.vg.villageSystem.VillageManager;
import cn.ykcryobs.vg.villageSystem.economy.trader.ITrader;
import cn.ykcryobs.vg.villageSystem.economy.transaction.TransactionManager;
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
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;

import java.util.UUID;

/**
 * 交易命令
 *
 * @author llykff
 */
public class TransactionCommands {

    public static LiteralArgumentBuilder<CommandSourceStack> build(CommandBuildContext context) {
        return Commands.literal("transaction").then(Commands.literal("tryToBuy")
                        .then(Commands.argument("buyerUUID", UuidArgument.uuid())
                                .suggests(CommandUtils::suggestVillagerUuids)
                                .then(Commands.argument("item", ItemArgument.item(context))
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                                .executes(TransactionCommands::executeTryToBuy)))))
                .then(Commands.literal("releaseProduct")
                        .then(Commands.argument("sellerUUID", UuidArgument.uuid())
                                .suggests(CommandUtils::suggestVillagerUuids)
                                .then(Commands.argument("item", ItemArgument.item(context))
                                        .executes(TransactionCommands::executeReleaseProduct))));

    }


    private static int executeTryToBuy(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        UUID buyer = UuidArgument.getUuid(context, "buyerUUID");
        if (level.getEntity(buyer) instanceof Villager villager) {
            Item item = ItemArgument.getItem(context, "item").getItem();
            int amount = IntegerArgumentType.getInteger(context, "amount");
            TransactionManager.tryToBuy(item, amount, (ITrader) villager, (result, actualAmount) -> {
                source.sendSystemMessage(Component.translatable(
                        "commands.village_genesis.transaction.tryToBuy." + result.name().toLowerCase(),
                        actualAmount, item.toString(), buyer.toString()));
            });
            source.sendSuccess(
                    () -> Component.translatable("commands.village_genesis.transaction.tryToBuy.successPost",
                            amount, item.toString(), buyer.toString()), true);
            return 1;
        }

        source.sendFailure(Component.translatable("commands.village_genesis.transaction.invalidTrader",
                buyer.toString()));
        return 0;
    }


    private static int executeReleaseProduct(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        UUID seller = UuidArgument.getUuid(context, "sellerUUID");
        if (level.getEntity(seller) instanceof Villager villager) {
            Item item = ItemArgument.getItem(context, "item").getItem();
            if (VillageManager.addCommodityMap(item, (ITrader) villager)) {
                source.sendSuccess(() -> Component.translatable(
                        "commands.village_genesis.transaction.releaseProduct.success", seller.toString(),
                        item.toString()), true);
                return 1;
            }
            source.sendFailure(
                    Component.translatable("commands.village_genesis.transaction.releaseProduct.invalid_item",
                            seller.toString(), item.toString()));
            return 0;
        }
        source.sendFailure(Component.translatable("commands.village_genesis.transaction.invalidTrader",
                seller.toString()));
        return 0;
    }
}
