package cn.ykcryobs.vg.villageSystem;

import cn.ykcryobs.vg.utils.BoundingBox2D;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 村庄管理器
 *
 * @author llykff
 */
public class VillageManager extends SavedData {

    private static final RandomSource RANDOM = RandomSource.create();
    private static final VillageManager INSTANCE = new VillageManager();
    private static final Logger LOGGER = LogUtils.getLogger();
    protected Map<UUID, VillageData> villageMap = new HashMap<>();

    private VillageManager() {

    }

    public static VillageManager getInstance() {
        return INSTANCE;
    }

    /**
     * 注册一个村庄
     *
     * @param pos         村庄中心位置
     * @param boundingBox 村庄边界框
     * @param biome       村庄所在的群系
     */
    public static void registerVillage(BlockPos pos, BoundingBox2D boundingBox,
            @Nullable TagKey<Biome> biome) {
        VillageData villageData = new VillageData(pos, boundingBox, biome, RANDOM);

        VillageManager.getInstance().villageMap.putIfAbsent(villageData.getVillageId(), villageData);
        markDirty();
    }


    /**
     * 获取一个村庄的数据类
     *
     * @param id 村庄ID
     * @return 村庄数据类
     */
    public static Optional<VillageData> getVillageData(UUID id) {
        if (!isVillageExist(id)) {
            return Optional.empty();
        }
        return Optional.of(VillageManager.getInstance().villageMap.get(id));
    }

    /**
     * 检查一个村庄是否存在
     *
     * @param id 村庄中心位置
     * @return 是否存在
     */
    public static boolean isVillageExist(UUID id) {
        if (!VillageManager.getInstance().villageMap.containsKey(id)) {
            LOGGER.warn("VillageManager: 未找到村庄数据类，ID：{}", id);
            return false;
        }
        return true;
    }

    /**
     * 获取一个位置所在的村庄（如果有）
     *
     * @param pos 要检查的位置
     * @return 村庄中心位置（第一个匹配的）
     */
    public static Optional<VillageData> getVillageIfPosInVillage(BlockPos pos) {
        return VillageManager.getInstance().villageMap.values().stream()
                .filter(village -> village.getBoundingBox().contains(pos)).findFirst();
    }

    /**
     * 检查一个位置是否在任何村庄的范围内
     *
     * @param pos 要检查的位置
     * @return 是否在任何村庄范围内
     */
    public static boolean isPosInVillage(BlockPos pos) {
        return VillageManager.getInstance().villageMap.values().stream()
                .anyMatch(village -> village.getBoundingBox().contains(pos));
    }

    /**
     * 检查一个位置是否在指定村庄范围
     *
     * @param pos       要检查的位置
     * @param villageId 村庄ID
     * @return 是否在任何村庄范围内
     */
    public static boolean isPosInVillage(BlockPos pos, UUID villageId) {

        return getVillageData(villageId).isPresent() && getVillageData(villageId).get().getBoundingBox()
                .contains(pos);
    }

    public static VillageManager load(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag villages = tag.getList("villages", CompoundTag.TAG_COMPOUND);
        for (Tag villageTag : villages) {
            VillageData villageData = VillageData.load((CompoundTag) villageTag, registries);
            VillageManager.getInstance().villageMap.putIfAbsent(villageData.getVillageId(), villageData);
        }

        LOGGER.info("Loaded {} villages from world data", villages.size());
        return INSTANCE;
    }

    // NOTE 关注这个会不会对保存性能有影响，有的话就独立各个子类的 SD
    public static void markDirty() {
        VillageManager.getInstance().setDirty();
    }

    public static RandomSource getRandom() {
        return RANDOM;
    }

    public void tick() {
        // TODO 剔除不必要的村庄优化性能
        this.villageMap.values().forEach(VillageData::tick);
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        ListTag tagList = new ListTag();
        for (VillageData villageData : villageMap.values()) {
            tagList.add(villageData.save(new CompoundTag(), registries));
        }

        tag.put("villages", tagList);

        LOGGER.info("Saved {} villages to world data", villageMap.size());
        return tag;
    }
}
