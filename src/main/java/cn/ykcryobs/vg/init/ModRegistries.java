package cn.ykcryobs.vg.init;

import cn.ykcryobs.vg.VillageGenesis;
import cn.ykcryobs.vg.villageSystem.economy.trader.sellerFilter.ISellerFilter;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

/**
 * 注册表注册器
 *
 * @author llykff
 */
@EventBusSubscriber(modid = VillageGenesis.MOD_ID)
public class ModRegistries {

    public static final ResourceKey<Registry<ISellerFilter>> SELLER_FILTER_REGISTRY_KEY = ResourceKey.createRegistryKey(
            VillageGenesis.getIdentifier("seller_filter"));
    public static final Registry<ISellerFilter> SELLER_FILTER_REGISTRY = new RegistryBuilder<>(
            SELLER_FILTER_REGISTRY_KEY).sync(false).create();


    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(SELLER_FILTER_REGISTRY);
    }
}
