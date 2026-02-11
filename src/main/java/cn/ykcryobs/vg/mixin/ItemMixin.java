package cn.ykcryobs.vg.mixin;

import cn.ykcryobs.vg.dataMap.ItemDataMap;
import cn.ykcryobs.vg.init.ModDataMap;
import cn.ykcryobs.vg.item.ITradableItem;
import cn.ykcryobs.vg.villageSystem.economy.market.ResourceType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * @author llykff
 */
@Mixin(Item.class)
@Implements({@Interface(iface = ITradableItem.class, prefix = "tradable$")})
public abstract class ItemMixin implements ITradableItem {

    @Unique
    public boolean tradable$isTradable() {
        return villageGenesis$getItemDataMap() != null;
    }

    @Unique
    public float tradable$getWorkPoint() {
        if (tradable$isTradable()) {
            return villageGenesis$getItemDataMap().workPoint();
        }
        return 0;
    }

    @Unique
    public ResourceType tradable$getResourceType() {
        if (tradable$isTradable()) {
            return villageGenesis$getItemDataMap().resourceType();
        }
        return ResourceType.NONE;
    }

    @Unique
    private ItemDataMap villageGenesis$getItemDataMap() {
        return BuiltInRegistries.ITEM.wrapAsHolder((Item) (Object) this).getData(ModDataMap.ITEM_DATA_MAP);
    }

}
