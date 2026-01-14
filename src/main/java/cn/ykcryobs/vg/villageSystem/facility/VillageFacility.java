package cn.ykcryobs.vg.villageSystem.facility;

import cn.ykcryobs.vg.config.ServerConfig;
import cn.ykcryobs.vg.villageSystem.VillageManager;
import cn.ykcryobs.vg.villageSystem.facility.types.FacilityType;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

import java.util.UUID;

/**
 * 村庄设施类，代表村庄中的各种设施建筑 设施可以为村庄提供不同的功能和加成效果
 *
 * @author llykff
 */
public class VillageFacility {

    private static final Logger LOGGER = LogUtils.getLogger();

    // 设施唯一标识符
    private final UUID facilityId;
    // 设施类型
    private final FacilityType facilityType;
    // 设施位置
    private BlockPos position;
    // 设施等级
    private int facilityLevel;
    // 设施状态
    private FacilityStatus status;
    // 设施效率 (影响产能)
    private double efficiency;
    // 设施当前耐久
    private double durability;
    // 耐久减少tick计数器
    private int durabilityTickCounter;

    public VillageFacility(FacilityType facilityType, BlockPos position) {
        this(UUID.randomUUID(), facilityType, position, 1, FacilityStatus.NORMAL, 1.0,
                facilityType.getMaxDurability(1), 0);
        this.markDirty();
    }

    /**
     * 构造函数，用于从NBT数据重建设施
     *
     * @param facilityId            设施ID
     * @param facilityType          设施类型
     * @param position              设施位置
     * @param facilityLevel         设施等级
     * @param status                设施状态
     * @param efficiency            效率
     * @param durability            当前耐久
     * @param durabilityTickCounter 耐久tick计数器
     */
    public VillageFacility(UUID facilityId, FacilityType facilityType, BlockPos position, int facilityLevel,
            FacilityStatus status, double efficiency, double durability, int durabilityTickCounter) {
        this.facilityId = facilityId;
        this.facilityType = facilityType;
        this.position = position;
        this.facilityLevel = facilityLevel;
        this.status = status;
        this.efficiency = efficiency;
        this.durability = durability;
        this.durabilityTickCounter = durabilityTickCounter;

        LOGGER.debug(
                "Reconstructed/Constructed facility: {} with ID {} at {} with level {}, status {}, durability {}/{} with tick counter {}",
                facilityType, facilityId, position, facilityLevel, status, durability,
                this.getMaxDurability(), durabilityTickCounter);
    }

    /**
     * 从NBT数据创建设施实例
     *
     * @param nbt NBT数据
     * @return 设施实例，如果数据无效则返回null
     */
    public static VillageFacility deserializeNBT(CompoundTag nbt, FacilityType facilityType) {
        UUID facilityId = nbt.getUUID("facilityId");
        int facilityLevel = nbt.getInt("facilityLevel");
        FacilityStatus status = FacilityStatus.valueOf(nbt.getString("status"));
        double efficiency = nbt.getDouble("efficiency");
        BlockPos position = BlockPos.of(nbt.getLong("position"));
        // 读取耐久相关数据
        double durability = nbt.getDouble("durability");
        int durabilityTickCounter = nbt.getInt("durabilityTickCounter");

        LOGGER.debug("Deserialized facility: {} with ID {} from NBT", facilityType, facilityId);
        return new VillageFacility(facilityId, facilityType, position, facilityLevel, status, efficiency,
                durability, durabilityTickCounter);
    }

    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        nbt.putUUID("facilityId", this.facilityId);
        nbt.putInt("facilityLevel", this.facilityLevel);
        nbt.putString("status", this.status.name());
        nbt.putDouble("efficiency", this.efficiency);
        nbt.putLong("position", this.position.asLong());
        // 保存耐久相关数据
        nbt.putDouble("durability", this.durability);
        nbt.putInt("durabilityTickCounter", this.durabilityTickCounter);

        nbt.putString("facilityType", facilityType.getFacilityTypeName());

        return nbt;
    }

    /**
     * 获取设施位置
     *
     * @return 设施位置坐标
     */
    public BlockPos getPosition() {
        return this.position;
    }

    /**
     * 设置设施位置
     *
     * @param position 新的设施位置
     */
    public void setPosition(BlockPos position) {
        LOGGER.debug("Changed position of facility {} from {} to {}", this.facilityId, this.position,
                position);
        this.position = position;
        this.markDirty();
    }

    /**
     * 获取设施等级
     *
     * @return 设施等级
     */
    public int getFacilityLevel() {
        return this.facilityLevel;
    }

    /**
     * 设置设施等级
     *
     * @param facilityLevel 新的设施等级
     */
    public void setFacilityLevel(int facilityLevel) {
        LOGGER.debug("Changed level of facility {} from {} to {}", this.facilityId, this.facilityLevel,
                facilityLevel);
        this.facilityLevel = facilityLevel;
        this.markDirty();
    }

    /**
     * 获取设施效率
     *
     * @return 设施效率
     */
    public double getEfficiency() {
        return efficiency;
    }

    /**
     * 设置设施效率
     *
     * @param efficiency 新的设施效率
     */
    public void setEfficiency(double efficiency) {
        LOGGER.debug("Changed efficiency of facility {} from {} to {}", this.facilityId, this.efficiency,
                efficiency);
        this.efficiency = efficiency;
        this.markDirty();
    }

    /**
     * 获取设施耐久百分比
     *
     * @return 设施耐久百分比的数值（0~100）
     */
    public double getDurabilityPercentage() {
        return (this.durability / this.getMaxDurability()) * 100;
    }

    /**
     * 获取设施状态
     *
     * @return 设施状态
     */
    public FacilityStatus getStatus() {
        return this.status;
    }

    /**
     * 设置设施状态
     *
     * @param status 新的设施状态
     */
    public void setStatus(FacilityStatus status) {
        LOGGER.debug("Changed status of facility {} from {} to {}", this.facilityId, this.status, status);
        this.status = status;
        this.markDirty();
    }

    /**
     * 获取设施当前耐久值
     *
     * @return 当前耐久值
     */
    public double getDurability() {
        return this.durability;
    }

    /**
     * 设置设施耐久值
     *
     * @param durability 新的耐久值
     */
    public void setDurability(double durability) {
        // 确保耐久值在合理范围内
        this.durability = Math.max(0, Math.min(durability, this.getMaxDurability()));
        this.markDirty();
    }

    /**
     * 增加设施耐久值
     *
     * @param delta 耐久值的改变量
     */
    public void addDurability(double delta) {
        this.setDurability(this.durability + delta);
    }

    /**
     * 获取设施最大耐久值
     *
     * @return 最大耐久值
     */
    public double getMaxDurability() {
        return facilityType.getMaxDurability(this.facilityLevel);
    }

    /**
     * 检查耐久状态，更新设施状态
     */
    private void checkDurabilityStatus() {
        if (this.durability <= 0) {
            if (this.status != FacilityStatus.DAMAGED) {
                this.setStatus(FacilityStatus.DAMAGED);
            }
            return;
        }

        // 计算耐久百分比
        double durabilityPercentage = getDurabilityPercentage();
        if (durabilityPercentage <= ServerConfig.durabilityThresholdToNeedMaintenance.get()) {
            // 耐久低于30%，需要维护
            if (this.status != FacilityStatus.NEED_MAINTENANCE && this.status != FacilityStatus.DAMAGED) {
                this.setStatus(FacilityStatus.NEED_MAINTENANCE);
            }
        } else if (durabilityPercentage > ServerConfig.durabilityThresholdToNeedMaintenance.get()
                && this.status == FacilityStatus.NEED_MAINTENANCE) {
            this.setStatus(FacilityStatus.NORMAL);
        }
    }

    /**
     * 获取设施类型
     *
     * @return 设施类型
     */
    public FacilityType getFacilityType() {
        return facilityType;
    }

    /**
     * 获取设施ID
     *
     * @return 设施唯一标识符
     */
    public UUID getFacilityId() {
        return this.facilityId;
    }

    public void tick() {
        if (this.status.isFunctional()) {
            this.getFacilityType().tick();
        }

        if (this.facilityType.isDurabilityAffected() && this.status.isOperational()) {
            this.durabilityTickCounter++;

            if (this.durabilityTickCounter >= ServerConfig.durabilityInspectionInterval.get()) {
                double durabilityDecrease = 0.5 + Math.random();
                this.addDurability(-durabilityDecrease);

                this.checkDurabilityStatus();

                this.durabilityTickCounter = 0;
            }
        }
    }

    public void markDirty() {
        VillageManager.markDirty();
    }

    /**
     * 设施状态枚举
     */
    public enum FacilityStatus {
        /**
         * 正常运行状态
         */
        NORMAL("normal"),
        /**
         * 需维护状态
         */
        NEED_MAINTENANCE("need_maintenance"),
        /**
         * 维护中状态
         */
        MAINTENANCE("maintenance"),
        /**
         * 损坏状态
         */
        DAMAGED("damaged"),
        /**
         * 升级中状态
         */
        UPGRADING("upgrading"),
        /**
         * 停用状态
         */
        DISABLED("disabled"),
        /**
         * 废弃状态
         */
        ABANDONED("abandoned");

        private final String id;

        /**
         * 构造函数
         *
         * @param id 状态ID
         */
        FacilityStatus(String id) {
            this.id = id;
        }

        /**
         * 获取显示名称
         *
         * @return 显示名称
         */
        public Component getDisplayName() {
            return Component.translatable("facility.village_genesis.status." + this.id);
        }

        /**
         * 检查设施是否功能正常
         *
         * @return 如果设施功能可用，则返回true；否则返回false
         */
        public boolean isFunctional() {
            return this == NORMAL || this == NEED_MAINTENANCE;
        }

        /**
         * 检查设施基础计算是否可用
         *
         * @return 如果设施基本计算可用，则返回true；否则返回false
         */
        public boolean isOperational() {
            return this != ABANDONED && this != UPGRADING && this != DAMAGED;
        }

    }
}