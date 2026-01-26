package cn.ykcryobs.vg.config;

import cn.ykcryobs.vg.config.utils.ConfigUtils;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 服务器配置类，包含仅服务器端使用的配置项
 *
 * @author llykff
 */
public class ServerConfig {

    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.IntValue durabilityInspectionInterval;

    public static final ModConfigSpec.IntValue durabilityThresholdToNeedMaintenance;

    public static final ModConfigSpec.IntValue minFactor;
    public static final ModConfigSpec.IntValue maxFactor;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        ConfigUtils.defineCategory(builder, "facilityDurability");
        durabilityInspectionInterval = ConfigUtils.defineInt(builder, "durabilityInspectionInterval", 20 * 30,
                1, Integer.MAX_VALUE, "The interval in ticks.");
        durabilityThresholdToNeedMaintenance = ConfigUtils.defineInt(builder,
                "durabilityThresholdToNeedMaintenance", 30, 0, 100,
                "The durability threshold to need maintenance.(0-100)%");
        ConfigUtils.endCategory(builder);

        ConfigUtils.defineCategory(builder, "economy");
        minFactor = ConfigUtils.defineInt(builder, "minFactor", 0, 0, 1, "The minimum factor in economy.");
        maxFactor = ConfigUtils.defineInt(builder, "maxFactor", 5, 1, 100, "The maximum factor in economy.");
        ConfigUtils.endCategory(builder);

        // 构建配置规范
        SPEC = builder.build();
    }
}