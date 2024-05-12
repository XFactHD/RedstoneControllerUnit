package io.github.xfacthd.rsctrlunit.common.emulator.interpreter;

import io.github.xfacthd.rsctrlunit.common.util.Utils;
import net.minecraft.nbt.CompoundTag;

import java.util.Arrays;

// TODO: double-buffer IO ports to allow running the interpreter off-thread
public final class IOPorts
{
    private final byte[] portStatesOut = new byte[4];
    private final byte[] portStatesIn = new byte[4];

    IOPorts() { }

    byte readInputPort(int port)
    {
        return portStatesIn[port];
    }

    public void writeInputPort(int port, byte value)
    {
        portStatesIn[port] = value;
    }

    public byte readOutputPort(int port)
    {
        return portStatesOut[port];
    }

    void writeOutputPort(int port, byte value)
    {
        portStatesOut[port] = value;
    }

    void load(CompoundTag tag)
    {
        Utils.copyByteArray(tag.getByteArray("out"), portStatesOut);
        Utils.copyByteArray(tag.getByteArray("in"), portStatesIn);
    }

    public byte[] getPortStatesOut()
    {
        return portStatesOut;
    }

    public byte[] getPortStatesIn()
    {
        return portStatesIn;
    }

    CompoundTag save()
    {
        CompoundTag tag = new CompoundTag();
        tag.putByteArray("out", Arrays.copyOf(portStatesOut, portStatesOut.length));
        tag.putByteArray("in", Arrays.copyOf(portStatesIn, portStatesIn.length));
        return tag;
    }
}
