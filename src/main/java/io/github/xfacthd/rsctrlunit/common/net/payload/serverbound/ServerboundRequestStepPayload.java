package io.github.xfacthd.rsctrlunit.common.net.payload.serverbound;

import io.github.xfacthd.rsctrlunit.common.menu.ControllerMenu;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundRequestStepPayload(int windowId) implements CustomPacketPayload
{
    public static final Type<ServerboundRequestStepPayload> TYPE = Utils.payloadType("serverbound_request_step");
    public static final StreamCodec<ByteBuf, ServerboundRequestStepPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ServerboundRequestStepPayload::windowId,
            ServerboundRequestStepPayload::new
    );

    public void handle(IPayloadContext ctx)
    {
        if (ctx.player().containerMenu instanceof ControllerMenu ctrlMenu && ctrlMenu.containerId == windowId)
        {
            ctrlMenu.requestStep();
        }
    }

    @Override
    public Type<ServerboundRequestStepPayload> type()
    {
        return TYPE;
    }
}
