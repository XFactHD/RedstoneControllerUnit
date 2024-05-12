package io.github.xfacthd.rsctrlunit.common.net;

import io.github.xfacthd.rsctrlunit.common.net.payload.clientbound.*;
import io.github.xfacthd.rsctrlunit.common.net.payload.serverbound.*;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class NetworkSetup
{
    private static final String NET_VERSION = "1";

    public static void onRegisterPayloadHandlers(final RegisterPayloadHandlersEvent event)
    {
        event.registrar(NET_VERSION)
                .playToClient(
                        ClientboundUpdateStatusPayload.TYPE,
                        ClientboundUpdateStatusPayload.CODEC,
                        ClientboundUpdateStatusPayload::handle
                )
                .playToClient(
                        ClientboundUpdateCodePayload.TYPE,
                        ClientboundUpdateCodePayload.STREAM_CODEC,
                        ClientboundUpdateCodePayload::handle
                )
                .playToClient(
                        ClientboundUpdatePortConfigsPayload.TYPE,
                        ClientboundUpdatePortConfigsPayload.STREAM_CODEC,
                        ClientboundUpdatePortConfigsPayload::handle
                )
                .playToServer(
                        ServerboundSetPortConfigPayload.TYPE,
                        ServerboundSetPortConfigPayload.STREAM_CODEC,
                        ServerboundSetPortConfigPayload::handle
                ).playToServer(
                        ServerboundLoadRomPayload.TYPE,
                        ServerboundLoadRomPayload.STREAM_CODEC,
                        ServerboundLoadRomPayload::handle
                )
                .playToServer(
                        ServerboundClearRomPayload.TYPE,
                        ServerboundClearRomPayload.STREAM_CODEC,
                        ServerboundClearRomPayload::handle
                );
    }



    private NetworkSetup() { }
}
