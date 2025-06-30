package io.github.xfacthd.rsctrlunit.common.net;

import io.github.xfacthd.rsctrlunit.common.net.payload.clientbound.ClientboundReplyCodePayload;
import io.github.xfacthd.rsctrlunit.common.net.payload.clientbound.ClientboundUpdateCodePayload;
import io.github.xfacthd.rsctrlunit.common.net.payload.clientbound.ClientboundUpdatePortConfigsPayload;
import io.github.xfacthd.rsctrlunit.common.net.payload.clientbound.ClientboundUpdatePortMappingPayload;
import io.github.xfacthd.rsctrlunit.common.net.payload.clientbound.ClientboundUpdateStatusPayload;
import io.github.xfacthd.rsctrlunit.common.net.payload.serverbound.ServerboundControllerActionPayload;
import io.github.xfacthd.rsctrlunit.common.net.payload.serverbound.ServerboundRequestCodePayload;
import io.github.xfacthd.rsctrlunit.common.net.payload.serverbound.ServerboundSetPortConfigPayload;
import io.github.xfacthd.rsctrlunit.common.net.payload.serverbound.ServerboundSetPortMappingPayload;
import io.github.xfacthd.rsctrlunit.common.net.payload.serverbound.ServerboundWriteToTargetPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class NetworkSetup
{
    private static final String NET_VERSION = "1";

    public static void onRegisterPayloadHandlers(final RegisterPayloadHandlersEvent event)
    {
        event.registrar(NET_VERSION)
                .playToClient(
                        ClientboundUpdateStatusPayload.TYPE,
                        ClientboundUpdateStatusPayload.CODEC
                )
                .playToClient(
                        ClientboundUpdateCodePayload.TYPE,
                        ClientboundUpdateCodePayload.STREAM_CODEC
                )
                .playToClient(
                        ClientboundUpdatePortConfigsPayload.TYPE,
                        ClientboundUpdatePortConfigsPayload.STREAM_CODEC
                )
                .playToClient(
                        ClientboundReplyCodePayload.TYPE,
                        ClientboundReplyCodePayload.STREAM_CODEC
                )
                .playToClient(
                        ClientboundUpdatePortMappingPayload.TYPE,
                        ClientboundUpdatePortMappingPayload.STREAM_CODEC
                )
                .playToServer(
                        ServerboundSetPortConfigPayload.TYPE,
                        ServerboundSetPortConfigPayload.STREAM_CODEC,
                        ServerboundSetPortConfigPayload::handle
                )
                .playToServer(
                        ServerboundControllerActionPayload.TYPE,
                        ServerboundControllerActionPayload.STREAM_CODEC,
                        ServerboundControllerActionPayload::handle
                )
                .playToServer(
                        ServerboundWriteToTargetPayload.TYPE,
                        ServerboundWriteToTargetPayload.STREAM_CODEC,
                        ServerboundWriteToTargetPayload::handle
                )
                .playToServer(
                        ServerboundRequestCodePayload.TYPE,
                        ServerboundRequestCodePayload.STREAM_CODEC,
                        ServerboundRequestCodePayload::handle
                )
                .playToServer(
                        ServerboundSetPortMappingPayload.TYPE,
                        ServerboundSetPortMappingPayload.STREAM_CODEC,
                        ServerboundSetPortMappingPayload::handle
                );
    }



    private NetworkSetup() { }
}
