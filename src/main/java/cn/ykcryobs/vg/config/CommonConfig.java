package cn.ykcryobs.vg.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 通用配置类，包含客户端和服务器共享的配置项
 *
 * @author VillageGenesis Team
 */
public class CommonConfig {

    public static final ModConfigSpec SPEC;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        SPEC = builder.build();
    }
}