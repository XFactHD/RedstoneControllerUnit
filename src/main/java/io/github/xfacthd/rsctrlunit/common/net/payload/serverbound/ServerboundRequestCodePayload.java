package io.github.xfacthd.rsctrlunit.common.net.payload.serverbound;

import io.github.xfacthd.rsctrlunit.common.menu.ProgrammerMenu;
import io.github.xfacthd.rsctrlunit.common.net.payload.clientbound.ClientboundReplyCodePayload;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundRequestCodePayload(int windowId) implements CustomPacketPayload
{
    public static final Type<ServerboundRequestCodePayload> TYPE = Utils.payloadType("serverbound_request_rom");
    public static final StreamCodec<ByteBuf, ServerboundRequestCodePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ServerboundRequestCodePayload::windowId,
            ServerboundRequestCodePayload::new
    );

    public void handle(IPayloadContext ctx)
    {
        if (ctx.player().containerMenu instanceof ProgrammerMenu progMenu && progMenu.containerId == windowId)
        {
            ctx.reply(new ClientboundReplyCodePayload(windowId, progMenu.getBlockTargetCode()));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
