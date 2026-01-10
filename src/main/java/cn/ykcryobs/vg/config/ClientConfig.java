package cn.ykcryobs.vg.config;

import cn.ykcryobs.vg.config.utils.ConfigUtils;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 客户端配置类，包含仅客户端使用的配置项
 *
 * @author llykff
 */
public class ClientConfig {

    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.IntValue particleRenderInterval;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        particleRenderInterval = ConfigUtils.defineInt(builder, "particleRenderInterval", 20, 0,
                Integer.MAX_VALUE);

        SPEC = builder.build();
    }
}