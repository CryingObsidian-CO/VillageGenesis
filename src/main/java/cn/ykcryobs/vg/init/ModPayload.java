package cn.ykcryobs.vg.init;

import cn.ykcryobs.vg.VillageGenesis;
import cn.ykcryobs.vg.client.networking.ClientVillageInfoPayloadHandler;
import cn.ykcryobs.vg.networking.ServerVillageInfoPayloadHandler;
import cn.ykcryobs.vg.networking.payload.VillageBaseInfoPayload;
import cn.ykcryobs.vg.networking.payload.VillageInfoRequestPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * 注册自定义Payload
 *
 * @author llykff
 */
@EventBusSubscriber(modid = VillageGenesis.MOD_ID)
public class ModPayload {

    @SubscribeEvent
    private static void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1").executesOn(HandlerThread.NETWORK);

        registrar.playBidirectional(VillageBaseInfoPayload.TYPE, VillageBaseInfoPayload.STREAM_CODEC,
                new DirectionalPayloadHandler<>(ClientVillageInfoPayloadHandler::handleVillageBaseInfo,
                        ServerVillageInfoPayloadHandler::handleVillageBaseInfo));

        registrar.playBidirectional(VillageInfoRequestPayload.TYPE, VillageInfoRequestPayload.STREAM_CODEC,
                new DirectionalPayloadHandler<>(ClientVillageInfoPayloadHandler::handleVillageInfoRequest,
                        ServerVillageInfoPayloadHandler::handleVillageInfoRequest));
    }

}
