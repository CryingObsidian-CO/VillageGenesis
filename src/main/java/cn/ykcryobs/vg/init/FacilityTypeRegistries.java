package cn.ykcryobs.vg.init;

import cn.ykcryobs.vg.VillageGenesis;
import cn.ykcryobs.vg.villageSystem.facility.types.FacilityType;
import cn.ykcryobs.vg.villageSystem.facility.types.ThatchedHutType;
import cn.ykcryobs.vg.villageSystem.facility.types.VillageCenterType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

/**
 * @author llykff
 */
@EventBusSubscriber(modid = VillageGenesis.MOD_ID)
public class FacilityTypeRegistries {

    @SubscribeEvent
    public static void registerCodec(DataPackRegistryEvent.NewRegistry event) {
        FacilityType.FacilityTypeCodec.registerCodec("thatched_hut", ThatchedHutType.CODEC);
        FacilityType.FacilityTypeCodec.registerCodec("village_center", VillageCenterType.CODEC);
    }

}
