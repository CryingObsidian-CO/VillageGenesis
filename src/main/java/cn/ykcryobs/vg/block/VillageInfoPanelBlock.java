package cn.ykcryobs.vg.block;

import cn.ykcryobs.vg.block.entity.VillageInfoPanelBlockEntity;
import cn.ykcryobs.vg.client.screen.VillageInfoPanelScreen;
import cn.ykcryobs.vg.dataComponents.BoundaryScepterComponent;
import cn.ykcryobs.vg.init.ModBlockCodec;
import cn.ykcryobs.vg.init.ModBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * 村庄信息面板方块 用于显示村庄信息并可以通过shift右键钟绑定村庄
 *
 * @author llykff
 */
public class VillageInfoPanelBlock extends BaseEntityBlock {

    public VillageInfoPanelBlock(Properties properties) {
        super(properties);
    }

    public VillageInfoPanelBlock() {
        this(Properties.of());
    }

    @Override
    protected @NotNull MapCodec<VillageInfoPanelBlock> codec() {
        return ModBlockCodec.VILLAGE_INFO_PANEL.get();
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new VillageInfoPanelBlockEntity(pos, state);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.Builder params) {
        if (params.getOptionalParameter(
                LootContextParams.BLOCK_ENTITY) instanceof VillageInfoPanelBlockEntity blockEntity) {
            UUID villageId = blockEntity.getBoundedVillageId();
            ItemStack dropItem = new ItemStack(ModBlocks.VILLAGE_INFO_PANEL_ITEM.get());
            BoundaryScepterComponent.setBoundedVillage(dropItem, villageId);
            return List.of(dropItem);
        }
        return super.getDrops(state, params);
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level,
            @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (level.isClientSide()) {
            UUID villageId = ((VillageInfoPanelBlockEntity) level.getBlockEntity(pos)).getBoundedVillageId();
            if (villageId == null) {
                return InteractionResult.FAIL;
            }
            Minecraft.getInstance().setScreen(new VillageInfoPanelScreen(
                    Component.translatable("gui.village_genesis.village_info_title"), villageId.toString()));
        } else {
            level.sendBlockUpdated(pos, state, state, 3);
        }
        return InteractionResult.SUCCESS;
    }

}