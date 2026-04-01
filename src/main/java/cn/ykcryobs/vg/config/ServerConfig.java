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

    public static final ModConfigSpec.IntValue transactionThreadCount;
    public static final ModConfigSpec.IntValue minFactor;
    public static final ModConfigSpec.IntValue maxFactor;

    public static final ModConfigSpec.DoubleValue priceScoreThreshold;
    public static final ModConfigSpec.DoubleValue taxScoreThreshold;
    public static final ModConfigSpec.DoubleValue distanceScoreThreshold;
    public static final ModConfigSpec.DoubleValue quantityScoreThreshold;
    public static final ModConfigSpec.DoubleValue cognitionScoreThreshold;

    public static final ModConfigSpec.DoubleValue priceScoreWeight;
    public static final ModConfigSpec.DoubleValue taxScoreWeight;
    public static final ModConfigSpec.DoubleValue distanceScoreWeight;
    public static final ModConfigSpec.DoubleValue quantityScoreWeight;
    public static final ModConfigSpec.DoubleValue cognitionScoreWeight;

    public static final ModConfigSpec.BooleanValue enableCognitionFilter;

    public static final ModConfigSpec.DoubleValue interVillageTaxRate;
    public static final ModConfigSpec.DoubleValue distancePenaltyPerBlock;

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
        transactionThreadCount = ConfigUtils.defineInt(builder, "transactionThreadCount", 4, 1, 100,
                "The thread count for transaction.");
        minFactor = ConfigUtils.defineInt(builder, "minFactor", 0, 0, 1, "The minimum factor in economy.");
        maxFactor = ConfigUtils.defineInt(builder, "maxFactor", 5, 1, 100, "The maximum factor in economy.");

        priceScoreThreshold = ConfigUtils.defineDouble(builder, "priceScoreThreshold", 100.0, 0.0,
                Double.MAX_VALUE, "Maximum acceptable price score for sellers.");
        taxScoreThreshold = ConfigUtils.defineDouble(builder, "taxScoreThreshold", 50.0, 0.0,
                Double.MAX_VALUE, "Maximum acceptable tax penalty score for sellers.");
        distanceScoreThreshold = ConfigUtils.defineDouble(builder, "distanceScoreThreshold", 1000.0, 0.0,
                Double.MAX_VALUE, "Maximum acceptable distance penalty score for sellers.");
        quantityScoreThreshold = ConfigUtils.defineDouble(builder, "quantityScoreThreshold", 30.0, 0.0, 100.0,
                "Minimum quantity score threshold (0-100).");
        cognitionScoreThreshold = ConfigUtils.defineDouble(builder, "cognitionScoreThreshold", 10.0, 0.0,
                100.0, "Minimum cognition score threshold (0-100).");

        priceScoreWeight = ConfigUtils.defineDouble(builder, "priceScoreWeight", 0.4, 0.0, 1.0,
                "Weight for price score in final seller ranking (0.0-1.0).");
        taxScoreWeight = ConfigUtils.defineDouble(builder, "taxScoreWeight", 0.2, 0.0, 1.0,
                "Weight for tax score in final seller ranking (0.0-1.0).");
        distanceScoreWeight = ConfigUtils.defineDouble(builder, "distanceScoreWeight", 0.2, 0.0, 1.0,
                "Weight for distance score in final seller ranking (0.0-1.0).");
        quantityScoreWeight = ConfigUtils.defineDouble(builder, "quantityScoreWeight", 0.2, 0.0, 1.0,
                "Weight for quantity score in final seller ranking (0.0-1.0).");
        cognitionScoreWeight = ConfigUtils.defineDouble(builder, "cognitionScoreWeight", 0.0, 0.0, 1.0,
                "Weight for cognition score in final seller ranking (0.0-1.0).");

        enableCognitionFilter = ConfigUtils.defineBoolean(builder, "enableCognitionFilter", false,
                "Enable cognition-based filtering for early-stage villages.");

        interVillageTaxRate = ConfigUtils.defineDouble(builder, "interVillageTaxRate", 0.05, 0.0, 1.0,
                "Base tax rate for inter-village trades (0.0-1.0).");
        distancePenaltyPerBlock = ConfigUtils.defineDouble(builder, "distancePenaltyPerBlock", 0.001, 0.0,
                1.0,
                "Distance penalty per block for trade routes.");

        ConfigUtils.endCategory(builder);

        // 构建配置规范
        SPEC = builder.build();
    }
}