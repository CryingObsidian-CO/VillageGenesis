package cn.ykcryobs.vg.init;

import cn.ykcryobs.vg.VillageGenesis;
import cn.ykcryobs.vg.dataComponents.BoundaryScepterComponent;
import cn.ykcryobs.vg.dataComponents.CurrencyDataComponent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * 数据组件注册器
 *
 * @author llykff
 */
public class ModDataComponents {

    public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(
            Registries.DATA_COMPONENT_TYPE, VillageGenesis.MOD_ID);

    public static final Supplier<DataComponentType<UUID>> BOUNDED_VILLAGE = REGISTRAR.registerComponentType(
            "bounded_village", builder -> builder.persistent(BoundaryScepterComponent.UUID_CODEC)
                    .networkSynchronized(BoundaryScepterComponent.UUID_STREAM_CODEC));
    public static final Supplier<DataComponentType<CurrencyDataComponent>> CURRENCY_DATA = REGISTRAR.registerComponentType(
            "currency_data", builder -> builder.persistent(CurrencyDataComponent.CODEC)
                    .networkSynchronized(CurrencyDataComponent.STREAM_CODEC));

    public static void register(IEventBus modEventBus) {
        REGISTRAR.register(modEventBus);
    }
}
