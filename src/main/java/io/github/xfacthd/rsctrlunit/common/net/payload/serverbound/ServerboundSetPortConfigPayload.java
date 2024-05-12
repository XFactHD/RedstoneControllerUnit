package io.github.xfacthd.rsctrlunit.common.net.payload.serverbound;

import io.github.xfacthd.rsctrlunit.common.menu.ControllerMenu;
import io.github.xfacthd.rsctrlunit.common.redstone.port.PortConfig;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import io.github.xfacthd.rsctrlunit.common.util.property.RedstoneType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundSetPortConfigPayload(int windowId, int port, PortConfig config) implements CustomPacketPayload
{
    public static final Type<ServerboundSetPortConfigPayload> TYPE = Utils.payloadType("serverbound_set_port_config");
    public static final StreamCodec<FriendlyByteBuf, ServerboundSetPortConfigPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ServerboundSetPortConfigPayload::windowId,
            ByteBufCodecs.BYTE.map(Byte::toUnsignedInt, Integer::byteValue),
            ServerboundSetPortConfigPayload::port,
            RedstoneType.PORT_STREAM_CODEC,
            ServerboundSetPortConfigPayload::config,
            ServerboundSetPortConfigPayload::new
    );

    public void handle(IPayloadContext ctx)
    {
        if (ctx.player().containerMenu instanceof ControllerMenu ctrlMenu && ctrlMenu.containerId == windowId)
        {
            ctrlMenu.setPortConfig(port, config);
        }
    }

    @Override
    public Type<ServerboundSetPortConfigPayload> type()
    {
        return TYPE;
    }
}
