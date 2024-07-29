package io.github.xfacthd.rsctrlunit.common.emulator.interpreter;

import io.github.xfacthd.rsctrlunit.common.emulator.opcode.OpcodeHelpers;
import io.github.xfacthd.rsctrlunit.common.emulator.util.BitWriteMode;
import io.github.xfacthd.rsctrlunit.common.emulator.util.Constants;

import java.util.Arrays;

public final class RAM
{
    private final byte[] ram = new byte[Constants.RAM_SIZE];
    private final byte[] sfr = new byte[Constants.SFR_SIZE];
    private final IOPorts ioPorts;
    private boolean updatingParity = false;

    public RAM(IOPorts ioPorts)
    {
        this.ioPorts = ioPorts;
    }

    public byte readByte(int address)
    {
        return readByte(address, true, false);
    }

    public byte readByte(int address, boolean direct, boolean readOutIfIO)
    {
        if (address < Constants.SFR_START || !direct)
        {
            return ram[address];
        }
        return switch (address)
        {
            case Constants.ADDRESS_IO_PORT0 -> readOutIfIO ? ioPorts.readOutputPort(0) : ioPorts.readInputPort(0);
            case Constants.ADDRESS_IO_PORT1 -> readOutIfIO ? ioPorts.readOutputPort(1) : ioPorts.readInputPort(1);
            case Constants.ADDRESS_IO_PORT2 -> readOutIfIO ? ioPorts.readOutputPort(2) : ioPorts.readInputPort(2);
            case Constants.ADDRESS_IO_PORT3 -> readOutIfIO ? ioPorts.readOutputPort(3) : ioPorts.readInputPort(3);
            default -> sfr[address - Constants.SFR_START];
        };
    }

    public int read(int address)
    {
        return read(address, true, false);
    }

    public int read(int address, boolean direct, boolean readOutIfIO)
    {
        return readByte(address, direct, readOutIfIO) & 0xFF;
    }

    public boolean readBit(int bitAddress)
    {
        return readBit(bitAddress, false);
    }

    public boolean readBit(int bitAddress, boolean readOutIfIO)
    {
        byte data = readByte(OpcodeHelpers.calculateByteAddressFromBitAddress(bitAddress), true, readOutIfIO);
        return (data & (1 << OpcodeHelpers.calculateBitIndexFromBitAddress(bitAddress))) != 0;
    }

    public void writeByte(int address, byte value)
    {
        writeByte(address, value, true);
    }

    public void writeByte(int address, byte value, boolean direct)
    {
        if (address < Constants.SFR_START || !direct)
        {
            ram[address] = value;
            return;
        }
        switch (address)
        {
            case Constants.ADDRESS_IO_PORT0 -> ioPorts.writeOutputPort(0, value);
            case Constants.ADDRESS_IO_PORT1 -> ioPorts.writeOutputPort(1, value);
            case Constants.ADDRESS_IO_PORT2 -> ioPorts.writeOutputPort(2, value);
            case Constants.ADDRESS_IO_PORT3 -> ioPorts.writeOutputPort(3, value);
            default ->
            {
                sfr[address - Constants.SFR_START] = value;
                if (address == Constants.ADDRESS_ACCUMULATOR)
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

        byte data = readByte(address, true, true);
        data = switch (mode)
        {
            case SET ->         (byte) ((data |  (1 << index)) & 0xFF);
            case CLEAR ->       (byte) ((data & ~(1 << index)) & 0xFF);
            case COMPLEMENT ->  (byte) ((data ^  (1 << index)) & 0xFF);
        };
        writeByte(address, data, true);
    }

    private void updateParity(byte acc)
    {
        if (updatingParity) return;

        updatingParity = true;
        int parity = 0;
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
        Arrays.fill(sfr, (byte) 0);
        sfr[Constants.ADDRESS_IO_PORT0 - Constants.SFR_START] = 0; // Would be 0xFF on real hardware
        sfr[Constants.ADDRESS_STACK_POINTER - Constants.SFR_START] = Constants.INITIAL_STACK_POINTER;
        sfr[Constants.ADDRESS_PCON - Constants.SFR_START] &= 0b01110000;
        sfr[Constants.ADDRESS_IO_PORT1 - Constants.SFR_START] = 0; // Would be 0xFF on real hardware
        sfr[Constants.ADDRESS_SBUF - Constants.SFR_START] = 0; // Would be indeterminate on real hardware
        sfr[Constants.ADDRESS_IO_PORT2 - Constants.SFR_START] = 0; // Would be 0xFF on real hardware
        sfr[Constants.ADDRESS_IE - Constants.SFR_START] &= 0b01100000;
        sfr[Constants.ADDRESS_IO_PORT3 - Constants.SFR_START] = 0; // Would be 0xFF on real hardware
        sfr[Constants.ADDRESS_IP - Constants.SFR_START] &= (byte) 0b11100000;
    }

    byte[] getRamArray()
    {
        return ram;
    }

    byte[] getSfrArray()
    {
        return sfr;
    }
}
