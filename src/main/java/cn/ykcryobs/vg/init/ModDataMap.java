package cn.ykcryobs.vg.init;

import cn.ykcryobs.vg.VillageGenesis;
import cn.ykcryobs.vg.dataMap.ItemDataMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

/**
 * 注册自定义数据地图
 *
 * @author llykff
 */
@EventBusSubscriber(modid = VillageGenesis.MOD_ID)
public class ModDataMap {

    public static final DataMapType<Item, ItemDataMap> ITEM_DATA_MAP = DataMapType.builder(
            ResourceLocation.fromNamespaceAndPath(VillageGenesis.MOD_ID, "item_data_map"), Registries.ITEM,
            ItemDataMap.CODEC).synced(ItemDataMap.CODEC, false).build();

    @SubscribeEvent
    private static void registerDataMapTypes(RegisterDataMapTypesEvent event) {
        event.register(ITEM_DATA_MAP);
    }
}
