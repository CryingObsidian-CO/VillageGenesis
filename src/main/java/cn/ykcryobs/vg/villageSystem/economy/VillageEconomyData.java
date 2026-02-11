package cn.ykcryobs.vg.villageSystem.economy;

import cn.ykcryobs.vg.config.ServerConfig;
import cn.ykcryobs.vg.item.ITradableItem;
import cn.ykcryobs.vg.villageSystem.VillageManager;
import cn.ykcryobs.vg.villageSystem.economy.market.ResourceType;
import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * 村庄经济数据类
 *
 * @author llykff
 */
public class VillageEconomyData {

    private final Map<ResourceType, Pair<Integer, Integer>> supplyDemand = new EnumMap<>(ResourceType.class);
    private final Map<ResourceType, Float> supplyDemandFactors = new EnumMap<>(ResourceType.class);
    private final Map<Item, Pair<Integer, Integer>> extraSupplyDemand = new HashMap<>();
    private final Map<Item, Float> extraSupplyDemandFactors = new HashMap<>();

    public VillageEconomyData() {
        for (ResourceType resourceType : ResourceType.values()) {
            supplyDemandFactors.put(resourceType, 1.0f);
        }
    }

    /**
     * 获取物品的供需因子
     *
     * @param item 物品
     * @return 因子值
     */
    public float getFactorFromItem(ITradableItem item) {
        if (item.getResourceType() == ResourceType.NONE) {
            return extraSupplyDemandFactors.getOrDefault((Item) item, 1.0f);
        }
        return supplyDemandFactors.get(item.getResourceType());
    }

    /**
     * 计算供需因子
     *
     * @param supply 供给
     * @param demand 需求
     * @return 因子值
     */
    private float calculateFactor(int supply, int demand) {
        int safeSupply = Math.max(supply, 1); // 除零保护
        float factor = (float) demand / safeSupply;
        return Math.max(ServerConfig.minFactor.get(), Math.min(ServerConfig.maxFactor.get(), factor));
    }

    /**
     * 计算供需因子
     *
     * @param resourceType 资源类型
     */
    private void calculateFactor(ResourceType resourceType) {
        Pair<Integer, Integer> supplyDemandPair = supplyDemand.get(resourceType);
        int supply = supplyDemandPair.getFirst();
        int demand = supplyDemandPair.getSecond();
        supplyDemandFactors.put(resourceType, calculateFactor(supply, demand));
        this.markDirty();
    }

    /**
     * 计算额外供需因子
     *
     * @param item 物品
     */
    private void calculateFactor(Item item) {
        Pair<Integer, Integer> extraSupplyDemandPair = extraSupplyDemand.get(item);
        int extraSupply = extraSupplyDemandPair.getFirst();
        int extraDemand = extraSupplyDemandPair.getSecond();
        extraSupplyDemandFactors.put(item, calculateFactor(extraSupply, extraDemand));
        this.markDirty();
    }

    /**
     * 设置供需
     *
     * @param resourceType 资源类型
     * @param supply       供给
     * @param demand       需求
     */
    private void setSupplyDemand(ResourceType resourceType, int supply, int demand) {
        supplyDemand.put(resourceType, new Pair<>(supply, demand));
        calculateFactor(resourceType);
    }

    /**
     * 设置额外供需
     *
     * @param item        物品
     * @param extraSupply 额外供给
     * @param extraDemand 额外需求
     */
    private void setExtraSupplyDemand(ITradableItem item, int extraSupply, int extraDemand) {
        if (item.getResourceType() == ResourceType.NONE) {
            extraSupplyDemand.put((Item) item, new Pair<>(extraSupply, extraDemand));
            calculateFactor((Item) item);
        }
        this.setSupplyDemand(item.getResourceType(), extraSupply, extraDemand);
    }

    /**
     * 添加供给 你可以使用 {@link #addExtraSupply(Item, int)} 如果你不确定物品是否有对应的资源类型
     *
     * @param resourceType 资源类型
     * @param delta        供给增量
     */
    public void addSupply(ResourceType resourceType, int delta) {
        Pair<Integer, Integer> supplyDemandPair = supplyDemand.get(resourceType);
        setSupplyDemand(resourceType, supplyDemandPair.getFirst() + delta, supplyDemandPair.getSecond());
    }

    /**
     * 添加需求 你可以使用 {@link #addExtraDemand(Item, int)} 如果你不确定物品是否有对应的资源类型
     *
     * @param resourceType 资源类型
     * @param delta        需求增量
     */
    public void addDemand(ResourceType resourceType, int delta) {
        Pair<Integer, Integer> supplyDemandPair = supplyDemand.get(resourceType);
        setSupplyDemand(resourceType, supplyDemandPair.getFirst(), supplyDemandPair.getSecond() + delta);
    }

    /**
     * 添加额外供给
     *
     * @param item  物品
     * @param delta 供给增量
     */
    public void addExtraSupply(Item item, int delta) {
        if (((ITradableItem) item).isTradable()) {
            Pair<Integer, Integer> extraSupplyDemandPair = extraSupplyDemand.get(item);
            setExtraSupplyDemand((ITradableItem) item, extraSupplyDemandPair.getFirst() + delta,
                    extraSupplyDemandPair.getSecond());
        }
    }

    /**
     * 添加额外需求
     *
     * @param item  物品
     * @param delta 需求增量
     */
    public void addExtraDemand(Item item, int delta) {
        if (((ITradableItem) item).isTradable()) {
            Pair<Integer, Integer> extraSupplyDemandPair = extraSupplyDemand.get(item);
            setExtraSupplyDemand((ITradableItem) item, extraSupplyDemandPair.getFirst(),
                    extraSupplyDemandPair.getSecond() + delta);
        }
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag supplyDemandList = new ListTag();
        for (Map.Entry<ResourceType, Pair<Integer, Integer>> entry : supplyDemand.entrySet()) {
            CompoundTag supplyDemandTag = new CompoundTag();
            supplyDemandTag.putString("resourceType", entry.getKey().toString());
            supplyDemandTag.putInt("supply", entry.getValue().getFirst());
            supplyDemandTag.putInt("demand", entry.getValue().getSecond());
            supplyDemandList.add(supplyDemandTag);
        }
        tag.put("supplyDemand", supplyDemandList);

        ListTag extraSupplyDemandList = new ListTag();
        for (Map.Entry<Item, Pair<Integer, Integer>> entry : extraSupplyDemand.entrySet()) {
            CompoundTag extraSupplyDemandTag = new CompoundTag();
            extraSupplyDemandTag.putInt("item", Item.getId(entry.getKey()));
            extraSupplyDemandTag.putInt("supply", entry.getValue().getFirst());
            extraSupplyDemandTag.putInt("demand", entry.getValue().getSecond());
            extraSupplyDemandList.add(extraSupplyDemandTag);
        }
        tag.put("extraSupplyDemand", extraSupplyDemandList);

        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        if (tag.contains("supplyDemand", Tag.TAG_LIST)) {
            ListTag supplyDemandList = tag.getList("supplyDemand", Tag.TAG_COMPOUND);
            for (Tag t : supplyDemandList) {
                CompoundTag supplyDemandTag = (CompoundTag) t;
                ResourceType resourceType = ResourceType.valueOf(supplyDemandTag.getString("resourceType"));
                int supply = supplyDemandTag.getInt("supply");
                int demand = supplyDemandTag.getInt("demand");
                setSupplyDemand(resourceType, supply, demand);
            }
        }

        if (tag.contains("extraSupplyDemand", Tag.TAG_LIST)) {
            ListTag extraSupplyDemandList = tag.getList("extraSupplyDemand", Tag.TAG_COMPOUND);
            for (Tag t : extraSupplyDemandList) {
                CompoundTag extraSupplyDemandTag = (CompoundTag) t;
                Item item = Item.byId(extraSupplyDemandTag.getInt("item"));
                int supply = extraSupplyDemandTag.getInt("supply");
                int demand = extraSupplyDemandTag.getInt("demand");
                setExtraSupplyDemand((ITradableItem) item, supply, demand);
            }
        }
    }

    public void markDirty() {
        VillageManager.markDirty();
    }
}
