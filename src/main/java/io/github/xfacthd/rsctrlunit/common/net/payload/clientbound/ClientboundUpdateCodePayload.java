package io.github.xfacthd.rsctrlunit.common.net.payload.clientbound;

import io.github.xfacthd.rsctrlunit.common.emulator.util.Code;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ClientboundUpdateCodePayload(int windowId, Code code) implements CustomPacketPayload
{
    public static final Type<ClientboundUpdateCodePayload> TYPE = Utils.payloadType("clientbound_update_code");
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateCodePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ClientboundUpdateCodePayload::windowId,
            Code.STREAM_CODEC,
            ClientboundUpdateCodePayload::code,
            ClientboundUpdateCodePayload::new
    );

    @Override
    public Type<ClientboundUpdateCodePayload> type()
    {
        return TYPE;
    }
}
