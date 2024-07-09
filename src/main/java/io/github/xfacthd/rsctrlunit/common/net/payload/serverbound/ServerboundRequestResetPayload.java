package io.github.xfacthd.rsctrlunit.common.net.payload.serverbound;

import io.github.xfacthd.rsctrlunit.common.menu.ControllerMenu;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundRequestResetPayload(int windowId) implements CustomPacketPayload
{
    public static final Type<ServerboundRequestResetPayload> TYPE = Utils.payloadType("serverbound_request_reset");
    public static final StreamCodec<ByteBuf, ServerboundRequestResetPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ServerboundRequestResetPayload::windowId,
            ServerboundRequestResetPayload::new
    );

    public void handle(IPayloadContext ctx)
    {
        if (ctx.player().containerMenu instanceof ControllerMenu ctrlMenu && ctrlMenu.containerId == windowId)
        {
            ctrlMenu.requestReset();
        }
    }

    @Override
    public Type<ServerboundRequestResetPayload> type()
    {
        return TYPE;
    }
}
