package cn.ykcryobs.vg.utils;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.slf4j.Logger;

import java.util.Objects;

/**
 * @author llykff
 */
public class BoundingBox2D {

    private static final Logger LOGGER = LogUtils.getLogger();
    private int minX;
    private int minZ;
    private int maxX;
    private int maxZ;

    public BoundingBox2D(int minX, int minZ, int maxX, int maxZ) {
        this.minX = minX;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxZ = maxZ;
        if (maxX < minX || maxZ < minZ) {
            LOGGER.warn(
                    "BoundingBox2D: maxX < minX || maxZ < minZ, minX: {}, minZ: {}, maxX: {}, maxZ: {}",
                    minX, minZ, maxX, maxZ);
            this.minX = Math.min(minX, maxX);
            this.minZ = Math.min(minZ, maxZ);
            this.maxX = Math.max(minX, maxX);
            this.maxZ = Math.max(minZ, maxZ);
        }
    }

    public BoundingBox2D(BoundingBox boundingBox) {
        this(boundingBox.minX(), boundingBox.minZ(), boundingBox.maxX(), boundingBox.maxZ());
    }

    public static BoundingBox2D deserializeNBT(CompoundTag tag) {
        int minX = tag.getInt("minX");
        int minZ = tag.getInt("minZ");
        int maxX = tag.getInt("maxX");
        int maxZ = tag.getInt("maxZ");
        return new BoundingBox2D(minX, minZ, maxX, maxZ);
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("minX", minX);
        tag.putInt("minZ", minZ);
        tag.putInt("maxX", maxX);
        tag.putInt("maxZ", maxZ);
        return tag;
    }

    /**
     * 检查指定坐标是否在边界内
     *
     * @param x 要检查的X坐标
     * @param z 要检查的Z坐标
     * @return 是否在边界内
     */
    public boolean inSide(int x, int z) {
        return x >= minX && x <= maxX && z >= minZ && z <= maxZ;
    }

    /**
     * 获取边界框的中心X坐标
     *
     * @return 中心X坐标
     */
    public int getCenterX() {
        return (minX + maxX) / 2;
    }

    /**
     * 获取边界框的中心Z坐标
     *
     * @return 中心Z坐标
     */
    public int getCenterZ() {
        return (minZ + maxZ) / 2;
    }

    /**
     * 获取边界框的最小X坐标
     *
     * @return 最小X坐标
     */
    public int getMinX() {
        return minX;
    }

    /**
     * 获取边界框的最小Z坐标
     *
     * @return 最小Z坐标
     */
    public int getMinZ() {
        return minZ;
    }

    /**
     * 获取边界框的最大X坐标
     *
     * @return 最大X坐标
     */
    public int getMaxX() {
        return maxX;
    }

    /**
     * 获取边界框的最大Z坐标
     *
     * @return 最大Z坐标
     */
    public int getMaxZ() {
        return maxZ;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d) - (%d, %d)", minX, minZ, maxX, maxZ);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BoundingBox2D other)) {
            return false;
        }
        return minX == other.minX && minZ == other.minZ && maxX == other.maxX && maxZ == other.maxZ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minX, minZ, maxX, maxZ);
    }

    public boolean contains(BlockPos pos) {
        return inSide(pos.getX(), pos.getZ());
    }
}
