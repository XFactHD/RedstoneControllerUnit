package io.github.xfacthd.rsctrlunit.common.net.payload.clientbound;

import io.github.xfacthd.rsctrlunit.client.util.ClientAccess;
import io.github.xfacthd.rsctrlunit.common.redstone.RedstoneInterface;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundUpdatePortMappingPayload(int windowId, int[] portMapping) implements CustomPacketPayload
{
    public static final Type<ClientboundUpdatePortMappingPayload> TYPE = Utils.payloadType("clientbound_update_port_mapping");
    public static final StreamCodec<FriendlyByteBuf, ClientboundUpdatePortMappingPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ClientboundUpdatePortMappingPayload::windowId,
            RedstoneInterface.PORT_MAPPING_STREAM_CODEC,
            ClientboundUpdatePortMappingPayload::portMapping,
            ClientboundUpdatePortMappingPayload::new
    );

    public void handle(@SuppressWarnings("unused") IPayloadContext ctx)
    {
        if (FMLEnvironment.dist.isClient())
        {
            ClientAccess.handlePortMappingUpdate(windowId, portMapping);
        }
    }

    @Override
    public Type<ClientboundUpdatePortMappingPayload> type()
    {
        return TYPE;
    }
}
