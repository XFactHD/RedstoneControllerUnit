package io.github.xfacthd.rsctrlunit.common.net.payload.serverbound;

import io.github.xfacthd.rsctrlunit.common.menu.ControllerMenu;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundTogglePortMapRenderPayload(int windowId) implements CustomPacketPayload
{
    public static final Type<ServerboundTogglePortMapRenderPayload> TYPE = Utils.payloadType("serverbound_toggle_port_map");
    public static final StreamCodec<ByteBuf, ServerboundTogglePortMapRenderPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ServerboundTogglePortMapRenderPayload::windowId,
            ServerboundTogglePortMapRenderPayload::new
    );

    public void handle(IPayloadContext ctx)
    {
        if (ctx.player().containerMenu instanceof ControllerMenu ctrlMenu && ctrlMenu.containerId == windowId)
        {
            ctrlMenu.togglePortMapRender();
        }
    }

    @Override
    public Type<ServerboundTogglePortMapRenderPayload> type()
    {
        return TYPE;
    }
}
