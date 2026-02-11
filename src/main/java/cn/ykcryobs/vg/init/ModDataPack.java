package cn.ykcryobs.vg.init;

import cn.ykcryobs.vg.VillageGenesis;
import cn.ykcryobs.vg.villageSystem.facility.types.FacilityType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

/**
 * 数据包注册表注册器
 *
 * @author llykff
 */

@EventBusSubscriber(modid = VillageGenesis.MOD_ID)
public class ModDataPack {

    public static final ResourceKey<Registry<FacilityType>> FACILITY_REGISTRY_KEY = ResourceKey.createRegistryKey(
            ResourceLocation.fromNamespaceAndPath(VillageGenesis.MOD_ID, "village/facilities"));

    @SubscribeEvent
    private static void registerDataPackRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(FACILITY_REGISTRY_KEY, FacilityType.FacilityTypeCodec.DISPATCH_CODEC,
                FacilityType.FacilityTypeCodec.DISPATCH_CODEC);
//        NOTE 涉及同步嘛（第三个参数是否为 null）
    }
}
