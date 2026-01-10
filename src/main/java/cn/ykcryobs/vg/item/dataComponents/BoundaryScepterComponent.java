package cn.ykcryobs.vg.item.dataComponents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

/**
 * 边界权杖组件
 *
 * @author llykff
 */
public class BoundaryScepterComponent {

    public static final Codec<UUID> UUID_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(Codec.STRING.fieldOf("village_id").forGetter(UUID::toString))
                    .apply(instance, UUID::fromString));

    public static final StreamCodec<ByteBuf, UUID> UUID_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, UUID::toString, UUID::fromString);
}

