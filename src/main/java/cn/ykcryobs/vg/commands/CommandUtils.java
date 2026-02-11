package cn.ykcryobs.vg.commands;

import cn.ykcryobs.vg.villageSystem.VillageManager;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.phys.AABB;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author llykff
 */
public class CommandUtils {

    public static CompletableFuture<Suggestions> suggestVillagerUuids(
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

    public static CompletableFuture<Suggestions> suggestVillageUuids(
            CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(
                VillageManager.getAllVillageIds().stream().map(UUID::toString), builder);
    }
}
