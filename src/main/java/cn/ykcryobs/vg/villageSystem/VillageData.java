package cn.ykcryobs.vg.villageSystem;

import cn.ykcryobs.vg.VillageGenesis;
import cn.ykcryobs.vg.init.ModDataPackRegistries;
import cn.ykcryobs.vg.utils.BoundingBox2D;
import cn.ykcryobs.vg.villageSystem.facility.interfaces.IFacilityType;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 村庄信息核心类，记录村庄的基本信息和状态
 *
 * @author llykff
 */
public class VillageData {

    private static final Logger LOGGER = LogUtils.getLogger();

    // 村庄唯一标识符
    private UUID villageId;
    // 村庄名称
    private String villageName;
    // 村庄等级
    private int villageLevel;
    // 村庄经验值
    private int villageExp;
    // 村庄人口数量
    private int population;
    // 村庄边界中心点
    private BlockPos centerPos;
    // 村庄边界大小 (半径)
    private BoundingBox2D boundingBox;
    // 村庄创建时间
    private long createdTime;
    // 最后更新时间
    private long lastUpdateTime;
    // 村庄发展状态 (发展/衰退/废弃)
    private VillageStatus status;
    // 村庄村民列表
    private Set<UUID> villagers;
    // 村庄设施列表
    private Map<IFacilityType, List<VillageFacility>> facilities;

    public VillageData() {
        this.villageId = null;
        this.villageName = null;
        this.centerPos = null;
        this.boundingBox = null;
        this.villageLevel = 0;
        this.villageExp = 0;
        this.population = 0;
        this.createdTime = System.currentTimeMillis();
        this.lastUpdateTime = System.currentTimeMillis();
        this.status = VillageStatus.DEVELOPING;
        this.villagers = new HashSet<>();
        this.facilities = new HashMap<>();
    }

    public VillageData(BlockPos centerPos, BoundingBox2D boundingBox) {
        this.villageId = UUID.randomUUID();
        this.villageName = null;
        this.centerPos = centerPos;
        this.boundingBox = boundingBox;
        this.villageLevel = 0;
        this.villageExp = 0;
        this.population = 0;
        this.createdTime = System.currentTimeMillis();
        this.lastUpdateTime = System.currentTimeMillis();
        this.status = VillageStatus.DEVELOPING;
        this.villagers = new HashSet<>();
        this.facilities = new HashMap<>();
    }

    public static VillageData load(CompoundTag nbt, HolderLookup.Provider provider) {
        HolderLookup.RegistryLookup<IFacilityType> facilityRegistry = provider.lookupOrThrow(
                ModDataPackRegistries.FACILITY_REGISTRY_KEY);

        VillageData villageData = new VillageData();

        villageData.villageId = nbt.getUUID("villageId");
        villageData.villageName = nbt.getString("villageName");
        villageData.villageLevel = nbt.getInt("villageLevel");
        villageData.villageExp = nbt.getInt("villageExp");
        villageData.population = nbt.getInt("population");
        villageData.boundingBox = BoundingBox2D.deserializeNBT(nbt.getCompound("boundingBox"));
        villageData.createdTime = nbt.getLong("createdTime");
        villageData.lastUpdateTime = nbt.getLong("lastUpdateTime");

        // 加载中心位置
        villageData.centerPos = BlockPos.of(nbt.getLong("centerPos"));

        // 加载村庄状态
        String statusStr = nbt.getString("status");
        villageData.status = VillageStatus.valueOf(statusStr);

        // 加载村民列表
        villageData.villagers = new HashSet<>();
        if (nbt.contains("villagers", Tag.TAG_LIST)) {
            ListTag villagerList = nbt.getList("villagers", Tag.TAG_STRING);
            for (Tag tag : villagerList) {
                StringTag villagerIdTag = (StringTag) tag;
                villageData.villagers.add(UUID.fromString(villagerIdTag.getAsString()));
            }
        }

        // 加载设施列表
        villageData.facilities = new HashMap<>();
        if (nbt.contains("facilities", Tag.TAG_LIST)) {
            ListTag facilityList = nbt.getList("facilities", Tag.TAG_COMPOUND);
            for (Tag tag : facilityList) {
                CompoundTag typeTag = (CompoundTag) tag;
                String typeStr = typeTag.getString("facilityType");
                ResourceKey<IFacilityType> facilityTypeKey = ResourceKey.create(
                        ModDataPackRegistries.FACILITY_REGISTRY_KEY,
                        ResourceLocation.fromNamespaceAndPath(VillageGenesis.MOD_ID,
                                "village/facilities/" + typeStr));
                IFacilityType facilityType = facilityRegistry.get(facilityTypeKey)
                        .map(Holder.Reference::value).orElse(null);
                if (facilityType == null) {
                    LOGGER.warn("Cannot find facility type: {}", typeStr);
                    continue;
                }
                ListTag instanceListTag = typeTag.getList("instances", Tag.TAG_COMPOUND);
                List<VillageFacility> facilityInstances = new ArrayList<>();
                for (Tag instanceTag : instanceListTag) {
                    CompoundTag instanceNbt = (CompoundTag) instanceTag;
                    VillageFacility facility = VillageFacility.deserializeNBT(instanceNbt,
                            facilityType);
                    facilityInstances.add(facility);
                }
                villageData.facilities.put(facilityType, facilityInstances);
            }
        }
        return villageData;
    }

    /**
     * 获取村庄ID
     *
     * @return 村庄唯一标识符
     */
    public UUID getVillageId() {
        return this.villageId;
    }

    /**
     * 获取村庄名称
     *
     * @return 村庄名称
     */
    public String getVillageName() {
        return this.villageName;
    }

    /**
     * 设置村庄名称
     *
     * @param villageName 新的村庄名称
     */
    public void setVillageName(String villageName) {
        this.villageName = villageName;
        this.markDirty();
    }

    /**
     * 获取村庄等级
     *
     * @return 村庄等级
     */
    public int getVillageLevel() {
        return this.villageLevel;
    }

    /**
     * 设置村庄等级
     *
     * @param villageLevel 新的村庄等级
     */
    public void setVillageLevel(int villageLevel) {
        this.villageLevel = Math.max(1, Math.min(10, villageLevel));
        this.markDirty();
    }

    /**
     * 获取村庄经验值
     *
     * @return 村庄经验值
     */
    public int getVillageExp() {
        return this.villageExp;
    }

    /**
     * 添加村庄经验值
     *
     * @param exp 要添加的经验值
     * @return 是否升级
     */
    public boolean addExp(int exp) {
        this.villageExp += exp;
        // 检查是否升级
        int requiredExp = getRequiredExpForNextLevel();
        if (this.villageExp >= requiredExp && this.villageLevel < 10) {
            this.villageLevel++;
            this.villageExp = 0;
            this.markDirty();
            return true;
        }

        this.markDirty();
        return false;
    }

    /**
     * 获取下一级所需经验值
     *
     * @return 下一级所需经验值
     */
    public int getRequiredExpForNextLevel() {
        if (this.villageLevel >= 10) {
            return Integer.MAX_VALUE; // 最高等级不再需要经验值
        }
        // 计算下一级所需经验值，递增算法
        return this.villageLevel * 100 + (this.villageLevel - 1) * 50;
    }

    /**
     * 获取村庄人口
     *
     * @return 村庄人口数量
     */
    public int getPopulation() {
        return this.population;
    }

    /**
     * 设置村庄人口
     *
     * @param population 新的人口数量
     */
    public void setPopulation(int population) {
        this.population = Math.max(0, population);
        this.markDirty();
    }

    /**
     * 增加村庄人口
     *
     * @param delta 增加的人口数量（可以为负数）
     */
    public void addPopulation(int delta) {
        this.population = Math.max(0, this.population + delta);
        this.markDirty();
    }

    /**
     * 获取村庄中心位置
     *
     * @return 中心坐标
     */
    public BlockPos getCenterPos() {
        return this.centerPos;
    }

    /**
     * 设置村庄中心位置
     *
     * @param centerPos 新的中心坐标
     */
    public void setCenterPos(BlockPos centerPos) {
        this.centerPos = centerPos;
        this.markDirty();
    }

    /**
     * 获取村庄边界半径
     *
     * @return 边界半径
     */
    public BoundingBox2D getBoundingBox() {
        return this.boundingBox;
    }

    /**
     * 设置村庄边界半径
     *
     * @param boundingBox 新的边界半径
     */
    public void setBoundingBox(BoundingBox2D boundingBox) {
        this.boundingBox = boundingBox; // 限制在合理范围内
        this.markDirty();
    }

    /**
     * 检查指定坐标是否在村庄边界内
     *
     * @param x 要检查的X坐标
     * @param z 要检查的Z坐标
     * @return 是否在边界内
     */
    public boolean inVillageBoundary(int x, int z) {
        return this.boundingBox.inSide(x, z);
    }

    /**
     * 检查指定坐标是否在村庄边界内
     *
     * @param pos 要检查的位置
     * @return 是否在边界内
     */
    public boolean inVillageBoundary(BlockPos pos) {
        return this.boundingBox.inSide(pos.getX(), pos.getZ());
    }

    /**
     * 获取村庄状态
     *
     * @return 村庄当前状态
     */
    public VillageStatus getStatus() {
        return this.status;
    }

    /**
     * 设置村庄状态
     *
     * @param status 新的村庄状态
     */
    public void setStatus(VillageStatus status) {
        this.status = status;
        this.markDirty();
    }

    /**
     * 获取村庄设施列表
     *
     * @return 村庄设施列表
     */
    public List<VillageFacility> getFacilitiesFromType(IFacilityType type) {
        return new ArrayList<>(this.facilities.getOrDefault(type, Collections.emptyList()));
    }

    /**
     * 添加村庄设施
     *
     * @param facility 要添加的设施
     */
    public void addFacility(VillageFacility facility) {
        this.facilities.computeIfAbsent(facility.getFacilityType(), k -> new ArrayList<>())
                .add(facility);
        this.markDirty();
    }

    /**
     * 移除村庄设施
     *
     * @param facility 要移除的设施
     */
    public void removeFacility(VillageFacility facility) {
        List<VillageFacility> list = this.facilities.get(facility.getFacilityType());
        if (list != null) {
            list.remove(facility);
        }
        this.markDirty();
    }

    /**
     * 获取村庄创建时间
     *
     * @return 创建时间戳
     */
    public long getCreatedTime() {
        return this.createdTime;
    }

    /**
     * 获取最后更新时间
     *
     * @return 最后更新时间戳
     */
    public long getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    /**
     * 获取村庄信息概览
     *
     * @return 村庄信息字符串
     */
    public String getInfoOverview() {
        return String.format(
                "村庄: %s (等级%d) - 人口: %d - 状态: %s - 中心 : (%d, %d) - 边界 : %s",
                this.villageName, this.villageLevel, this.population, this.status.getDisplayName(),
                this.centerPos.getX(), this.centerPos.getZ(), this.boundingBox.toString());
    }

    /**
     * 标记数据为脏数据，需要保存
     */
    private void markDirty() {
        this.lastUpdateTime = System.currentTimeMillis();
        // 注意：由于VillageData实现了INBTSerializable而不是SavedData，这里不需要调用setDirty()
    }

    /**
     * 获取所有村庄村民UUID
     *
     * @return 所有村民UUID的集合
     */
    public Set<UUID> getVillagers() {
        return villagers;
    }

    /**
     * 添加村庄村民
     *
     * @param villager 要添加的村民UUID
     */
    public void addVillager(UUID villager) {
        this.villagers.add(villager);
    }

    /**
     * 移除村庄村民
     *
     * @param villager 要移除的村民UUID
     */
    public void removeVillager(UUID villager) {
        this.villagers.remove(villager);
    }

    /**
     * 清空所有村民
     */
    public void clearVillagers() {
        this.villagers.clear();
    }

    /**
     * 检查村庄是否包含指定村民
     *
     * @param villager 要检查的村民UUID
     * @return 是否包含该村民
     */
    public boolean hasVillager(UUID villager) {
        return this.villagers.contains(villager);
    }

    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider provider) {
        nbt.putUUID("villageId", this.villageId);
        nbt.putString("villageName", this.villageName);
        nbt.putInt("villageLevel", this.villageLevel);
        nbt.putInt("villageExp", this.villageExp);
        nbt.putInt("population", this.population);
        nbt.put("boundingBox", this.boundingBox.serializeNBT());
        nbt.putLong("createdTime", this.createdTime);
        nbt.putLong("lastUpdateTime", System.currentTimeMillis());

        nbt.putLong("centerPos", this.centerPos.asLong());

        nbt.putString("status", this.status.name());

        // 保存村民列表
        ListTag villagerList = new ListTag();
        for (UUID villagerId : this.villagers) {
            villagerList.add(StringTag.valueOf(villagerId.toString()));
        }
        nbt.put("villagers", villagerList);

        // 保存设施列表
        ListTag facilityList = new ListTag();
        for (Map.Entry<IFacilityType, List<VillageFacility>> entry : this.facilities.entrySet()) {
            IFacilityType facilityType = entry.getKey();
            List<VillageFacility> facilityInstances = entry.getValue();
            CompoundTag typeTag = new CompoundTag();
            typeTag.putString("facilityType", facilityType.getFacilityType());
            ListTag instanceListTag = new ListTag();
            for (VillageFacility facility : facilityInstances) {
                instanceListTag.add(facility.serializeNBT(provider));
            }
            typeTag.put("facilityInstances", instanceListTag);
            facilityList.add(typeTag);
        }

        nbt.put("facilities", facilityList);

        return nbt;
    }

    /**
     * 村庄状态枚举
     */
    public enum VillageStatus {
        /**
         * 发展状态 - 村庄正在积极发展
         */
        DEVELOPING("developing"),
        /**
         * 停滞状态 - 村庄发展停滞
         */
        STAGNANT("stagnant"),
        /**
         * 衰退状态 - 村庄正在衰退
         */
        DECLINING("declining"),
        /**
         * 废弃状态 - 村庄已被废弃
         */
        ABANDONED("abandoned");

        private final String id;

        /**
         * 构造函数
         *
         * @param id id
         */
        VillageStatus(String id) {
            this.id = id;
        }

        /**
         * 获取的显示名称
         *
         * @return 显示名称
         */
        public Component getDisplayName() {
            return Component.translatable("village.village_genesis." + this.id);
        }
    }
}