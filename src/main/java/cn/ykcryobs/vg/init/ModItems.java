package cn.ykcryobs.vg.init;

import cn.ykcryobs.vg.VillageGenesis;
import cn.ykcryobs.vg.item.BoundaryScepterItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * @author llykff
 */
public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(
            VillageGenesis.MOD_ID);

    public static final net.neoforged.neoforge.registries.DeferredHolder<net.minecraft.world.item.Item, net.minecraft.world.item.Item> BOUNDARY_SCEPTER = ITEMS.register(
            "boundary_scepter", BoundaryScepterItem::new);

}
