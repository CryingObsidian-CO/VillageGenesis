package cn.ykcryobs.vg.villageSystem.economy.market;

import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

/**
 * 市场类型枚举
 *
 * @author llykff
 */
public enum MarketType implements StringRepresentable {
    LOCAL_ONLY, GLOBAL_ONLY, BOTH;

    @Override
    public @NotNull String getSerializedName() {
        return name().toLowerCase();
    }

    public Component getDisplayName() {
        return Component.translatable("market.village_genesis.market_type." + getSerializedName());
    }

    public boolean isLocal() {
        return this == LOCAL_ONLY || this == BOTH;
    }

    public boolean isGlobal() {
        return this == GLOBAL_ONLY || this == BOTH;
    }
}
