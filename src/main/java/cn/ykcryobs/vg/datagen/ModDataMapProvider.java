package cn.ykcryobs.vg.datagen;

import cn.ykcryobs.vg.dataMap.ItemDataMap;
import cn.ykcryobs.vg.init.ModDataMap;
import cn.ykcryobs.vg.villageSystem.economy.market.ResourceType;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.DataMapProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * @author llykff
 */
public class ModDataMapProvider extends DataMapProvider {

    public ModDataMapProvider(PackOutput packOutput,
            CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }


    @Override
    protected void gather(HolderLookup.@NotNull Provider provider) {
        this.builder(ModDataMap.ITEM_DATA_MAP).replace(true)
                .add(getItemHolder(Items.APPLE), new ItemDataMap(0.5f, ResourceType.FOOD), false);
    }

    public Holder<Item> getItemHolder(Item item) {
        return BuiltInRegistries.ITEM.wrapAsHolder(item);
    }
}
