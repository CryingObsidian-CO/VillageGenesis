package cn.ykcryobs.vg.datagen;

import cn.ykcryobs.vg.structures.LegacyVillageStructurePoolElement;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.data.worldgen.placement.VillagePlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

/**
 * 世界生成数据提供器
 *
 * @author llykff
 */
public class ModWordGenProvider {

    public static ResourceKey<StructureTemplatePool> createOverrideKey(String name) {
        return ResourceKey.create(Registries.TEMPLATE_POOL,
                ResourceLocation.fromNamespaceAndPath("minecraft", name));
    }

    public static RegistrySetBuilder getWorldGenBuilder() {
        RegistrySetBuilder builder = new RegistrySetBuilder();

        builder.add(Registries.TEMPLATE_POOL, ModWordGenProvider::bootstrapPlain);

        return builder;
    }

    @SuppressWarnings("deprecation")
    private static void bootstrapPlain(BootstrapContext<StructureTemplatePool> context) {
        HolderGetter<PlacedFeature> holdergetter = context.lookup(Registries.PLACED_FEATURE);
        Holder<PlacedFeature> holder = holdergetter.getOrThrow(VillagePlacements.OAK_VILLAGE);
        Holder<PlacedFeature> holder1 = holdergetter.getOrThrow(VillagePlacements.FLOWER_PLAIN_VILLAGE);
        Holder<PlacedFeature> holder2 = holdergetter.getOrThrow(VillagePlacements.PILE_HAY_VILLAGE);
        HolderGetter<StructureProcessorList> holderGetter1 = context.lookup(Registries.PROCESSOR_LIST);
        Holder<StructureProcessorList> mossify10 = holderGetter1.getOrThrow(
                ProcessorLists.MOSSIFY_10_PERCENT);
        Holder<StructureProcessorList> mossify20 = holderGetter1.getOrThrow(
                ProcessorLists.MOSSIFY_20_PERCENT);
        Holder<StructureProcessorList> mossify70 = holderGetter1.getOrThrow(
                ProcessorLists.MOSSIFY_70_PERCENT);
        Holder<StructureProcessorList> zombieProcessor = holderGetter1.getOrThrow(
                ProcessorLists.ZOMBIE_PLAINS);
        Holder<StructureProcessorList> holder7 = holderGetter1.getOrThrow(ProcessorLists.STREET_PLAINS);
        Holder<StructureProcessorList> holder8 = holderGetter1.getOrThrow(ProcessorLists.FARM_PLAINS);
        HolderGetter<StructureTemplatePool> holderGetter2 = context.lookup(Registries.TEMPLATE_POOL);
        Holder<StructureTemplatePool> emptyFallback = holderGetter2.getOrThrow(Pools.EMPTY);
        Holder<StructureTemplatePool> holder10 = holderGetter2.getOrThrow(
                createOverrideKey("village/plains/terminators"));

        context.register(createOverrideKey("village/plains/town_centers"),
                new StructureTemplatePool(emptyFallback,
                        ImmutableList.of(Pair.of(LegacyVillageStructurePoolElement.legacyVillage(
                                                "village/plains/town_centers/plains_fountain_01", mossify20,
                                                "village_genesis:village_center"),
                                        50), Pair.of(LegacyVillageStructurePoolElement.legacyVillage(
                                        "village/plains/town_centers/plains_meeting_point_1", mossify20,
                                        "village_genesis:village_center"), 50),
                                Pair.of(LegacyVillageStructurePoolElement.legacyVillage(
                                        "village/plains/town_centers/plains_meeting_point_2",
                                        "village_genesis:village_center"), 50),
                                Pair.of(LegacyVillageStructurePoolElement.legacyVillage(
                                        "village/plains/town_centers/plains_meeting_point_3", mossify70,
                                        "village_genesis:village_center"), 50),
                                Pair.of(LegacyVillageStructurePoolElement.legacyVillage(
                                        "village/plains/zombie/town_centers/plains_fountain_01",
                                        zombieProcessor,
                                        "village_genesis:village_center"), 1),
                                Pair.of(LegacyVillageStructurePoolElement.legacyVillage(
                                        "village/plains/zombie/town_centers/plains_meeting_point_1",
                                        zombieProcessor,
                                        "village_genesis:village_center"), 1),
                                Pair.of(LegacyVillageStructurePoolElement.legacyVillage(
                                        "village/plains/zombie/town_centers/plains_meeting_point_2",
                                        zombieProcessor,
                                        "village_genesis:village_center"), 1),
                                Pair.of(LegacyVillageStructurePoolElement.legacyVillage(
                                        "village/plains/zombie/town_centers/plains_meeting_point_3",
                                        zombieProcessor,
                                        "village_genesis:village_center"), 1)),
                        StructureTemplatePool.Projection.RIGID));
    }
}
