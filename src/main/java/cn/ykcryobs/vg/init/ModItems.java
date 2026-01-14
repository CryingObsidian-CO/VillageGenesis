package cn.ykcryobs.vg.init;

import cn.ykcryobs.vg.VillageGenesis;
import cn.ykcryobs.vg.item.BoundaryScepterItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * 物品注册器
 *
 * @author llykff
 */
public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(VillageGenesis.MOD_ID);

    public static final Supplier<Item> BOUNDARY_SCEPTER = ITEMS.register("boundary_scepter",
            BoundaryScepterItem::new);

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
