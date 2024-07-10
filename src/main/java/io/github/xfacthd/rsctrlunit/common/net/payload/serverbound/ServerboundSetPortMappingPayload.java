package io.github.xfacthd.rsctrlunit.common.net.payload.serverbound;

import io.github.xfacthd.rsctrlunit.common.menu.ControllerMenu;
import io.github.xfacthd.rsctrlunit.common.redstone.RedstoneInterface;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundSetPortMappingPayload(int windowId, int[] portMapping) implements CustomPacketPayload
{
    public static final Type<ServerboundSetPortMappingPayload> TYPE = Utils.payloadType("serverbound_set_port_mapping");
    public static final StreamCodec<FriendlyByteBuf, ServerboundSetPortMappingPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ServerboundSetPortMappingPayload::windowId,
            RedstoneInterface.PORT_MAPPING_STREAM_CODEC,
            ServerboundSetPortMappingPayload::portMapping,
            ServerboundSetPortMappingPayload::new
    );

    public void handle(IPayloadContext ctx)
    {
        if (ctx.player().containerMenu instanceof ControllerMenu ctrlMenu && ctrlMenu.containerId == windowId)
        {
            ctrlMenu.setPortMapping(portMapping);
        }
    }

    @Override
    public Type<ServerboundSetPortMappingPayload> type()
    {
        return TYPE;
    }
}
