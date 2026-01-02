package cn.ykcryobs.vg.villageSystem;

import cn.ykcryobs.vg.config.CommonConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;

import java.util.List;
import javax.annotation.Nullable;

/**
 * 村庄名称生成器 - 基于群系和配置生成有意义的村庄名称 支持三部分组成：前缀+核心词+后缀，可根据群系调整
 *
 * @author llykff
 */
public class VillageNameGenerator {

    /**
     * 基于群系生成村庄名称（用于保存）
     *
     * @param biome  群系
     * @param random 随机数生成器
     * @return 生成的村庄名称组件
     */
    public static Component generateVillageName(@Nullable TagKey<Biome> biome,
            RandomSource random) {
        CommonConfig.NameGenerationMode mode = CommonConfig.villageNameGenerationMode.get();

        return switch (mode) {
            case PREFIX_CORE_SUFFIX -> generatePrefixCoreSuffixName(biome, random);
            case CORE_SUFFIX -> generateCoreSuffixName(biome, random);
            case RANDOM -> generateRandomName(biome, random);
        };
    }

    /**
     * 生成完整三部分村庄名：前缀+核心词+后缀
     *
     * @param biome  群系
     * @param random 随机数生成器
     * @return 生成的村庄名称组件
     */
    private static Component generatePrefixCoreSuffixName(@Nullable TagKey<Biome> biome,
            RandomSource random) {
        // 获取前缀（不随群系变化）
        List<? extends String> prefixes = CommonConfig.villagePrefixes.get();
        String prefixKey = prefixes.get(random.nextInt(prefixes.size()));

        // 获取核心词和后缀（随群系变化）
        VillageNameParts nameParts = getNamePartsForBiome(biome);
        String coreKey = nameParts.coreList.get(random.nextInt(nameParts.coreList.size()));
        String suffixKey = nameParts.suffixList.get(random.nextInt(nameParts.suffixList.size()));

        // 构建组件
        MutableComponent result = Component.translatable(
                "village.village_genesis.name.prefix." + prefixKey);
        result.append(Component.translatable("village.village_genesis.name.core." + coreKey));
        result.append(Component.translatable("village.village_genesis.name.suffix." + suffixKey));

        return result;
    }

    /**
     * 生成核心词+后缀村庄名
     *
     * @param biome  群系
     * @param random 随机数生成器
     * @return 生成的核心词+后缀村庄名称组件
     */
    private static Component generateCoreSuffixName(@Nullable TagKey<Biome> biome,
            RandomSource random) {
        VillageNameParts nameParts = getNamePartsForBiome(biome);
        String coreKey = nameParts.coreList.get(random.nextInt(nameParts.coreList.size()));
        String suffixKey = nameParts.suffixList.get(random.nextInt(nameParts.suffixList.size()));

        // 构建组件
        MutableComponent result = Component.translatable(
                "village.village_genesis.name.core." + coreKey);
        result.append(Component.translatable("village.village_genesis.name.suffix." + suffixKey));

        return result;
    }

    /**
     * 随机生成村庄名（可能在某些群系中会缺少前缀）
     *
     * @param biome  群系
     * @param random 随机数生成器
     * @return 生成的随机村庄名称组件
     */
    private static Component generateRandomName(@Nullable TagKey<Biome> biome,
            RandomSource random) {
        boolean hasPrefix = random.nextBoolean();

        if (hasPrefix) {
            return generatePrefixCoreSuffixName(biome, random);
        } else {
            return generateCoreSuffixName(biome, random);
        }
    }

    /**
     * 根据群系获取对应的名称部分（核心词和后缀列表）
     *
     * @param biome 群系
     * @return 对应的名称部分（核心词和后缀列表）
     */
    private static VillageNameParts getNamePartsForBiome(@Nullable TagKey<Biome> biome) {
        if (biome == null) {
            // 如果没有群系信息，使用平原默认值
            return new VillageNameParts(CommonConfig.villageCorePlains.get(),
                    CommonConfig.villageSuffixPlains.get());
        }

        if (BiomeTags.HAS_VILLAGE_DESERT.equals(biome)) {
            return new VillageNameParts(CommonConfig.villageCoreDesert.get(),
                    CommonConfig.villageSuffixDesert.get());
        } else if (BiomeTags.HAS_VILLAGE_SNOWY.equals(biome)) {
            return new VillageNameParts(CommonConfig.villageCoreSnowy.get(),
                    CommonConfig.villageSuffixSnowy.get());
        } else if (BiomeTags.HAS_VILLAGE_TAIGA.equals(biome)) {
            return new VillageNameParts(CommonConfig.villageCoreTaiga.get(),
                    CommonConfig.villageSuffixTaiga.get());
        } else if (BiomeTags.HAS_VILLAGE_SAVANNA.equals(biome)) {
            return new VillageNameParts(CommonConfig.villageCoreSavanna.get(),
                    CommonConfig.villageSuffixSavanna.get());
        } else {
            // 未知群系类型，使用平原默认值
            return new VillageNameParts(CommonConfig.villageCorePlains.get(),
                    CommonConfig.villageSuffixPlains.get());
        }
    }

    public static CompoundTag serializeNbt(Component nameComponent) {
        CompoundTag nbt = new CompoundTag();
        String prefixKey = "";
        String coreKey = "";
        String suffixKey = "";
        if (nameComponent instanceof MutableComponent mutableComponent) {
            List<Component> children = mutableComponent.getSiblings();
            if (children.size() == 1) {
                coreKey = ((TranslatableContents) mutableComponent.getContents()).getKey();
                suffixKey = ((TranslatableContents) children.getFirst().getContents()).getKey();
            } else if (children.size() == 2) {
                prefixKey = ((TranslatableContents) mutableComponent.getContents()).getKey();
                coreKey = ((TranslatableContents) children.getFirst().getContents()).getKey();
                suffixKey = ((TranslatableContents) children.getLast().getContents()).getKey();
            }
        }
        nbt.putString("prefixKey", prefixKey);
        nbt.putString("coreKey", coreKey);
        nbt.putString("suffixKey", suffixKey);
        return nbt;
    }

    public static Component deserializeNbt(CompoundTag nbt) {
        String prefixKey = nbt.getString("prefixKey");
        String coreKey = nbt.getString("coreKey");
        String suffixKey = nbt.getString("suffixKey");

        MutableComponent result;
        if (!prefixKey.isEmpty()) {
            result = Component.translatable(prefixKey);
            result.append(Component.translatable(coreKey));
        } else {
            result = Component.translatable(coreKey);
        }
        result.append(Component.translatable(suffixKey));
        return result;
    }

    /**
     * 村庄名称部分数据类
     */
    private record VillageNameParts(List<? extends String> coreList,
                                    List<? extends String> suffixList) {

    }
}