package cn.ykcryobs.vg.config;

import cn.ykcryobs.vg.config.utils.ConfigUtils;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Arrays;
import java.util.List;

/**
 * 通用配置类，包含客户端和服务器共享的配置项
 *
 * @author VillageGenesis Team
 */
public class CommonConfig {

    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.ConfigValue<List<? extends String>> villagePrefixes;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> villageCorePlains;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> villageCoreDesert;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> villageCoreSnowy;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> villageCoreTaiga;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> villageCoreSavanna;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> villageSuffixPlains;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> villageSuffixDesert;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> villageSuffixSnowy;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> villageSuffixTaiga;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> villageSuffixSavanna;
    public static final ModConfigSpec.EnumValue<NameGenerationMode> villageNameGenerationMode;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        ConfigUtils.defineCategory(builder, "villageNameGeneration");
        // 村庄名称生成配置
        villageNameGenerationMode = ConfigUtils.defineEnum(builder, "villageNameGenerationMode",
                NameGenerationMode.class, NameGenerationMode.PREFIX_CORE_SUFFIX);

        // 前缀配置（不随群系变化）
        villagePrefixes = ConfigUtils.defineStringList(builder, "villagePrefixes",
                Arrays.asList("new", "old", "great", "little", "high", "low", "north", "south",
                        "east", "west", "royal", "ancient", "noble"));

        // 平原核心词
        villageCorePlains = ConfigUtils.defineStringList(builder, "villageCorePlains",
                Arrays.asList("green", "meadow", "field", "farm", "grange", "acre", "vale", "hill",
                        "brook", "crossing", "ford", "bridge", "plain", "grass"));

        // 沙漠核心词
        villageCoreDesert = ConfigUtils.defineStringList(builder, "villageCoreDesert",
                Arrays.asList("dune", "dunes", "oasis", "harbor", "haven", "rest", "shade",
                        "shelter", "refuge", "camp", "post", "station", "fort", "tower", "watch",
                        "outpost"));

        // 雪原核心词
        villageCoreSnowy = ConfigUtils.defineStringList(builder, "villageCoreSnowy",
                Arrays.asList("frost", "ice", "snow", "blizzard", "winter", "freeze", "chill",
                        "crystal", "glacier", "peak", "summit", "ridge", "slope", "haven",
                        "shelter", "warmth"));

        // 针叶林核心词
        villageCoreTaiga = ConfigUtils.defineStringList(builder, "villageCoreTaiga",
                Arrays.asList("pine", "spruce", "fir", "hemlock", "forest", "grove", "wood",
                        "thicket", "branch", "needle", "timber", "lumber", "moss", "fern",
                        "wilds"));

        // 热带草原核心词
        villageCoreSavanna = ConfigUtils.defineStringList(builder, "villageCoreSavanna",
                Arrays.asList("acacia", "savanna", "plains", "grassland", "steppe", "prairie",
                        "outback", "scrub", "bush", "veld", "table", "plateau", "mesa", "butte",
                        "ridge", "view"));

        // 平原后缀
        villageSuffixPlains = ConfigUtils.defineStringList(builder, "villageSuffixPlains",
                Arrays.asList("village", "hamlet", "settlement", "town"));

        // 沙漠后缀
        villageSuffixDesert = ConfigUtils.defineStringList(builder, "villageSuffixDesert",
                Arrays.asList("fort", "tower", "watch", "outpost", "station", "post"));

        // 雪原后缀
        villageSuffixSnowy = ConfigUtils.defineStringList(builder, "villageSuffixSnowy",
                Arrays.asList("settlement", "fort", "haven", "shelter", "outpost"));

        // 针叶林后缀
        villageSuffixTaiga = ConfigUtils.defineStringList(builder, "villageSuffixTaiga",
                Arrays.asList("village", "settlement", "hamlet", "lodge", "camp"));

        // 热带草原后缀
        villageSuffixSavanna = ConfigUtils.defineStringList(builder, "villageSuffixSavanna",
                Arrays.asList("village", "settlement", "outpost", "station", "camp"));
        ConfigUtils.endCategory(builder);

        SPEC = builder.build();
    }

    public enum NameGenerationMode {
        PREFIX_CORE_SUFFIX, CORE_SUFFIX, RANDOM
    }
}