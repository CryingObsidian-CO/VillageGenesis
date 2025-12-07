package cn.ykcryobs.vg.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 服务器配置类，包含仅服务器端使用的配置项
 *
 * @author VillageGenesis Team
 */
public class ServerConfig {

    public static final ModConfigSpec SPEC;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        // 构建配置规范
        SPEC = builder.build();
    }
}