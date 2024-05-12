package io.github.xfacthd.rsctrlunit.common.net.payload.clientbound;

import io.github.xfacthd.rsctrlunit.client.util.ClientAccess;
import io.github.xfacthd.rsctrlunit.common.redstone.port.PortConfig;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import io.github.xfacthd.rsctrlunit.common.util.property.RedstoneType;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundUpdatePortConfigsPayload(int windowId, Direction facing, PortConfig[] configs) implements CustomPacketPayload
{
    public static final Type<ClientboundUpdatePortConfigsPayload> TYPE = Utils.payloadType("clientbound_update_port_configs");
    public static final StreamCodec<FriendlyByteBuf, ClientboundUpdatePortConfigsPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ClientboundUpdatePortConfigsPayload::windowId,
            Direction.STREAM_CODEC,
            ClientboundUpdatePortConfigsPayload::facing,
            RedstoneType.PORT_ARRAY_STREAM_CODEC,
            ClientboundUpdatePortConfigsPayload::configs,
            ClientboundUpdatePortConfigsPayload::new
    );

    public void handle(@SuppressWarnings("unused") IPayloadContext ctx)
    {
        if (FMLEnvironment.dist.isClient())
        {
            ClientAccess.handlePortConfigUpdate(windowId, facing, configs);
        }
    }

    @Override
    public Type<ClientboundUpdatePortConfigsPayload> type()
    {
        return TYPE;
    }
}
