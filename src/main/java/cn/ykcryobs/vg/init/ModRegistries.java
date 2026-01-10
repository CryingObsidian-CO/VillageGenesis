package cn.ykcryobs.vg.init;

import cn.ykcryobs.vg.VillageGenesis;
import cn.ykcryobs.vg.structures.LegacyVillageStructurePoolElement;
import cn.ykcryobs.vg.structures.VillageStructurePoolElement;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * 注册表注册器
 *
 * @author llykff
 */
public class ModRegistries {

    public static final DeferredRegister<StructurePoolElementType<?>> STRUCTURE_POOL_ELEMENT_REGISTRY = DeferredRegister.create(
            BuiltInRegistries.STRUCTURE_POOL_ELEMENT, VillageGenesis.MOD_ID);

    @SuppressWarnings("rawtypes")
    public static final Supplier<StructurePoolElementType> VILLAGE = ModRegistries.STRUCTURE_POOL_ELEMENT_REGISTRY.register(
            "village", () -> ((StructurePoolElementType) () -> VillageStructurePoolElement.CODEC));
    @SuppressWarnings("rawtypes")
    public static final Supplier<StructurePoolElementType> LEGACY_VILLAGE = ModRegistries.STRUCTURE_POOL_ELEMENT_REGISTRY.register(
            "legacy_village",
            () -> ((StructurePoolElementType) () -> LegacyVillageStructurePoolElement.CODEC));

}
