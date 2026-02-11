package cn.ykcryobs.vg.commands.village;

import cn.ykcryobs.vg.VillageGenesis;
import cn.ykcryobs.vg.commands.CommandUtils;
import cn.ykcryobs.vg.event.VillageNewStageEvent;
import cn.ykcryobs.vg.villageSystem.VillageData;
import cn.ykcryobs.vg.villageSystem.VillageManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * 演进阶段命令
 *
 * @author llykff
 */
public class EvolutionStageCommands {

    public static LiteralArgumentBuilder<CommandSourceStack> build(CommandBuildContext context) {
        return Commands.literal("evolutionStage").then(Commands.literal("update")
                .then(Commands.argument("villageUUID", UuidArgument.uuid())
                        .suggests(CommandUtils::suggestVillageUuids)
                        .executes(EvolutionStageCommands::executeUpdate))).then(Commands.literal("info")
                .then(Commands.argument("villageUUID", UuidArgument.uuid())
                        .suggests(CommandUtils::suggestVillageUuids)
                        .executes(EvolutionStageCommands::executeInfo)));
    }

    private static int executeUpdate(CommandContext<CommandSourceStack> context) {
        UUID villageUUID = UuidArgument.getUuid(context, "villageUUID");
        Optional<VillageData> dataOptional = VillageManager.getVillageData(villageUUID);
        if (dataOptional.isPresent()) {
            VillageData villageData = dataOptional.get();
            VillageGenesis.postEvent(new VillageNewStageEvent(villageUUID, villageData.getEvolutionStage()));

            context.getSource().sendSuccess(
                    () -> Component.translatable("commands.village_genesis.evolutionStage.update.success",
                            villageUUID.toString(), villageData.getEvolutionStage().getDisplayName()), true);
            return 1;
        }

        context.getSource().sendFailure(
                Component.translatable("commands.village_genesis.evolutionStage.noVillageData",
                        villageUUID.toString()));
        return 0;
    }

    private static int executeInfo(CommandContext<CommandSourceStack> context) {
        UUID villageUUID = UuidArgument.getUuid(context, "villageUUID");
        Optional<VillageData> dataOptional = VillageManager.getVillageData(villageUUID);
        if (dataOptional.isPresent()) {
            VillageData villageData = dataOptional.get();
            context.getSource().sendSuccess(
                    () -> Component.translatable("commands.village_genesis.evolutionStage.info",
                            villageUUID.toString(),
                            villageData.getEvolutionStage().getDisplayName()), true);
            return 1;
        }

        context.getSource().sendFailure(
                Component.translatable("commands.village_genesis.evolutionStage.noVillageData",
                        villageUUID.toString()));
        return 0;
    }
}
