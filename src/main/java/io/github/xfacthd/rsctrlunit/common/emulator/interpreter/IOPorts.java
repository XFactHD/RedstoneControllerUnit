package io.github.xfacthd.rsctrlunit.common.emulator.interpreter;

import net.minecraft.nbt.CompoundTag;

import java.util.concurrent.atomic.AtomicIntegerArray;

public final class IOPorts
{
    private final AtomicIntegerArray portStatesOut = new AtomicIntegerArray(4);
    private final AtomicIntegerArray portStatesIn = new AtomicIntegerArray(4);

    IOPorts() { }

    byte readInputPort(int port)
    {
        return (byte) (portStatesIn.get(port) & 0xFF);
    }

    public void writeInputPort(int port, byte value)
    {
        portStatesIn.set(port, value & 0xFF);
    }

    public byte readOutputPort(int port)
    {
        return (byte) (portStatesOut.get(port) & 0xFF);
    }

    void writeOutputPort(int port, byte value)
    {
        portStatesOut.set(port, value & 0xFF);
    }

    public byte[] getPortStatesOut()
    {
        return transferArray(new byte[4], portStatesOut, TransferHandler.SAVE);
    }

    public byte[] getPortStatesIn()
    {
        return transferArray(new byte[4], portStatesIn, TransferHandler.SAVE);
    }

    void load(CompoundTag tag)
    {
        transferArray(tag.getByteArray("out"), portStatesOut, TransferHandler.LOAD);
        transferArray(tag.getByteArray("in"), portStatesIn, TransferHandler.LOAD);
    }

    CompoundTag save()
    {
        CompoundTag tag = new CompoundTag();
        tag.putByteArray("out", transferArray(new byte[4], portStatesOut, TransferHandler.SAVE));
        tag.putByteArray("in", transferArray(new byte[4], portStatesIn, TransferHandler.SAVE));
        return tag;
    }



    private static byte[] transferArray(byte[] array, AtomicIntegerArray atomicArray, TransferHandler handler)
    {
        for (int i = 0; i < atomicArray.length(); i++)
        {
            handler.handle(array, atomicArray, i);
        }
        return array;
    }

    private interface TransferHandler
    {
        TransferHandler LOAD = (array, atomicArray, idx) -> atomicArray.set(idx, array[idx] & 0xFF);
        TransferHandler SAVE = (array, atomicArray, idx) -> array[idx] = (byte) (atomicArray.get(idx) & 0xFF);

        void handle(byte[] array, AtomicIntegerArray atomicArray, int idx);
    }
}
