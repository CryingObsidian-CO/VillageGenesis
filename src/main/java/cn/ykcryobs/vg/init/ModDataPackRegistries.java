package cn.ykcryobs.vg.init;

import cn.ykcryobs.vg.VillageGenesis;
import cn.ykcryobs.vg.villageSystem.facility.interfaces.IFacilityType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

/**
 * @author llykff
 */

@EventBusSubscriber(modid = VillageGenesis.MOD_ID)
public class ModDataPackRegistries {

    public static final ResourceKey<Registry<IFacilityType>> FACILITY_REGISTRY_KEY = ResourceKey.createRegistryKey(
            ResourceLocation.fromNamespaceAndPath(VillageGenesis.MOD_ID,
                    "village/facilities"));


    @SubscribeEvent
    static void registerDataPackRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(FACILITY_REGISTRY_KEY, IFacilityType.DISPATCH_CODEC,
                IFacilityType.DISPATCH_CODEC);
//        NOTE 涉及同步嘛（第三个参数是否为 null）
    }
}
