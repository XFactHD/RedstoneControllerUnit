package io.github.xfacthd.rsctrlunit.common.net.payload.clientbound;

import io.github.xfacthd.rsctrlunit.client.util.ClientAccess;
import io.github.xfacthd.rsctrlunit.common.emulator.interpreter.IOPorts;
import io.github.xfacthd.rsctrlunit.common.emulator.interpreter.StatusView;
import io.github.xfacthd.rsctrlunit.common.emulator.util.Constants;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundUpdateStatusPayload(int windowId, byte[] ram, byte[] output, byte[] input, int programCounter) implements CustomPacketPayload
{
    public static final Type<ClientboundUpdateStatusPayload> TYPE = Utils.payloadType("clientbound_update_status");
    public static final StreamCodec<FriendlyByteBuf, ClientboundUpdateStatusPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ClientboundUpdateStatusPayload::windowId,
            ByteBufCodecs.byteArray(Constants.RAM_SIZE),
            ClientboundUpdateStatusPayload::ram,
            ByteBufCodecs.byteArray(4),
            ClientboundUpdateStatusPayload::output,
            ByteBufCodecs.byteArray(4),
            ClientboundUpdateStatusPayload::input,
            ByteBufCodecs.VAR_INT,
            ClientboundUpdateStatusPayload::programCounter,
            ClientboundUpdateStatusPayload::new
    );

    public ClientboundUpdateStatusPayload(int windowId, StatusView view, IOPorts ioPorts)
    {
        this(windowId, view.getRamView(), ioPorts.getPortStatesOut(), ioPorts.getPortStatesIn(), view.getProgramCounter());
    }

    public void handle(@SuppressWarnings("unused") IPayloadContext ctx)
    {
        if (FMLEnvironment.dist.isClient())
        {
            ClientAccess.handleStatusViewUpdate(windowId, ram, output, input, programCounter);
        }
    }

    @Override
    public Type<ClientboundUpdateStatusPayload> type()
    {
        return TYPE;
    }
}
