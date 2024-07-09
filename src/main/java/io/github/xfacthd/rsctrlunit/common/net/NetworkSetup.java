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
                .playToClient(
                        ClientboundReplyCodePayload.TYPE,
                        ClientboundReplyCodePayload.STREAM_CODEC,
                        ClientboundReplyCodePayload::handle
                )
                .playToServer(
                        ServerboundSetPortConfigPayload.TYPE,
                        ServerboundSetPortConfigPayload.STREAM_CODEC,
                        ServerboundSetPortConfigPayload::handle
                )
                .playToServer(
                        ServerboundLoadRomPayload.TYPE,
                        ServerboundLoadRomPayload.STREAM_CODEC,
                        ServerboundLoadRomPayload::handle
                )
                .playToServer(
                        ServerboundSaveRomPayload.TYPE,
                        ServerboundSaveRomPayload.STREAM_CODEC,
                        ServerboundSaveRomPayload::handle
                )
                .playToServer(
                        ServerboundClearRomPayload.TYPE,
                        ServerboundClearRomPayload.STREAM_CODEC,
                        ServerboundClearRomPayload::handle
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
                        ServerboundTogglePauseResumePayload.TYPE,
                        ServerboundTogglePauseResumePayload.STREAM_CODEC,
                        ServerboundTogglePauseResumePayload::handle
                )
                .playToServer(
                        ServerboundRequestStepPayload.TYPE,
                        ServerboundRequestStepPayload.STREAM_CODEC,
                        ServerboundRequestStepPayload::handle
                )
                .playToServer(
                        ServerboundRequestResetPayload.TYPE,
                        ServerboundRequestResetPayload.STREAM_CODEC,
                        ServerboundRequestResetPayload::handle
                );
    }



    private NetworkSetup() { }
}
