package cn.ykcryobs.vg.villageSystem.facility;

import cn.ykcryobs.vg.init.ModDataPack;
import cn.ykcryobs.vg.villageSystem.VillageManager;
import cn.ykcryobs.vg.villageSystem.facility.types.FacilityType;
import cn.ykcryobs.vg.villageSystem.facility.types.interfaces.IFacilityCategory;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 村庄设施管理器，负责管理村庄内的各种设施 采用单层Map结构：FacilityType -> List<VillageFacility>，便于按类型查询和访问
 *
 * @author llykff
 */
public class FacilityManager {

    private static final Logger LOGGER = LogUtils.getLogger();

    private final UUID villageId;

    // HACK 似乎 category 到 facility 的映射更重要，因为 facility 有 FacilityType::tick 方法来区分不同 type 的逻辑，但是 category 没有，考虑要不要改改
    private final Map<FacilityType, List<VillageFacility>> facilities;

    private final Map<BlockPos, VillageFacility> positionToFacilityMap;

    public FacilityManager(UUID villageId) {
        this.villageId = villageId;
        this.facilities = new HashMap<>();
        this.positionToFacilityMap = new HashMap<>();
        this.markDirty();
    }

    /**
     * 注册设施到管理器，当建筑生成时调用此方法
     *
     * @param facility 要注册的设施
     * @see cn.ykcryobs.vg.villageSystem.VillageData#registerFacility(VillageFacility)
     * @deprecated You never use this method because it may not save the data to the disk. Use
     * {@link cn.ykcryobs.vg.villageSystem.VillageData#registerFacility(VillageFacility)} instead.
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public void registerFacility(VillageFacility facility) {
        if (facility == null) {
            LOGGER.warn("FacilityManager[{}]: Attempting to register null facility", this.villageId);
            return;
        }

        FacilityType facilityType = facility.getFacilityType();
        if (facilityType == null) {
            LOGGER.warn("FacilityManager[{}]: Attempting to register facility with null type",
                    this.villageId);
            return;
        }

        if (this.positionToFacilityMap.containsKey(facility.getPosition())) {
            LOGGER.warn("FacilityManager[{}]: Facility already exists at position {}", this.villageId,
                    facility.getPosition());
            return;
        }

        List<VillageFacility> facilitiesByType = this.facilities.computeIfAbsent(facilityType,
                k -> new ArrayList<>());
        facilitiesByType.add(facility);
        this.positionToFacilityMap.put(facility.getPosition(), facility);

        LOGGER.debug("FacilityManager[{}]: Successfully registered facility {} at {}", this.villageId,
                facilityType.getFacilityTypeIdentifier(), facility.getPosition());

    }

    /**
     * 移除设施
     *
     * @param facility 要移除的设施
     * @see cn.ykcryobs.vg.villageSystem.VillageData#removeFacility(VillageFacility)
     * @deprecated You never use this method because it may not save the data to the disk. Use
     * {@link cn.ykcryobs.vg.villageSystem.VillageData#removeFacility(VillageFacility)} instead.
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public void removeFacility(VillageFacility facility) {
        if (facility == null) {
            LOGGER.warn("FacilityManager[{}]: Attempting to remove null facility", this.villageId);
            return;
        }

        FacilityType facilityType = facility.getFacilityType();
        if (facilityType == null) {
            LOGGER.warn("FacilityManager[{}]: Attempting to remove facility with null type", this.villageId);
            return;
        }

        List<VillageFacility> facilitiesByType = this.facilities.get(facilityType);

        if (facilitiesByType == null) {
            LOGGER.warn("FacilityManager[{}]: No facilities found for type {}", this.villageId,
                    facilityType.getFacilityTypeIdentifier());
            return;
        }

        if (facilitiesByType.remove(facility)) {
            this.positionToFacilityMap.remove(facility.getPosition());

            if (facilitiesByType.isEmpty()) {
                this.facilities.remove(facilityType);
            }

            LOGGER.debug("FacilityManager[{}]: Successfully removed facility {}", this.villageId,
                    facility.getFacilityId());
        }
    }

    /**
     * 根据分类获取所有设施
     *
     * @param category 设施分类
     * @return 该分类下的所有设施
     */
    public Collection<VillageFacility> getFacilitiesByCategory(IFacilityCategory category) {
        if (category == null) {
            return Collections.emptyList();
        }

        List<VillageFacility> result = new ArrayList<>();
        for (Map.Entry<FacilityType, List<VillageFacility>> entry : this.facilities.entrySet()) {
            if (entry.getKey() instanceof IFacilityCategory facilityCategory
                    && facilityCategory.getFacilityCategoryName()
                    .equals(category.getFacilityCategoryName())) {
                result.addAll(entry.getValue());
            }
        }

        return Collections.unmodifiableList(result);
    }

    /**
     * 根据类型获取所有设施
     *
     * @param facilityType 设施类型
     * @return 该类型的所有设施
     */
    public Collection<VillageFacility> getFacilitiesByType(FacilityType facilityType) {
        if (facilityType == null) {
            return Collections.emptyList();
        }

        List<VillageFacility> facilitiesByType = this.facilities.get(facilityType);

        if (facilitiesByType == null) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(facilitiesByType);
    }

    /**
     * 根据位置获取设施
     *
     * @param position 位置
     * @return 该位置的设施，如果不存在返回空
     */
    public Optional<VillageFacility> getFacilityByPosition(BlockPos position) {
        if (position == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.positionToFacilityMap.get(position));
    }

    /**
     * 检查指定位置是否存在设施
     *
     * @param position 位置
     * @return 如果存在设施返回true
     */
    public boolean hasFacilityAtPosition(BlockPos position) {
        return getFacilityByPosition(position).isPresent();
    }

    /**
     * 获取指定分类的设施数量
     *
     * @param category 设施分类
     * @return 设施数量
     */
    public int getFacilityCountByCategory(IFacilityCategory category) {
        if (category == null) {
            return 0;
        }

        int count = 0;
        for (Map.Entry<FacilityType, List<VillageFacility>> entry : this.facilities.entrySet()) {
            if (entry.getKey() instanceof IFacilityCategory facilityCategory && category.equals(
                    facilityCategory)) {
                count += entry.getValue().size();
            }
        }

        return count;
    }

    /**
     * 获取指定类型的设施数量
     *
     * @param facilityType 设施类型
     * @return 设施数量
     */
    public int getFacilityCountByType(FacilityType facilityType) {
        if (facilityType == null) {
            return 0;
        }

        List<VillageFacility> facilitiesByType = this.facilities.get(facilityType);

        if (facilitiesByType == null) {
            return 0;
        }

        return facilitiesByType.size();
    }

    /**
     * 获取所有设施总数
     *
     * @return 设施总数
     */
    public int getFacilityCount() {
        int count = 0;
        for (List<VillageFacility> facilitiesByType : this.facilities.values()) {
            count += facilitiesByType.size();
        }
        return count;
    }

    /**
     * 获取所有注册的分类
     *
     * @return 所有注册的分类
     */
    public Collection<IFacilityCategory> getRegisteredCategories() {
        List<IFacilityCategory> categories = new ArrayList<>();
        for (FacilityType facilityType : this.facilities.keySet()) {
            if (facilityType instanceof IFacilityCategory category) {
                categories.add(category);
            }
        }
        return Collections.unmodifiableList(categories);
    }

    /**
     * 获取所有注册的类型
     *
     * @return 所有注册的类型
     */
    public Collection<FacilityType> getRegisteredTypes() {
        return Collections.unmodifiableSet(this.facilities.keySet());
    }

    /**
     * 获取所有设施
     *
     * @return 所有设施
     */
    public Collection<VillageFacility> getAllFacilities() {
        List<VillageFacility> allFacilities = new ArrayList<>();
        for (List<VillageFacility> facilitiesByType : this.facilities.values()) {
            allFacilities.addAll(facilitiesByType);
        }
        return Collections.unmodifiableCollection(allFacilities);
    }

    /**
     * 序列化NBT数据
     *
     * @param provider 注册提供器
     * @return 序列化后的NBT数据
     */
    public ListTag serializeNBT(HolderLookup.Provider provider) {
        ListTag facilitiesList = new ListTag();
        for (VillageFacility facility : this.getAllFacilities()) {
            CompoundTag facilityTag = facility.serializeNBT(provider);
            facilitiesList.add(facilityTag);
        }
        LOGGER.info("Serialized facility manager: {} facilities", getFacilityCount());

        return facilitiesList;
    }

    /**
     * 反序列化NBT数据
     *
     * @param nbt      NBT数据
     * @param provider 注册提供器
     */
    public void deserializeNBT(CompoundTag nbt, HolderLookup.Provider provider) {
        this.facilities.clear();
        this.positionToFacilityMap.clear();

        HolderLookup.RegistryLookup<FacilityType> facilityRegistry = provider.lookupOrThrow(
                ModDataPack.FACILITY_REGISTRY_KEY);

        ListTag facilitiesList = nbt.getList("facilities", Tag.TAG_COMPOUND);
        int loadedCount = 0;
        for (int i = 0; i < facilitiesList.size(); i++) {
            CompoundTag facilityTag = facilitiesList.getCompound(i);
            String typeName = facilityTag.getString("facilityType");

            ResourceKey<FacilityType> resourceKey = ResourceKey.create(
                    ModDataPack.FACILITY_REGISTRY_KEY, ResourceLocation.parse(typeName));
            Holder<FacilityType> facilityHolder = facilityRegistry.get(resourceKey).orElse(null);

            FacilityType facilityType = facilityHolder != null ? facilityHolder.value() : null;

            if (facilityType != null) {
                VillageFacility facility = VillageFacility.deserializeNBT(facilityTag, facilityType);
                this.registerFacility(facility);
                loadedCount++;
            } else {
                LOGGER.warn("FacilityManager[{}]: Failed to load facility with unknown type {}",
                        this.villageId, typeName);
            }
        }

        LOGGER.info("FacilityManager[{}]: Loaded {} facilities", this.villageId, loadedCount);
    }


    public void tick() {
        for (List<VillageFacility> facilityList : this.facilities.values()) {
            for (VillageFacility facility : facilityList) {
                facility.tick();
            }
        }
    }

    /**
     * 获取所属村庄的ID
     *
     * @return 村庄ID
     */
    public UUID getVillageId() {
        return this.villageId;
    }

    /**
     * 标记设施管理器为脏状态，需要保存
     */
    public void markDirty() {
        VillageManager.markDirty();
    }
}
