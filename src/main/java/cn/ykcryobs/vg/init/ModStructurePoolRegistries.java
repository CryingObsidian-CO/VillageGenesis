package cn.ykcryobs.vg.init;

import cn.ykcryobs.vg.VillageGenesis;
import cn.ykcryobs.vg.structures.LegacyVillageStructurePoolElement;
import cn.ykcryobs.vg.structures.VillageStructurePoolElement;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * 注册表注册器
 *
 * @author llykff
 */
public class ModStructurePoolRegistries {

    public static final DeferredRegister<StructurePoolElementType<?>> STRUCTURE_POOL_ELEMENT_REGISTRY = DeferredRegister.create(
            BuiltInRegistries.STRUCTURE_POOL_ELEMENT, VillageGenesis.MOD_ID);

    @SuppressWarnings("rawtypes")
    public static final Supplier<StructurePoolElementType> VILLAGE = ModStructurePoolRegistries.STRUCTURE_POOL_ELEMENT_REGISTRY.register(
            "village", () -> ((StructurePoolElementType) () -> VillageStructurePoolElement.CODEC));
    @SuppressWarnings("rawtypes")
    public static final Supplier<StructurePoolElementType> LEGACY_VILLAGE = ModStructurePoolRegistries.STRUCTURE_POOL_ELEMENT_REGISTRY.register(
            "legacy_village",
            () -> ((StructurePoolElementType) () -> LegacyVillageStructurePoolElement.CODEC));

    public static void register(IEventBus modEventBus) {
        STRUCTURE_POOL_ELEMENT_REGISTRY.register(modEventBus);
    }
}
