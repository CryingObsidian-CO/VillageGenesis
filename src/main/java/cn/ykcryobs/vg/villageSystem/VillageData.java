package cn.ykcryobs.vg.villageSystem;

import cn.ykcryobs.vg.VillageGenesis;
import cn.ykcryobs.vg.init.ModDataPackRegistries;
import cn.ykcryobs.vg.villageSystem.facility.interfaces.IFacilityType;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 村庄信息核心类，记录村庄的基本信息和状态
 *
 * @author llykff
 */
public class VillageData implements INBTSerializable<CompoundTag> {

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
    private int boundaryRadius;
    // 村庄创建时间
    private long createdTime;
    // 最后更新时间
    private long lastUpdateTime;
    // 村庄发展状态 (发展/衰退/废弃)
    private VillageStatus status;
    // 村庄设施列表
    private Map<IFacilityType, List<VillageFacility>> facilities;

    public VillageData() {
        this.villageId = null;
        this.villageName = null;
        this.centerPos = null;
        this.boundaryRadius = 0;
        this.villageLevel = 1;
        this.villageExp = 0;
        this.population = 0;
        this.createdTime = System.currentTimeMillis();
        this.lastUpdateTime = System.currentTimeMillis();
        this.status = VillageStatus.DEVELOPING;
        this.facilities = new HashMap<>();
    }

    /**
     * 构造函数，创建新的村庄信息
     *
     * @param villageId      村庄唯一ID
     * @param villageName    村庄名称
     * @param centerPos      村庄中心位置
     * @param boundaryRadius 村庄边界半径
     */
    public VillageData(UUID villageId, String villageName, BlockPos centerPos, int boundaryRadius,
            int population) {
        this.villageId = villageId;
        this.villageName = villageName;
        this.centerPos = centerPos;
        this.boundaryRadius = boundaryRadius;
        this.villageLevel = 1;
        this.villageExp = 0;
        this.population = population;
        this.createdTime = System.currentTimeMillis();
        this.lastUpdateTime = System.currentTimeMillis();
        this.status = VillageStatus.DEVELOPING;
        this.facilities = new HashMap<>();
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag nbt = new CompoundTag();
        nbt.putUUID("villageId", this.villageId);
        nbt.putString("villageName", this.villageName);
        nbt.putInt("villageLevel", this.villageLevel);
        nbt.putInt("villageExp", this.villageExp);
        nbt.putInt("population", this.population);
        nbt.putInt("boundaryRadius", this.boundaryRadius);
        nbt.putLong("createdTime", this.createdTime);
        nbt.putLong("lastUpdateTime", System.currentTimeMillis());

        nbt.putInt("centerX", this.centerPos.getX());
        nbt.putInt("centerY", this.centerPos.getY());
        nbt.putInt("centerZ", this.centerPos.getZ());

        nbt.putString("status", this.status.name());

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

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        HolderLookup.RegistryLookup<IFacilityType> facilityRegistry = provider.lookupOrThrow(
                ModDataPackRegistries.FACILITY_REGISTRY_KEY);

        this.villageId = nbt.getUUID("villageId");
        this.villageName = nbt.getString("villageName");
        this.villageLevel = nbt.getInt("villageLevel");
        this.villageExp = nbt.getInt("villageExp");
        this.population = nbt.getInt("population");
        this.boundaryRadius = nbt.getInt("boundaryRadius");
        this.createdTime = nbt.getLong("createdTime");
        this.lastUpdateTime = nbt.getLong("lastUpdateTime");

        // 加载中心位置
        int centerX = nbt.getInt("centerX");
        int centerY = nbt.getInt("centerY");
        int centerZ = nbt.getInt("centerZ");
        this.centerPos = new BlockPos(centerX, centerY, centerZ);

        // 加载村庄状态
        String statusStr = nbt.getString("status");
        this.status = VillageStatus.valueOf(statusStr);

        // 加载设施列表
        this.facilities = new HashMap<>();
        if (nbt.contains("facilities", Tag.TAG_LIST)) {
            ListTag facilityList = nbt.getList("facilities", Tag.TAG_COMPOUND);
            for (Tag tag : facilityList) {
                CompoundTag typeTag = (CompoundTag) tag;
                String typeStr = typeTag.getString("facilityType");
                ResourceKey<IFacilityType> facilityTypeKey = ResourceKey.create(
                        ModDataPackRegistries.FACILITY_REGISTRY_KEY,
                        ResourceLocation.fromNamespaceAndPath(VillageGenesis.MOD_ID,
                                "village/facilities" + typeStr));
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
                this.facilities.put(facilityType, facilityInstances);
            }
        }

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
    public int getBoundaryRadius() {
        return this.boundaryRadius;
    }

    /**
     * 设置村庄边界半径
     *
     * @param boundaryRadius 新的边界半径
     */
    public void setBoundaryRadius(int boundaryRadius) {
        // TODO 配置边界半径范围
        this.boundaryRadius = Math.max(10, Math.min(100, boundaryRadius)); // 限制在合理范围内
        this.markDirty();
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
        this.facilities.get(facility.getFacilityType()).remove(facility);
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
     * 检查指定位置是否在村庄边界内
     *
     * @param pos 要检查的位置
     * @return 是否在边界内
     */
    public boolean isWithinBoundary(BlockPos pos) {
        double distance = Math.sqrt(Math.pow(pos.getX() - this.centerPos.getX(), 2) + Math.pow(
                pos.getZ() - this.centerPos.getZ(), 2));
        return distance <= this.boundaryRadius;
    }

    /**
     * 获取村庄信息概览
     *
     * @return 村庄信息字符串
     */
    public String getInfoOverview() {
        return String.format(
                "村庄: %s (等级%d) - 人口: %d - 状态: %s - 边界: 中心(%d, %d) 半径%d米",
                this.villageName, this.villageLevel, this.population, this.status.getDisplayName(),
                this.centerPos.getX(), this.centerPos.getZ(), this.boundaryRadius);
    }

    /**
     * 标记数据为脏数据，需要保存
     */
    private void markDirty() {
        this.lastUpdateTime = System.currentTimeMillis();
        // 注意：由于VillageData实现了INBTSerializable而不是SavedData，这里不需要调用setDirty()
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