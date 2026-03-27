package cn.ykcryobs.vg.villageSystem;

import cn.ykcryobs.vg.item.ITradableItem;
import cn.ykcryobs.vg.utils.BoundingBox2D;
import cn.ykcryobs.vg.villageSystem.economy.trader.ITrader;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 村庄管理器
 *
 * @author llykff
 */
public class VillageManager extends SavedData {

    protected static final Map<UUID, VillageData> villageMap = new ConcurrentHashMap<>();
    // TODO 这个要不要持久化
    private static final Map<Integer, Set<ITrader>> commodityMap = new HashMap<>();
    private static final RandomSource RANDOM = RandomSource.create();
    private static final VillageManager INSTANCE = new VillageManager();
    private static final Logger LOGGER = LogUtils.getLogger();

    private VillageManager() {

    }


    /**
     * 获取所有村庄的数据列表
     *
     * @return 所有村庄的数据列表
     */
    public static Set<VillageData> getAllVillages() {
        return Set.copyOf(villageMap.values());
    }

    /**
     * 获取所有村庄的UUID列表
     *
     * @return 所有村庄的UUID列表
     */
    public static Set<UUID> getAllVillageIds() {
        return Set.copyOf(villageMap.keySet());
    }

    /**
     * 添加一个商品的交易商
     *
     * @param item   商品
     * @param trader 交易商
     */
    public static boolean addCommodityMap(Item item, ITrader trader) {
        if (((ITradableItem) item).isTradable()) {
            commodityMap.computeIfAbsent(Item.getId(item), k -> new HashSet<>()).add(trader);
            return true;
        }
        LOGGER.error("VillageManager: try to add an untradable item {} to commodity map", item);
        return false;
    }

    /**
     * 获取一个商品的交易商列表
     *
     * @param item 商品
     * @return 交易商列表
     */
    public static Set<ITrader> findItemSeller(Item item) {
        return Collections.unmodifiableSet(commodityMap.getOrDefault(Item.getId(item), Set.of()));
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

        VillageManager.villageMap.putIfAbsent(villageData.getVillageId(), villageData);
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
        return Optional.of(VillageManager.villageMap.get(id));
    }

    /**
     * 检查一个村庄是否存在
     *
     * @param id 村庄中心位置
     * @return 是否存在
     */
    public static boolean isVillageExist(UUID id) {
        if (id == null) {
            LOGGER.warn("VillageManager: 尝试获取空ID的村庄数据类");
            return false;
        }
        if (!VillageManager.villageMap.containsKey(id)) {
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
        return VillageManager.villageMap.values().stream()
                .filter(village -> village.getBoundingBox().contains(pos)).findFirst();
    }

    /**
     * 检查一个位置是否在任何村庄的范围内
     *
     * @param pos 要检查的位置
     * @return 是否在任何村庄范围内
     */
    public static boolean isPosInVillage(BlockPos pos) {
        return VillageManager.villageMap.values().stream()
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
            VillageManager.villageMap.putIfAbsent(villageData.getVillageId(), villageData);
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

    public static VillageManager getInstance() {
        return INSTANCE;
    }

    public void tick() {
        // TODO 剔除不必要的村庄优化性能
        villageMap.values().forEach(VillageData::tick);
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
