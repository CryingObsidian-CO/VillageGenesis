package cn.ykcryobs.vg.commands.economy;

import cn.ykcryobs.vg.villageSystem.economy.TransactionManager;
import cn.ykcryobs.vg.villageSystem.economy.transaction.Inquiry;
import cn.ykcryobs.vg.villagerEnhance.IVillageMixin;
import cn.ykcryobs.vg.villagerEnhance.VillagerData;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.AABB;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author llykff
 */
public class SupplyAndDemand {

    public static LiteralArgumentBuilder<CommandSourceStack> build(CommandBuildContext context) {
        return Commands.literal("supplyAndDemand").then(Commands.literal("postInquiry")
                .then(Commands.argument("buyerUUID", UuidArgument.uuid())
                        .suggests(SupplyAndDemand::suggestVillagerUuids)
                        .then(Commands.argument("item", ItemArgument.item(context))
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(SupplyAndDemand::executePostInquiry)))));

    }

    private static CompletableFuture<Suggestions> suggestVillagerUuids(
            CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        CommandSourceStack source = context.getSource();

        int radius = 10;
        AABB bounds = new AABB(source.getPosition().x() - radius, source.getPosition().y() - radius,
                source.getPosition().z() - radius, source.getPosition().x() + radius,
                source.getPosition().y() + radius, source.getPosition().z() + radius);
        Set<String> villagerUuids = new HashSet<>();
        source.getLevel().getEntitiesOfClass(Villager.class, bounds).forEach(villager -> {
            villagerUuids.add(villager.getUUID().toString());
        });

        return SharedSuggestionProvider.suggest(villagerUuids, builder);
    }

    private static int executePostInquiry(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        UUID buyer = UuidArgument.getUuid(context, "buyerUUID");
        if (level.getEntity(buyer) instanceof Villager villager) {
            Optional<VillagerData> villagerDataOptional = ((IVillageMixin) villager).villageGenesis$getVillagerData();
            if (villagerDataOptional.isPresent()) {
                VillagerData villagerData = villagerDataOptional.get();
                Item item = ItemArgument.getItem(context, "item").getItem();
                int amount = IntegerArgumentType.getInteger(context, "amount");
                TransactionManager.postInquiryAsync(new Inquiry(villagerData, item, amount));
                source.sendSuccess(
                        () -> Component.translatable("commands.village_genesis.economy.postInquiry.success",
                                villagerData.getTraderId().toString(), item.toString(), amount), true);
                return 1;
            }
        }

        source.sendFailure(
                Component.translatable("commands.village_genesis.economy.invalidTrader", buyer.toString()));
        return 0;
    }

}
