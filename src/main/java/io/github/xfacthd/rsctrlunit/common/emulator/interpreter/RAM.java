package io.github.xfacthd.rsctrlunit.common.emulator.interpreter;

import io.github.xfacthd.rsctrlunit.common.emulator.opcode.OpcodeHelpers;
import io.github.xfacthd.rsctrlunit.common.emulator.util.BitWriteMode;
import io.github.xfacthd.rsctrlunit.common.emulator.util.Constants;

import java.util.Arrays;

public final class RAM
{
    private final byte[] ram = new byte[Constants.RAM_SIZE];
    private final IOPorts ioPorts;
    private boolean updatingParity = false;

    public RAM(IOPorts ioPorts)
    {
        this.ioPorts = ioPorts;
    }

    public byte readByte(int address)
    {
        return readByte(address, false);
    }

    public byte readByte(int address, boolean readOutIfIO)
    {
        return switch (address)
        {
            case Constants.ADDRESS_IO_PORT0 -> readOutIfIO ? ioPorts.readOutputPort(0) : ioPorts.readInputPort(0);
            case Constants.ADDRESS_IO_PORT1 -> readOutIfIO ? ioPorts.readOutputPort(1) : ioPorts.readInputPort(1);
            case Constants.ADDRESS_IO_PORT2 -> readOutIfIO ? ioPorts.readOutputPort(2) : ioPorts.readInputPort(2);
            case Constants.ADDRESS_IO_PORT3 -> readOutIfIO ? ioPorts.readOutputPort(3) : ioPorts.readInputPort(3);
            default -> ram[address];
        };
    }

    public int read(int address)
    {
        return read(address, false);
    }

    public int read(int address, boolean readOutIfIO)
    {
        return readByte(address, readOutIfIO) & 0xFF;
    }

    public boolean readBit(int bitAddress)
    {
        byte data = readByte(OpcodeHelpers.calculateByteAddressFromBitAddress(bitAddress));
        return (data & (1 << OpcodeHelpers.calculateBitIndexFromBitAddress(bitAddress))) != 0;
    }

    public void writeByte(int address, byte value)
    {
        writeByte(address, value, true);
    }

    private void writeByte(int address, byte value, boolean updateParityFromPSW)
    {
        switch (address)
        {
            case Constants.ADDRESS_IO_PORT0 -> ioPorts.writeOutputPort(0, value);
            case Constants.ADDRESS_IO_PORT1 -> ioPorts.writeOutputPort(1, value);
            case Constants.ADDRESS_IO_PORT2 -> ioPorts.writeOutputPort(2, value);
            case Constants.ADDRESS_IO_PORT3 -> ioPorts.writeOutputPort(3, value);
            default ->
            {
                ram[address] = value;
                if (address == Constants.ADDRESS_ACCUMULATOR || (address == Constants.ADDRESS_STATUS_WORD && updateParityFromPSW))
                {
                    updateParity(value);
                }
            }
        }
    }

    public void write(int address, int value)
    {
        writeByte(address, (byte) (value & 0xFF));
    }

    public void writeBit(int bitAddress, BitWriteMode mode)
    {
        int address = OpcodeHelpers.calculateByteAddressFromBitAddress(bitAddress);
        int index = OpcodeHelpers.calculateBitIndexFromBitAddress(bitAddress);

        byte data = readByte(address, true);
        data = switch (mode)
        {
            case SET ->         (byte) ((data |  (1 << index)) & 0xFF);
            case CLEAR ->       (byte) ((data & ~(1 << index)) & 0xFF);
            case COMPLEMENT ->  (byte) ((data ^  (1 << index)) & 0xFF);
        };
        writeByte(address, data, false);
    }

    private void updateParity(byte acc)
    {
        if (updatingParity) return;

        updatingParity = true;
        int parity = readBit(Constants.BIT_ADDRESS_PARITY) ? 1 : 0;
        for (int i = 0; i < 8; i++)
        {
            parity ^= ((acc >> i) & 0x1);
        }
        writeBit(Constants.BIT_ADDRESS_PARITY, BitWriteMode.of(parity != 0));
        updatingParity = false;
    }

    void reset()
    {
        Arrays.fill(ram, (byte) 0);
        ram[Constants.ADDRESS_IO_PORT0] = 0; // Would be 0xFF on real hardware
        ram[Constants.ADDRESS_STACK_POINTER] = Constants.INITIAL_STACK_POINTER;
        ram[Constants.ADDRESS_PCON] &= 0b01110000;
        ram[Constants.ADDRESS_IO_PORT1] = 0; // Would be 0xFF on real hardware
        ram[Constants.ADDRESS_SBUF] = 0; // Would be indeterminate on real hardware
        ram[Constants.ADDRESS_IO_PORT2] = 0; // Would be 0xFF on real hardware
        ram[Constants.ADDRESS_IE] &= 0b01100000;
        ram[Constants.ADDRESS_IO_PORT3] = 0; // Would be 0xFF on real hardware
        ram[Constants.ADDRESS_IP] &= (byte) 0b11100000;
    }

    byte[] getBackingArray()
    {
        return ram;
    }
}
