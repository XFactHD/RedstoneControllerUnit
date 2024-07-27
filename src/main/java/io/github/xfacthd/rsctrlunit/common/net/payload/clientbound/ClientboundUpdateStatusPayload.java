package io.github.xfacthd.rsctrlunit.common.net.payload.clientbound;

import io.github.xfacthd.rsctrlunit.client.util.ClientAccess;
import io.github.xfacthd.rsctrlunit.common.emulator.interpreter.IOPorts;
import io.github.xfacthd.rsctrlunit.common.emulator.interpreter.Interpreter;
import io.github.xfacthd.rsctrlunit.common.emulator.util.Constants;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundUpdateStatusPayload(int windowId, InterpreterState state) implements CustomPacketPayload
{
    public static final Type<ClientboundUpdateStatusPayload> TYPE = Utils.payloadType("clientbound_update_status");
    public static final StreamCodec<ByteBuf, ClientboundUpdateStatusPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ClientboundUpdateStatusPayload::windowId,
            InterpreterState.STREAM_CODEC,
            ClientboundUpdateStatusPayload::state,
            ClientboundUpdateStatusPayload::new
    );

    public static ClientboundUpdateStatusPayload of(int windowId, Interpreter interpreter)
    {
        return new ClientboundUpdateStatusPayload(windowId, interpreter.readLockGuarded(interp ->
        {
            byte[] ram = interp.getRam().clone();
            byte[] sfr = interp.getSfr().clone();
            IOPorts ports = interp.getIoPorts();
            int programCounter = interp.getProgramCounter();
            return new InterpreterState(ram, sfr, ports.getPortStatesOut(), ports.getPortStatesIn(), programCounter);
        }));
    }

    public void handle(@SuppressWarnings("unused") IPayloadContext ctx)
    {
        if (FMLEnvironment.dist.isClient())
        {
            ClientAccess.handleStatusViewUpdate(windowId, state.ram, state.sfr, state.output, state.input, state.programCounter);
        }
    }

    @Override
    public Type<ClientboundUpdateStatusPayload> type()
    {
        return TYPE;
    }



    public record InterpreterState(byte[] ram, byte[] sfr, byte[] output, byte[] input, int programCounter)
    {
        private static final StreamCodec<ByteBuf, InterpreterState> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.byteArray(Constants.RAM_SIZE),
                InterpreterState::ram,
                ByteBufCodecs.byteArray(Constants.SFR_SIZE),
                InterpreterState::sfr,
                ByteBufCodecs.byteArray(4),
                InterpreterState::output,
                ByteBufCodecs.byteArray(4),
                InterpreterState::input,
                ByteBufCodecs.VAR_INT,
                InterpreterState::programCounter,
                InterpreterState::new
        );
    }
}
