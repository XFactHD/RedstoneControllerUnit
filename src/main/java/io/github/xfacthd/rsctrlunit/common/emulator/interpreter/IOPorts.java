package io.github.xfacthd.rsctrlunit.common.emulator.interpreter;

import io.github.xfacthd.rsctrlunit.common.emulator.util.Constants;
import io.github.xfacthd.rsctrlunit.common.util.RCUCodecs;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.concurrent.atomic.AtomicIntegerArray;

public final class IOPorts
{
    private static final int MASK_INT_IN0 = 0b00000100;
    private static final int MASK_TCON_TYPE0 = 0b00000001;
    private static final int MASK_TCON_EDGE0 = 0b00000010;

    private final AtomicIntegerArray portStatesOut = new AtomicIntegerArray(4);
    private final AtomicIntegerArray portStatesIn = new AtomicIntegerArray(4);
    private boolean lastStateInt0 = false;
    private boolean lastStateInt1 = false;

    IOPorts() { }

    void run(RAM ram)
    {
        byte port = readInputPort(3);
        lastStateInt0 = checkExtInterrupt(ram, port, lastStateInt0, 0);
        lastStateInt1 = checkExtInterrupt(ram, port, lastStateInt1, 1);
    }

    private static boolean checkExtInterrupt(RAM ram, byte port, boolean lastState, int idx)
    {
        byte tcon = ram.readByte(Constants.ADDRESS_TCON);
        boolean state = (port & (MASK_INT_IN0 << idx)) != 0;
        boolean edge = (tcon & (MASK_TCON_TYPE0 << (idx * 2))) != 0;
        if ((edge && lastState && !state) || (!edge && !state))
        {
            ram.write(Constants.ADDRESS_TCON, tcon | (MASK_TCON_EDGE0 << (idx * 2)));
        }
        return state;
    }

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

    void load(ValueInput valueInput)
    {
        valueInput.read("out", RCUCodecs.BYTE_ARRAY).ifPresent(arr -> transferArray(arr, portStatesOut, TransferHandler.LOAD));
        valueInput.read("in", RCUCodecs.BYTE_ARRAY).ifPresent(arr -> transferArray(arr, portStatesIn, TransferHandler.LOAD));
        lastStateInt0 = valueInput.getBooleanOr("last_state_int0", false);
        lastStateInt1 = valueInput.getBooleanOr("last_state_int1", false);
    }

    void save(ValueOutput valueOutput)
    {
        valueOutput.store("out", RCUCodecs.BYTE_ARRAY, transferArray(new byte[4], portStatesOut, TransferHandler.SAVE));
        valueOutput.store("in", RCUCodecs.BYTE_ARRAY, transferArray(new byte[4], portStatesIn, TransferHandler.SAVE));
        valueOutput.putBoolean("last_state_int0", lastStateInt0);
        valueOutput.putBoolean("last_state_int1", lastStateInt1);
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
