package cn.ykcryobs.vg.init;

import cn.ykcryobs.vg.VillageGenesis;
import cn.ykcryobs.vg.block.VillageInfoPanelBlock;
import cn.ykcryobs.vg.block.item.VillageInfoPanelBlockItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * 方块注册器
 *
 * @author llykff
 */
public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(VillageGenesis.MOD_ID);


    public static final Supplier<VillageInfoPanelBlock> VILLAGE_INFO_PANEL = BLOCKS.register(
            "village_info_panel", () -> new VillageInfoPanelBlock());
    public static final Supplier<VillageInfoPanelBlockItem> VILLAGE_INFO_PANEL_ITEM = ModItems.ITEMS.register(
            "village_info_panel", () -> new VillageInfoPanelBlockItem(VILLAGE_INFO_PANEL.get()));


    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> blocks = BLOCKS.register(name, block);
        registerBlockItems(name, blocks);
        return blocks;
    }

    private static <T extends Block> void registerBlockItems(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
