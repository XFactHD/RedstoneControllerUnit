package io.github.xfacthd.rsctrlunit.common.emulator.opcode;

import io.github.xfacthd.rsctrlunit.common.emulator.interpreter.*;
import io.github.xfacthd.rsctrlunit.common.emulator.util.BitWriteMode;
import io.github.xfacthd.rsctrlunit.common.emulator.util.Constants;

public final class OpcodeHelpers
{
    private OpcodeHelpers() { }

    /**
     * Calculate the lower 11 bits of the target address of an AJMP or ACALL instruction
     */
    public static int calculateAjmpAddress(byte opcodeByte, byte operandByte)
    {
        return (opcodeByte & 0b1110000) << 3 | (operandByte & 0xFF);
    }

    /**
     * Calculate the program counter resulting from an AJMP or ACALL instruction starting at the given PC value
     */
    public static int calculateAjmpTarget(int programCounter, int address)
    {
        return (programCounter & 0b11100000_00000000) | (address & 0b00011111_11111111);
    }

    /**
     * Calculate byte address from bit address
     */
    public static int calculateByteAddressFromBitAddress(int bitAddress)
    {
        int address = bitAddress & 0xFF;
        if (address <= 0x7F)
        {
            return (address / 8) + 0x20;
        }
        else
        {
            return address & 0xF0;
        }
    }

    /**
     * Calculate bit index from bit address
     */
    public static int calculateBitIndexFromBitAddress(int bitAddress)
    {
        int address = bitAddress & 0xFF;
        return address % 8;
    }

    public static int getRegisterAddress(RAM ram, int register)
    {
        byte psw = ram.readByte(Constants.ADDRESS_STATUS_WORD);
        return getRegisterAddress(psw, register);
    }

    public static int getRegisterAddress(byte psw, int register)
    {
        int regCfg = (psw >> 3) & 0b00000011;
        int baseAddress = Constants.REGISTER_BASE_ADDRESS[regCfg];
        return baseAddress + register;
    }

    public static byte readRegisterDirect(RAM ram, int register)
    {
        int address = getRegisterAddress(ram, register);
        return ram.readByte(address);
    }

    public static byte readRegisterIndirect(RAM ram, int register)
    {
        byte regValue = readRegisterDirect(ram, register);
        return ram.readByte(regValue & 0xFF);
    }

    public static void writeRegisterDirect(RAM ram, int register, byte data)
    {
        int address = getRegisterAddress(ram, register);
        ram.writeByte(address, data);
    }

    public static void writeRegisterIndirect(RAM ram, int register, byte data)
    {
        int address = readRegisterDirect(ram, register) & 0xFF;
        ram.writeByte(address, data);
    }

    public static int readDataPointer(RAM ram)
    {
        int lower = ram.read(Constants.ADDRESS_DATA_POINTER_LOWER);
        int upper = ram.read(Constants.ADDRESS_DATA_POINTER_UPPER);
        return (upper << 8) | lower;
    }

    public static void writeDataPointer(RAM ram, int value)
    {
        ram.write(Constants.ADDRESS_DATA_POINTER_LOWER, value);
        ram.write(Constants.ADDRESS_DATA_POINTER_UPPER, value >> 8);
    }

    public static void readModifyWriteAccumulator(RAM ram, ReadModifyWriteOperation operation)
    {
        int value = ram.read(Constants.ADDRESS_ACCUMULATOR);
        value = operation.compute(ram, value);
        ram.write(Constants.ADDRESS_ACCUMULATOR, value);
    }

    public static void readModifyWriteAccumulatorWithArg(RAM ram, byte argument, ReadModifyWriteWithArgOperation operation)
    {
        int value = ram.read(Constants.ADDRESS_ACCUMULATOR);
        value = operation.compute(ram, value, argument);
        ram.write(Constants.ADDRESS_ACCUMULATOR, value);
    }

    public static void readModifyWriteRegister(RAM ram, int register, ReadModifyWriteOperation operation)
    {
        int value = readRegisterDirect(ram, register) & 0xFF;
        value = operation.compute(ram, value);
        ram.write(Constants.ADDRESS_ACCUMULATOR, value);
    }

    public static void readModifyWriteRegisterIndirect(RAM ram, int register, ReadModifyWriteOperation operation)
    {
        int value = readRegisterIndirect(ram, register) & 0xFF;
        value = operation.compute(ram, value);
        writeRegisterIndirect(ram, register, (byte) (value & 0xFF));
    }

    public static void readModifyWriteMemory(RAM ram, int address, ReadModifyWriteOperation operation)
    {
        address &= 0xFF;
        int value = ram.read(address);
        value = operation.compute(ram, value);
        ram.write(address, value);
    }

    public static void add(RAM ram, byte value, int carryIn)
    {
        int acc = ram.read(Constants.ADDRESS_ACCUMULATOR);
        int iValue = value & 0xFF;

        int result = acc + iValue + carryIn;
        int signedResult = toSigned(acc) + toSigned(iValue) + carryIn;

        boolean carry = false;
        if (result > 255)
        {
            result -= 256;
            carry = true;
        }
        ram.writeBit(Constants.BIT_ADDRESS_CARRY, BitWriteMode.of(carry));

        boolean overflow = signedResult < -128 || signedResult > 127;
        ram.writeBit(Constants.BIT_ADDRESS_OVERFLOW, BitWriteMode.of(overflow));

        boolean auxCarry = ((acc & 0xF) + (iValue & 0xF) + carryIn) > 15;
        ram.writeBit(Constants.BIT_ADDRESS_AUX_CARRY, BitWriteMode.of(auxCarry));

        ram.write(Constants.ADDRESS_ACCUMULATOR, result);
    }

    public static void addc(RAM ram, byte value)
    {
        add(ram, value, ram.readBit(Constants.BIT_ADDRESS_CARRY) ? 1 : 0);
    }

    public static void subb(RAM ram, byte value)
    {
        int acc = ram.read(Constants.ADDRESS_ACCUMULATOR);
        int carryIn = ram.readBit(Constants.BIT_ADDRESS_CARRY) ? 1 : 0;
        int iValue = (value & 0xFF) + carryIn;

        int result = acc - iValue;
        int signedResult = toSigned(acc) + toSigned(iValue) + carryIn;

        boolean carry = false;
        if (result < 0)
        {
            result += 256;
            carry = true;
        }
        ram.writeBit(Constants.BIT_ADDRESS_CARRY, BitWriteMode.of(carry));

        boolean overflow = signedResult < -128 || signedResult > 127;
        ram.writeBit(Constants.BIT_ADDRESS_OVERFLOW, BitWriteMode.of(overflow));

        boolean auxCarry = (iValue & 0xF) > (acc & 0xF);
        ram.writeBit(Constants.BIT_ADDRESS_AUX_CARRY, BitWriteMode.of(auxCarry));

        ram.write(Constants.ADDRESS_ACCUMULATOR, result);
    }

    public static void orMem(RAM ram, int address, byte value)
    {
        int mem = ram.read(address, true);
        ram.write(address, mem | (value & 0xFF));
    }

    public static void orAcc(RAM ram, byte value)
    {
        int acc = ram.read(Constants.ADDRESS_ACCUMULATOR);
        ram.write(Constants.ADDRESS_ACCUMULATOR, acc | (value & 0xFF));
    }

    public static void andMem(RAM ram, int address, byte value)
    {
        int mem = ram.read(address, true);
        ram.write(address, mem & (value & 0xFF));
    }

    public static void andAcc(RAM ram, byte value)
    {
        int acc = ram.read(Constants.ADDRESS_ACCUMULATOR);
        ram.write(Constants.ADDRESS_ACCUMULATOR, acc & (value & 0xFF));
    }

    public static void xorMem(RAM ram, int address, byte value)
    {
        int mem = ram.read(address, true);
        ram.write(address, mem ^ (value & 0xFF));
    }

    public static void xorAcc(RAM ram, byte value)
    {
        int acc = ram.read(Constants.ADDRESS_ACCUMULATOR);
        ram.write(Constants.ADDRESS_ACCUMULATOR, acc ^ (value & 0xFF));
    }

    public static int toSigned(int value)
    {
        return value >= 0 && value <= 127 ? value : (value - 256);
    }
}
