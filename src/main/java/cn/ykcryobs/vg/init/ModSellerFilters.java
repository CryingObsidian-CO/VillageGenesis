package cn.ykcryobs.vg.init;

import cn.ykcryobs.vg.VillageGenesis;
import cn.ykcryobs.vg.villageSystem.economy.trader.sellerFilter.ISellerFilter;
import cn.ykcryobs.vg.villageSystem.economy.trader.sellerFilter.PriceScoreFilter;
import cn.ykcryobs.vg.villageSystem.economy.trader.sellerFilter.QuantityScoreFilter;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * 售卖过滤器注册器
 *
 * @author llykff
 */
public class ModSellerFilters {

    public static final DeferredRegister<ISellerFilter> SELLER_FILTER_REGISTRY_DEFERRED = DeferredRegister.create(
            ModRegistries.SELLER_FILTER_REGISTRY_KEY, VillageGenesis.MOD_ID);

    public static final Supplier<PriceScoreFilter> PRICE_SCORE_FILTER = SELLER_FILTER_REGISTRY_DEFERRED.register(
            "price_score_filter", PriceScoreFilter::new);
    public static final Supplier<QuantityScoreFilter> QUANTITY_SCORE_FILTER = SELLER_FILTER_REGISTRY_DEFERRED.register(
            "quantity_score_filter", QuantityScoreFilter::new);

    public static void register(IEventBus modEventBus) {
        SELLER_FILTER_REGISTRY_DEFERRED.register(modEventBus);
    }

}
