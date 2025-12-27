package cn.ykcryobs.vg.villageSystem;

import cn.ykcryobs.vg.utils.BoundingBox2D;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author llykff
 */
public class VillageManager extends SavedData {

    private static final VillageManager INSTANCE = new VillageManager();
    private static final Logger LOGGER = LogUtils.getLogger();
    protected Map<Long, VillageData> villageMap = new HashMap<>();

    private VillageManager() {

    }

    public static VillageManager getInstance() {
        return INSTANCE;
    }

    /**
     * 注册一个村庄
     *
     * @param pos 村庄中心位置
     */
    public static void registerVillage(BlockPos pos, BoundingBox2D boundingBox) {
        VillageManager.getInstance().villageMap.putIfAbsent(pos.asLong(),
                new VillageData(pos, boundingBox));
    }

    /**
     * 获取一个村庄的数据类
     *
     * @param pos 村庄中心位置
     * @return 村庄数据类
     */
    public static Optional<VillageData> getVillageData(BlockPos pos) {
        if (!isVillageExist(pos)) {
            return Optional.empty();
        }
        return Optional.of(VillageManager.getInstance().villageMap.get(pos.asLong()));
    }

    /**
     * 检查一个村庄是否存在
     *
     * @param pos 村庄中心位置
     * @return 是否存在
     */
    public static boolean isVillageExist(BlockPos pos) {
        if (!VillageManager.getInstance().villageMap.containsKey(pos.asLong())) {
            LOGGER.warn("VillageManager: 未找到村庄数据类，位置：x{}, y{}, z{}", pos.getX(),
                    pos.getY(), pos.getZ());
            return false;
        }
        return true;
    }

    /**
     * 获取位置所属的村庄中心位置
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
     * @param pos           要检查的位置
     * @param villageCenter 村庄中心位置
     * @return 是否在任何村庄范围内
     */
    public static boolean isPosInVillage(BlockPos pos, BlockPos villageCenter) {

        return getVillageData(villageCenter).isPresent() && getVillageData(villageCenter).get()
                .getBoundingBox().contains(pos);
    }

    public static VillageManager load(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag villages = tag.getList("Villages", CompoundTag.TAG_COMPOUND);
        villages.forEach(villageTag -> {
            VillageData villageData = VillageData.load((CompoundTag) villageTag, registries);
            VillageManager.getInstance().villageMap.putIfAbsent(villageData.getCenterPos().asLong(),
                    villageData);
        });

        return INSTANCE;
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        ListTag tagList = new ListTag();
        villageMap.values().forEach(
                villageData -> tagList.add(villageData.save(new CompoundTag(), registries)));

        tag.put("Villages", tagList);
        return tag;
    }
}
