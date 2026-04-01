package cn.ykcryobs.vg.villageSystem.interVillage;

import com.mojang.logging.LogUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 村庄间系统协调层
 *
 * <p>
 * 管理所有村庄间关系数据，提供统一的村庄间交互接口。
 * </p>
 *
 * @author llykff
 */
public class InterVillageManager extends SavedData {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final InterVillageManager INSTANCE = new InterVillageManager();

    private static final Map<String, VillageRelation> relations = new ConcurrentHashMap<>();

    private static int currentTick = 0;

    private InterVillageManager() {
    }

    public static InterVillageManager load(CompoundTag tag, HolderLookup.Provider registries) {

        if (tag.contains("relations", Tag.TAG_LIST)) {
            ListTag relationsList = tag.getList("relations", Tag.TAG_COMPOUND);
            for (Tag t : relationsList) {
                CompoundTag relationTag = (CompoundTag) t;
                VillageRelation relation = VillageRelation.deserializeNBT(relationTag);
                String key = createRelationKey(relation.getVillageA(), relation.getVillageB());
                InterVillageManager.relations.put(key, relation);
            }
        }

        InterVillageManager.currentTick = tag.getInt("currentTick");

        LOGGER.info("Loaded {} inter-village relations", InterVillageManager.relations.size());
        return INSTANCE;
    }

    public static InterVillageManager getInstance() {
        return INSTANCE;
    }

    public static String createRelationKey(UUID villageA, UUID villageB) {
        UUID first = villageA.compareTo(villageB) < 0 ? villageA : villageB;
        UUID second = villageA.compareTo(villageB) < 0 ? villageB : villageA;
        return first + "_" + second;
    }

    // NOTE 同 VillageManager 关注这个会不会对保存性能有影响，有的话就独立各个子类的 SD
    public static void markDirty() {
        InterVillageManager.getInstance().setDirty();
    }

    public VillageRelation getOrCreateRelation(UUID villageA, UUID villageB) {
        if (villageA.equals(villageB)) {
            return null;
        }

        String key = createRelationKey(villageA, villageB);
        return relations.computeIfAbsent(key, k -> {
            VillageRelation relation = new VillageRelation(villageA, villageB);
            LOGGER.info("Created new inter-village relation: {} <-> {}", villageA, villageB);
            return relation;
        });
    }

    public VillageRelation getRelation(UUID villageA, UUID villageB) {
        String key = createRelationKey(villageA, villageB);
        return relations.get(key);
    }

    // NOTE 对村庄间关系的 tick 处理，暂定为 5s 一次
    public void tick() {
        currentTick++;
        if (currentTick >= 100) {
            currentTick = 0;
            relations.values().forEach(VillageRelation::tick);
        }
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        ListTag relationsList = new ListTag();
        for (VillageRelation relation : relations.values()) {
            relationsList.add(relation.serializeNBT());
        }
        tag.put("relations", relationsList);
        tag.putInt("currentTick", currentTick);

        LOGGER.info("Saved {} inter-village relations", relations.size());
        return tag;
    }

    public Map<String, VillageRelation> getAllRelations() {
        return Map.copyOf(relations);
    }
}
