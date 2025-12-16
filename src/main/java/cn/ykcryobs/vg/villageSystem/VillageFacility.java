package cn.ykcryobs.vg.villageSystem;

import cn.ykcryobs.vg.villageSystem.facility.interfaces.IFacilityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import java.util.UUID;

/**
 * 村庄设施类，代表村庄中的各种设施建筑 设施可以为村庄提供不同的功能和加成效果
 *
 * @author llykff
 */
public class VillageFacility {

    // 设施唯一标识符
    private final UUID facilityId;
    // 设施类型
    private final IFacilityType facilityType;
    // 设施位置
    private BlockPos position;
    // 设施等级
    private int facilityLevel;
    // 设施状态 (正常/损坏/升级中/停用/废弃)
    private FacilityStatus status;
    // 设施效率 (影响产能)
    private double efficiency;

    /**
     * 构造函数，创建新的村庄设施
     *
     * @param facilityType 设施类型
     * @param position     设施位置
     */
    public VillageFacility(IFacilityType facilityType, BlockPos position, int facilityLevel,
            FacilityStatus status, double efficiency) {
        this.facilityId = UUID.randomUUID();
        this.facilityType = facilityType;
        this.facilityLevel = facilityLevel;
        this.status = status;
        this.efficiency = efficiency;
        this.position = position;

    }

    /**
     * 构造函数，用于从NBT数据重建设施
     *
     * @param facilityId    设施ID
     * @param facilityType  设施类型
     * @param position      设施位置
     * @param facilityLevel 设施等级
     * @param status        设施状态
     * @param efficiency    效率
     */
    public VillageFacility(UUID facilityId, IFacilityType facilityType, BlockPos position,
            int facilityLevel, FacilityStatus status, double efficiency) {
        this.facilityId = facilityId;
        this.facilityType = facilityType;
        this.position = position;
        this.facilityLevel = facilityLevel;
        this.status = status;
        this.efficiency = efficiency;
    }

    /**
     * 从NBT数据创建设施实例
     *
     * @param nbt NBT数据
     * @return 设施实例，如果数据无效则返回null
     */
    public static VillageFacility deserializeNBT(CompoundTag nbt, IFacilityType facilityType) {

        UUID facilityId = nbt.getUUID("facilityId");
        int facilityLevel = nbt.getInt("facilityLevel");
        FacilityStatus status = FacilityStatus.valueOf(nbt.getString("status"));
        double efficiency = nbt.getDouble("efficiency");
        // 加载位置信息
        int posX = nbt.getInt("posX");
        int posY = nbt.getInt("posY");
        int posZ = nbt.getInt("posZ");
        BlockPos position = new BlockPos(posX, posY, posZ);

        return new VillageFacility(facilityId, facilityType, position, facilityLevel, status,
                efficiency);
    }


    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        nbt.putUUID("facilityId", this.facilityId);
        nbt.putInt("facilityLevel", this.facilityLevel);
        nbt.putString("status", this.status.name());
        nbt.putDouble("efficiency", this.efficiency);
        // 保存位置信息
        nbt.putInt("posX", this.position.getX());
        nbt.putInt("posY", this.position.getY());
        nbt.putInt("posZ", this.position.getZ());

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
        this.position = position;
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
        this.facilityLevel = facilityLevel;
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
        this.efficiency = efficiency;
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
        this.status = status;
    }

    /**
     * 获取设施类型
     *
     * @return 设施类型
     */
    public IFacilityType getFacilityType() {
        return facilityType;
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
            return Component.translatable("facility.village_genesis." + this.id);
        }
    }
}