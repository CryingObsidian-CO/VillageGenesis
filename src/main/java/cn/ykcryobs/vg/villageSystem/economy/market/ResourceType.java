package cn.ykcryobs.vg.villageSystem.economy.market;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

/**
 * 资源类型枚举
 *
 * @author llykff
 */
public enum ResourceType implements StringRepresentable {
    /**
     * 无资源类型（默认）
     */
    NONE,
    /**
     * 食物资源类型
     */
    FOOD,
    /**
     * 木头资源类型
     */
    WOOD,
    /**
     * 石头资源类型
     */
    STONE,
    /**
     * 盔甲资源类型
     */
    CLOTH;

    @Override
    public @NotNull String getSerializedName() {
        return name().toLowerCase();
    }
}
