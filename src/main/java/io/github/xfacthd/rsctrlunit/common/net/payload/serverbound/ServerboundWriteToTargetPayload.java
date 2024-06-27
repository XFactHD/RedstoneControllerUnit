package io.github.xfacthd.rsctrlunit.common.net.payload.serverbound;

import io.github.xfacthd.rsctrlunit.common.emulator.util.Code;
import io.github.xfacthd.rsctrlunit.common.menu.ProgrammerMenu;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundWriteToTargetPayload(int windowId, Code code) implements CustomPacketPayload
{
    public static final Type<ServerboundWriteToTargetPayload> TYPE = Utils.payloadType("serverbound_write_to_target");
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundWriteToTargetPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ServerboundWriteToTargetPayload::windowId,
            Code.STREAM_CODEC,
            ServerboundWriteToTargetPayload::code,
            ServerboundWriteToTargetPayload::new
    );

    public void handle(IPayloadContext ctx)
    {
        if (ctx.player().containerMenu instanceof ProgrammerMenu progMenu && progMenu.containerId == windowId)
        {
            progMenu.writeToTarget(code);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
