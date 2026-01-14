package cn.ykcryobs.vg.datagen;

import cn.ykcryobs.vg.VillageGenesis;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * 方块状态提供器
 *
 * @author llykff
 */
public class ModBlockStatesProvider extends BlockStateProvider {

    public ModBlockStatesProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, VillageGenesis.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // 为村庄信息面板注册简单方块状态和模型
//        simpleBlock(ModBlocks.VILLAGE_INFO_PANEL.get(), cubeAll(ModBlocks.VILLAGE_INFO_PANEL.get()));
//        simpleBlockItem(ModBlocks.VILLAGE_INFO_PANEL.get(), cubeAll(ModBlocks.VILLAGE_INFO_PANEL.get()));
    }
}
