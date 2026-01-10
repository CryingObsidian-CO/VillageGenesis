package cn.ykcryobs.vg.structures;

import cn.ykcryobs.vg.init.ModDataPackRegistries;
import cn.ykcryobs.vg.init.ModRegistries;
import cn.ykcryobs.vg.villageSystem.VillageData;
import cn.ykcryobs.vg.villageSystem.VillageManager;
import cn.ykcryobs.vg.villageSystem.facility.VillageFacility;
import cn.ykcryobs.vg.villageSystem.facility.types.FacilityType;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * 村庄结构池元素
 *
 * @author llykff
 */
public class VillageStructurePoolElement extends SinglePoolElement {

    public static final MapCodec<VillageStructurePoolElement> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(templateCodec(), processorsCodec(), projectionCodec(),
                            overrideLiquidSettingsCodec(),
                            Codec.STRING.fieldOf("facility_type")
                                    .forGetter(VillageStructurePoolElement::getFacilityTypeName))
                    .apply(instance, VillageStructurePoolElement::new));
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Holder<StructureProcessorList> EMPTY = Holder.direct(
            new StructureProcessorList(List.of()));

    private final String facilityTypeName;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public VillageStructurePoolElement(Either<ResourceLocation, StructureTemplate> template,
            Holder<StructureProcessorList> processors, StructureTemplatePool.Projection projection,
            Optional<LiquidSettings> overrideLiquidSettings, String facilityTypeName) {
        super(template, processors, projection, overrideLiquidSettings);
        this.facilityTypeName = facilityTypeName;
    }

    /**
     * 创建一个村庄结构池元素
     *
     * @param id               结构模板的ID
     * @param facilityTypeName 设施类型名称
     * @return 村庄结构池元素
     */
    public static Function<StructureTemplatePool.Projection, VillageStructurePoolElement> village(String id,
            String facilityTypeName) {
        return projection -> new VillageStructurePoolElement(Either.left(ResourceLocation.parse(id)), EMPTY,
                projection,
                Optional.empty(), facilityTypeName);
    }

    /**
     * 创建一个村庄结构池元素
     *
     * @param id               结构模板的ID
     * @param processors       结构处理器列表
     * @param facilityTypeName 设施类型名称
     * @return 村庄结构池元素
     */
    public static Function<StructureTemplatePool.Projection, VillageStructurePoolElement> village(String id,
            Holder<StructureProcessorList> processors, String facilityTypeName) {
        return projection -> new VillageStructurePoolElement(Either.left(ResourceLocation.parse(id)),
                processors,
                projection, Optional.empty(), facilityTypeName);
    }

    /**
     * 创建一个村庄结构池元素
     *
     * @param id               结构模板的ID
     * @param liquidSettings   液体设置
     * @param facilityTypeName 设施类型名称
     * @return 村庄结构池元素
     */
    public static Function<StructureTemplatePool.Projection, VillageStructurePoolElement> village(String id,
            LiquidSettings liquidSettings, String facilityTypeName) {
        return projection -> new VillageStructurePoolElement(Either.left(ResourceLocation.parse(id)), EMPTY,
                projection,
                Optional.of(liquidSettings), facilityTypeName);
    }


    /**
     * 创建一个村庄结构池元素
     *
     * @param id               结构模板的ID
     * @param processors       结构处理器列表
     * @param liquidSettings   液体设置
     * @param facilityTypeName 设施类型名称
     * @return 村庄结构池元素
     */
    public static Function<StructureTemplatePool.Projection, VillageStructurePoolElement> village(String id,
            Holder<StructureProcessorList> processors, LiquidSettings liquidSettings,
            String facilityTypeName) {
        return projection -> new VillageStructurePoolElement(Either.left(ResourceLocation.parse(id)),
                processors,
                projection, Optional.of(liquidSettings), facilityTypeName);
    }


    public String getFacilityTypeName() {
        return facilityTypeName;
    }

    @Override
    public boolean place(@NotNull StructureTemplateManager structureTemplateManager,
            @NotNull WorldGenLevel level,
            @NotNull StructureManager structureManager, @NotNull ChunkGenerator generator,
            @NotNull BlockPos offset,
            @NotNull BlockPos pos, @NotNull Rotation rotation, @NotNull BoundingBox box,
            @NotNull RandomSource random,
            @NotNull LiquidSettings liquidSettings, boolean keepJigsaws) {
        // 获取FacilityType

        HolderLookup.RegistryLookup<FacilityType> facilityRegistry = level.registryAccess()
                .lookupOrThrow(ModDataPackRegistries.FACILITY_REGISTRY_KEY);
        ResourceKey<FacilityType> resourceKey = ResourceKey.create(
                ModDataPackRegistries.FACILITY_REGISTRY_KEY,
                ResourceLocation.parse(facilityTypeName));
        Holder<FacilityType> facilityHolder = facilityRegistry.get(resourceKey).orElse(null);
        if (facilityHolder == null) {
            LOGGER.warn("Can't place structure {} at {}, not found in facility registry", facilityTypeName,
                    pos);
            return false;
        }

        FacilityType facilityType = facilityHolder.value();
        Optional<VillageData> villageDataOptional = VillageManager.getVillageIfPosInVillage(pos);
        if (villageDataOptional.isEmpty()) {
            LOGGER.warn("Can't place structure {} at {}, not in any village", facilityTypeName, pos);
            return false;
        }

        villageDataOptional.get().registerFacility(new VillageFacility(facilityType, pos));
        LOGGER.info("Placed structure {} at {}", facilityTypeName, pos);
        return super.place(structureTemplateManager, level, structureManager, generator, offset, pos,
                rotation, box,
                random, liquidSettings, keepJigsaws);
    }

    @Override
    public @NotNull StructurePoolElementType<?> getType() {
        return ModRegistries.VILLAGE.get();
    }

    @Override
    public @NotNull String toString() {
        return "VillageStructure: " + facilityTypeName + " [" + this.template + "]";
    }
}
