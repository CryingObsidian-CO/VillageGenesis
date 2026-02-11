package cn.ykcryobs.vg.dataMap;

import cn.ykcryobs.vg.villageSystem.economy.market.ResourceType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;


/**
 * 物品数据映射
 *
 * @author llykff
 */
public record ItemDataMap(float workPoint, ResourceType resourceType) {

    public static final Codec<ItemDataMap> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(Codec.FLOAT.fieldOf("workPoint").forGetter(ItemDataMap::workPoint),
                    StringRepresentable.fromEnum(ResourceType::values).fieldOf("resourceType")
                            .forGetter(ItemDataMap::resourceType)).apply(instance, ItemDataMap::new));
}
