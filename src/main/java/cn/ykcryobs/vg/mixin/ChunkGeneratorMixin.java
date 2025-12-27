package cn.ykcryobs.vg.mixin;

import cn.ykcryobs.vg.utils.BoundingBox2D;
import cn.ykcryobs.vg.villageSystem.VillageManager;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * @author llykff
 */
@Mixin(ChunkGenerator.class)
public abstract class ChunkGeneratorMixin {

    @Inject(method = "tryGenerateStructure", at = @At(value = "RETURN", ordinal = 0))
    private void tryGenerateStructureInject(
            StructureSet.StructureSelectionEntry structureSelectionEntry,
            StructureManager structureManager, RegistryAccess registryAccess, RandomState random,
            StructureTemplateManager structureTemplateManager, long seed, ChunkAccess chunk,
            ChunkPos chunkPos, SectionPos sectionPos, CallbackInfoReturnable<Boolean> cir,
            @Local Structure structure, @Local StructureStart structurestart) {
        if (structure.type() != StructureType.JIGSAW) {
            return;
        }

        HolderSet<Biome> villageBiomes = structure.biomes();
        boolean isVillage = villageGenesis$isVillageBiomeSet(villageBiomes);

        if (isVillage) {
            VillageManager.registerVillage(chunkPos.getWorldPosition(),
                    new BoundingBox2D(structurestart.getBoundingBox()));
        }
    }

    @Unique
    private boolean villageGenesis$isVillageBiomeSet(HolderSet<Biome> biomeSet) {
        Optional<TagKey<Biome>> optionalBiomeTagKey = biomeSet.unwrapKey();
        if (optionalBiomeTagKey.isPresent()) {
            TagKey<Biome> biomeTagKey = optionalBiomeTagKey.get();
            return biomeTagKey.equals(BiomeTags.HAS_VILLAGE_PLAINS) || biomeTagKey.equals(
                    BiomeTags.HAS_VILLAGE_DESERT) || biomeTagKey.equals(BiomeTags.HAS_VILLAGE_SNOWY)
                    || biomeTagKey.equals(BiomeTags.HAS_VILLAGE_TAIGA) || biomeTagKey.equals(
                    BiomeTags.HAS_VILLAGE_SAVANNA);
        }
        return false;
    }
}
