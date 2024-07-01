package io.github.xfacthd.rsctrlunit.common.net.payload.serverbound;

import io.github.xfacthd.rsctrlunit.common.menu.ControllerMenu;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundTogglePauseResumePayload(int windowId) implements CustomPacketPayload
{
    public static final Type<ServerboundTogglePauseResumePayload> TYPE = Utils.payloadType("serverbound_toggle_pause_resume");
    public static final StreamCodec<ByteBuf, ServerboundTogglePauseResumePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ServerboundTogglePauseResumePayload::windowId,
            ServerboundTogglePauseResumePayload::new
    );

    public void handle(IPayloadContext ctx)
    {
        if (ctx.player().containerMenu instanceof ControllerMenu ctrlMenu && ctrlMenu.containerId == windowId)
        {
            ctrlMenu.togglePauseResume();
        }
    }

    @Override
    public Type<ServerboundTogglePauseResumePayload> type()
    {
        return TYPE;
    }
}
