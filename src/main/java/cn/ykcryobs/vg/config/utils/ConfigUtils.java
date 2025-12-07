package cn.ykcryobs.vg.config.utils;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.function.Predicate;

/**
 * 配置工具类，提供配置相关的辅助方法
 *
 * @author VillageGenesis Team
 */
public class ConfigUtils {

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * 创建布尔值配置项
     *
     * @param builder      配置构建器
     * @param name         配置项名称
     * @param defaultValue 默认值
     * @param comment      配置项注释
     * @return 布尔值配置项
     */
    public static ModConfigSpec.BooleanValue defineBoolean(ModConfigSpec.Builder builder,
            String name, boolean defaultValue, String comment) {
        return builder.comment(comment).define(name, defaultValue);
    }

    /**
     * 创建布尔值配置项（带验证）
     *
     * @param builder      配置构建器
     * @param name         配置项名称
     * @param defaultValue 默认值
     * @param comment      配置项注释
     * @param validator    验证器
     * @return 布尔值配置项
     */
    public static ModConfigSpec.ConfigValue<Boolean> defineBoolean(ModConfigSpec.Builder builder,
            String name, boolean defaultValue, String comment, Predicate<Object> validator) {
        return builder.comment(comment).define(name, defaultValue, validator);
    }

    /**
     * 创建整数配置项
     *
     * @param builder      配置构建器
     * @param name         配置项名称
     * @param defaultValue 默认值
     * @param comment      配置项注释
     * @return 整数配置项
     */
    public static ModConfigSpec.IntValue defineInt(ModConfigSpec.Builder builder, String name,
            int defaultValue, String comment) {
        return builder.comment(comment)
                .defineInRange(name, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * 创建整数配置项（带范围限制）
     *
     * @param builder      配置构建器
     * @param name         配置项名称
     * @param defaultValue 默认值
     * @param min          最小值
     * @param max          最大值
     * @param comment      配置项注释
     * @return 整数配置项
     */
    public static ModConfigSpec.IntValue defineInt(ModConfigSpec.Builder builder, String name,
            int defaultValue, int min, int max, String comment) {
        return builder.comment(comment).defineInRange(name, defaultValue, min, max);
    }

    /**
     * 创建长整数配置项
     *
     * @param builder      配置构建器
     * @param name         配置项名称
     * @param defaultValue 默认值
     * @param comment      配置项注释
     * @return 长整数配置项
     */
    public static ModConfigSpec.LongValue defineLong(ModConfigSpec.Builder builder, String name,
            long defaultValue, String comment) {
        return builder.comment(comment)
                .defineInRange(name, defaultValue, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    /**
     * 创建长整数配置项（带范围限制）
     *
     * @param builder      配置构建器
     * @param name         配置项名称
     * @param defaultValue 默认值
     * @param min          最小值
     * @param max          最大值
     * @param comment      配置项注释
     * @return 长整数配置项
     */
    public static ModConfigSpec.LongValue defineLong(ModConfigSpec.Builder builder, String name,
            long defaultValue, long min, long max, String comment) {
        return builder.comment(comment).defineInRange(name, defaultValue, min, max);
    }

    /**
     * 创建双精度浮点数配置项
     *
     * @param builder      配置构建器
     * @param name         配置项名称
     * @param defaultValue 默认值
     * @param comment      配置项注释
     * @return 双精度浮点数配置项
     */
    public static ModConfigSpec.DoubleValue defineDouble(ModConfigSpec.Builder builder, String name,
            double defaultValue, String comment) {
        return builder.comment(comment)
                .defineInRange(name, defaultValue, -Double.MAX_VALUE, Double.MAX_VALUE);
    }

    /**
     * 创建双精度浮点数配置项（带范围限制）
     *
     * @param builder      配置构建器
     * @param name         配置项名称
     * @param defaultValue 默认值
     * @param min          最小值
     * @param max          最大值
     * @param comment      配置项注释
     * @return 双精度浮点数配置项
     */
    public static ModConfigSpec.DoubleValue defineDouble(ModConfigSpec.Builder builder, String name,
            double defaultValue, double min, double max, String comment) {
        return builder.comment(comment).defineInRange(name, defaultValue, min, max);
    }

    /**
     * 创建字符串配置项
     *
     * @param builder      配置构建器
     * @param name         配置项名称
     * @param defaultValue 默认值
     * @param comment      配置项注释
     * @return 字符串配置项
     */
    public static ModConfigSpec.ConfigValue<String> defineString(ModConfigSpec.Builder builder,
            String name, String defaultValue, String comment) {
        return builder.comment(comment).define(name, defaultValue);
    }

    /**
     * 创建字符串配置项（带验证）
     *
     * @param builder      配置构建器
     * @param name         配置项名称
     * @param defaultValue 默认值
     * @param comment      配置项注释
     * @param validator    验证器
     * @return 字符串配置项
     */
    public static ModConfigSpec.ConfigValue<String> defineString(ModConfigSpec.Builder builder,
            String name, String defaultValue, String comment, Predicate<Object> validator) {
        return builder.comment(comment).define(name, defaultValue, validator);
    }

    /**
     * 创建字符串列表配置项
     *
     * @param builder      配置构建器
     * @param name         配置项名称
     * @param defaultValue 默认值
     * @param comment      配置项注释
     * @return 字符串列表配置项
     */
    public static ModConfigSpec.ConfigValue<List<? extends String>> defineStringList(
            ModConfigSpec.Builder builder, String name, List<? extends String> defaultValue,
            String comment) {
        return builder.comment(comment)
                .defineList(name, defaultValue, () -> "", obj -> obj instanceof String);
    }

    /**
     * 创建枚举配置项
     *
     * @param builder      配置构建器
     * @param name         配置项名称
     * @param enumClass    枚举类
     * @param defaultValue 默认值
     * @param comment      配置项注释
     * @param <T>          枚举类型
     * @return 枚举配置项
     */
    public static <T extends Enum<T>> ModConfigSpec.EnumValue<T> defineEnum(
            ModConfigSpec.Builder builder, String name, Class<T> enumClass, T defaultValue,
            String comment) {
        return builder.comment(comment).defineEnum(name, defaultValue);
    }

    /**
     * 创建配置分类
     *
     * @param builder      配置构建器
     * @param categoryName 分类名称
     * @param comment      分类注释
     */
    public static void defineCategory(ModConfigSpec.Builder builder, String categoryName,
            String comment) {
        builder.comment(comment).push(categoryName);
    }

    /**
     * 结束配置分类
     *
     * @param builder 配置构建器
     */
    public static void endCategory(ModConfigSpec.Builder builder) {
        builder.pop();
    }
}