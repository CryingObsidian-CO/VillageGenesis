package cn.ykcryobs.vg.datagen;

import cn.ykcryobs.vg.VillageGenesis;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * @author llykff
 */
public class ModItemModelsProvider extends ItemModelProvider {
    public ModItemModelsProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, VillageGenesis.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

    }
}
