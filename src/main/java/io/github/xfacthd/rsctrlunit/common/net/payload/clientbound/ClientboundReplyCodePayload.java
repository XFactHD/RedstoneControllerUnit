package io.github.xfacthd.rsctrlunit.common.net.payload.clientbound;

import io.github.xfacthd.rsctrlunit.common.emulator.util.Code;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ClientboundReplyCodePayload(int windowId, Code code) implements CustomPacketPayload {
    public static final Type<ClientboundReplyCodePayload> TYPE = Utils.payloadType("clientbound_reply_rom");
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundReplyCodePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ClientboundReplyCodePayload::windowId,
            Code.STREAM_CODEC,
            ClientboundReplyCodePayload::code,
            ClientboundReplyCodePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
