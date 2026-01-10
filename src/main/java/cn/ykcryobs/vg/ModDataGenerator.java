package cn.ykcryobs.vg;

import cn.ykcryobs.vg.datagen.ModBlockLootTablesProvider;
import cn.ykcryobs.vg.datagen.ModBlockStatesProvider;
import cn.ykcryobs.vg.datagen.ModBlockTagsProvider;
import cn.ykcryobs.vg.datagen.ModEnUsLangProvider;
import cn.ykcryobs.vg.datagen.ModFacilityDataProvider;
import cn.ykcryobs.vg.datagen.ModFacilityLevelDataProvider;
import cn.ykcryobs.vg.datagen.ModItemModelsProvider;
import cn.ykcryobs.vg.datagen.ModItemTagsProvider;
import cn.ykcryobs.vg.datagen.ModRecipeProvider;
import cn.ykcryobs.vg.datagen.ModWordGenProvider;
import cn.ykcryobs.vg.datagen.ModZhCnLangProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * 数据生成器
 *
 * @author llykff
 */
@EventBusSubscriber(modid = VillageGenesis.MOD_ID)
public class ModDataGenerator {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), new LootTableProvider(packOutput, Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(ModBlockLootTablesProvider::new,
                        LootContextParamSets.BLOCK)), lookupProvider));
        generator.addProvider(event.includeServer(), new ModRecipeProvider(packOutput, lookupProvider));
        BlockTagsProvider blockTagsProvider = new ModBlockTagsProvider(packOutput, lookupProvider,
                fileHelper);
        generator.addProvider(event.includeServer(), blockTagsProvider);
        generator.addProvider(event.includeServer(),
                new ModItemTagsProvider(packOutput, lookupProvider, blockTagsProvider.contentsGetter(),
                        fileHelper));
        generator.addProvider(event.includeServer(),
                new DatapackBuiltinEntriesProvider(packOutput, lookupProvider,
                        ModWordGenProvider.getWorldGenBuilder(), Set.of("minecraft")));
        generator.addProvider(event.includeServer(),
                new ModFacilityDataProvider(packOutput, VillageGenesis.MOD_ID));
        generator.addProvider(event.includeServer(),
                new ModFacilityLevelDataProvider(packOutput, VillageGenesis.MOD_ID));

        generator.addProvider(event.includeClient(), new ModItemModelsProvider(packOutput, fileHelper));
        generator.addProvider(event.includeClient(), new ModBlockStatesProvider(packOutput, fileHelper));
        generator.addProvider(event.includeClient(), new ModEnUsLangProvider(packOutput));
        generator.addProvider(event.includeClient(), new ModZhCnLangProvider(packOutput));
    }
}