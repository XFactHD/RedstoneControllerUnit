package io.github.xfacthd.rsctrlunit.common.net.payload.serverbound;

import io.github.xfacthd.rsctrlunit.common.menu.ControllerMenu;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundSaveRomPayload(int windowId) implements CustomPacketPayload
{
    public static final Type<ServerboundSaveRomPayload> TYPE = Utils.payloadType("serverbound_save_rom");
    public static final StreamCodec<ByteBuf, ServerboundSaveRomPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ServerboundSaveRomPayload::windowId,
            ServerboundSaveRomPayload::new
    );

    public void handle(IPayloadContext ctx)
    {
        if (ctx.player().containerMenu instanceof ControllerMenu ctrlMenu && ctrlMenu.containerId == windowId)
        {
            ctrlMenu.saveRomToCard();
        }
    }

    @Override
    public Type<ServerboundSaveRomPayload> type()
    {
        return TYPE;
    }
}
