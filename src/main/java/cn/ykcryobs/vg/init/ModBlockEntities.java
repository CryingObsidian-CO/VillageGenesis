package cn.ykcryobs.vg.init;

import cn.ykcryobs.vg.VillageGenesis;
import cn.ykcryobs.vg.block.entity.VillageInfoPanelBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * 方块实体注册器
 *
 * @author llykff
 */
public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(
            Registries.BLOCK_ENTITY_TYPE, VillageGenesis.MOD_ID);

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITY_TYPES.register(modEventBus);
    }

    public static final Supplier<BlockEntityType<VillageInfoPanelBlockEntity>> VILLAGE_INFO_PANEL = BLOCK_ENTITY_TYPES.register(
            "village_info_panel", () -> BlockEntityType.Builder.of(VillageInfoPanelBlockEntity::new,
                    ModBlocks.VILLAGE_INFO_PANEL.get()).build(null));


}