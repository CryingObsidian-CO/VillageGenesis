package cn.ykcryobs.vg.structures;

import cn.ykcryobs.vg.init.ModRegistries;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * 旧版村庄结构池元素
 *
 * @author llykff
 */
public class LegacyVillageStructurePoolElement extends VillageStructurePoolElement {

    public static final MapCodec<LegacyVillageStructurePoolElement> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(templateCodec(), processorsCodec(), projectionCodec(),
                            overrideLiquidSettingsCodec(), Codec.STRING.fieldOf("facility_type")
                                    .forGetter(LegacyVillageStructurePoolElement::getFacilityTypeName))
                    .apply(instance, LegacyVillageStructurePoolElement::new));
    private static final Holder<StructureProcessorList> EMPTY = Holder.direct(
            new StructureProcessorList(List.of()));

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public LegacyVillageStructurePoolElement(Either<ResourceLocation, StructureTemplate> template,
            Holder<StructureProcessorList> processors, StructureTemplatePool.Projection projection,
            Optional<LiquidSettings> overrideLiquidSettings, String facilityTypeName) {
        super(template, processors, projection, overrideLiquidSettings, facilityTypeName);
    }

    /**
     * 创建一个旧版村庄结构池元素
     *
     * @param id               结构模板的ID
     * @param facilityTypeName 设施类型名称
     * @return 旧版村庄结构池元素
     * @deprecated use {@link VillageStructurePoolElement#village(String, String)}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public static Function<StructureTemplatePool.Projection, LegacyVillageStructurePoolElement> legacyVillage(
            String id,
            String facilityTypeName) {
        return projection -> new LegacyVillageStructurePoolElement(Either.left(ResourceLocation.parse(id)),
                EMPTY,
                projection, Optional.empty(), facilityTypeName);
    }

    /**
     * 创建一个旧版村庄结构池元素
     *
     * @param id               结构模板的ID
     * @param processors       结构处理器列表
     * @param facilityTypeName 设施类型名称
     * @return 旧版村庄结构池元素
     * @deprecated use {@link VillageStructurePoolElement#village(String, String)}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public static Function<StructureTemplatePool.Projection, LegacyVillageStructurePoolElement> legacyVillage(
            String id,
            Holder<StructureProcessorList> processors, String facilityTypeName) {
        return projection -> new LegacyVillageStructurePoolElement(Either.left(ResourceLocation.parse(id)),
                processors,
                projection, Optional.empty(), facilityTypeName);
    }

    @Override
    protected @NotNull StructurePlaceSettings getSettings(@NotNull Rotation rotation,
            @NotNull BoundingBox boundingBox,
            @NotNull LiquidSettings liquidSettings, boolean offset) {
        StructurePlaceSettings structureplacesettings = super.getSettings(rotation, boundingBox,
                liquidSettings,
                offset);
        structureplacesettings.popProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
        structureplacesettings.addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
        return structureplacesettings;
    }

    @Override
    public @NotNull StructurePoolElementType<?> getType() {
        return ModRegistries.LEGACY_VILLAGE.get();
    }

    @Override
    public @NotNull String toString() {
        return "LegacyVillageStructure: " + getFacilityTypeName() + " [" + this.template + "]";
    }

}
