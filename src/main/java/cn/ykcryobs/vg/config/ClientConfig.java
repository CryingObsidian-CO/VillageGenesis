package cn.ykcryobs.vg.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 客户端配置类，包含仅客户端使用的配置项
 *
 * @author VillageGenesis Team
 */
public class ClientConfig {

    public static final ModConfigSpec SPEC;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        SPEC = builder.build();
    }
}