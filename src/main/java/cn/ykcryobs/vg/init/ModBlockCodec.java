package cn.ykcryobs.vg.init;

import cn.ykcryobs.vg.VillageGenesis;
import cn.ykcryobs.vg.block.VillageInfoPanelBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * @author llykff
 */
public class ModBlockCodec {

    public static final DeferredRegister<MapCodec<? extends Block>> BLOCK_CODECS = DeferredRegister.create(
            Registries.BLOCK_TYPE, VillageGenesis.MOD_ID);

    public static final Supplier<MapCodec<VillageInfoPanelBlock>> VILLAGE_INFO_PANEL = BLOCK_CODECS.register(
            "village_info_panel", () -> BlockBehaviour.simpleCodec(VillageInfoPanelBlock::new));

}
