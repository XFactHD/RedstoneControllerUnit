package io.github.xfacthd.rsctrlunit.common.emulator.interpreter;

import com.mojang.datafixers.util.Pair;
import io.github.xfacthd.rsctrlunit.common.emulator.opcode.Opcode;
import io.github.xfacthd.rsctrlunit.common.emulator.opcode.OpcodeHelpers;
import io.github.xfacthd.rsctrlunit.common.emulator.util.*;
import io.github.xfacthd.rsctrlunit.common.util.Utils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;

import java.util.Arrays;

public final class Interpreter
{
    private final byte[] rom = new byte[Constants.ROM_SIZE];
    private final IOPorts ioPorts = new IOPorts();
    private final RAM ram = new RAM(ioPorts);
    private final Timers timers = new Timers(ram, ioPorts);
    private Code code = Code.EMPTY;
    private int programCounter = 0;

    public void run()
    {
        timers.run();

        byte romByte = readRomAndIncrementPC();
        Opcode opcode = Opcode.fromRomByte(romByte);
        switch (opcode)
        {
            case NOP -> {}
            case SJMP ->
            {
                int offset = readRomAndIncrementPC();
                programCounter += offset;
            }
            case JMP ->
            {
                int dptr = OpcodeHelpers.readDataPointer(ram);
                programCounter = ram.read(Constants.ADDRESS_ACCUMULATOR) + dptr;
            }
            case AJMP_000, AJMP_001, AJMP_010, AJMP_011, AJMP_100, AJMP_101, AJMP_110, AJMP_111 ->
            {
                int address = OpcodeHelpers.calculateAjmpAddress(romByte, readRomAndIncrementPC());
                programCounter = OpcodeHelpers.calculateAjmpTarget(programCounter, address);
            }
            case ACALL_000, ACALL_001, ACALL_010, ACALL_011, ACALL_100, ACALL_101, ACALL_110, ACALL_111 ->
            {
                int address = OpcodeHelpers.calculateAjmpAddress(romByte, readRomAndIncrementPC());
                pushStateBeforeCall();
                programCounter = OpcodeHelpers.calculateAjmpTarget(programCounter, address);
            }
            case LJMP ->
            {
                int upper = readRomAndIncrementPC() & 0xFF;
                int lower = readRomAndIncrementPC() & 0xFF;
                programCounter = (upper << 8) | lower;
            }
            case LCALL ->
            {
                int upper = readRomAndIncrementPC() & 0xFF;
                int lower = readRomAndIncrementPC() & 0xFF;
                pushStateBeforeCall();
                programCounter = (upper << 8) | lower;
            }
            case RET, RETI ->
            {
                int upper = popStack() & 0xFF;
                int lower = popStack() & 0xFF;
                programCounter = (upper << 8) | lower;
            }
            case JBC ->
            {
                byte bitAddress = readRomAndIncrementPC();
                int offset = readRomAndIncrementPC();
                if (ram.readBit(bitAddress))
                {
                    ram.writeBit(bitAddress, BitWriteMode.CLEAR);
                    programCounter += offset;
                }
            }
            case JB ->
            {
                byte bitAddress = readRomAndIncrementPC();
                int offset = readRomAndIncrementPC();
                if (ram.readBit(bitAddress))
                {
                    programCounter += offset;
                }
            }
            case JNB ->
            {
                byte bitAddress = readRomAndIncrementPC();
                int offset = readRomAndIncrementPC();
                if (!ram.readBit(bitAddress))
                {
                    programCounter += offset;
                }
            }
            case JC ->
            {
                int offset = readRomAndIncrementPC();
                if (ram.readBit(Constants.BIT_ADDRESS_CARRY))
                {
                    programCounter += offset;
                }
            }
            case JNC ->
            {
                int offset = readRomAndIncrementPC();
                if (!ram.readBit(Constants.BIT_ADDRESS_CARRY))
                {
                    programCounter += offset;
                }
            }
            case JZ ->
            {
                int offset = readRomAndIncrementPC();
                if (ram.read(Constants.ADDRESS_ACCUMULATOR) == 0)
                {
                    programCounter += offset;
                }
            }
            case JNZ ->
            {
                int offset = readRomAndIncrementPC();
                if (ram.read(Constants.ADDRESS_ACCUMULATOR) != 0)
                {
                    programCounter += offset;
                }
            }
            case CJNE_ACC_IMM ->
            {
                byte immediate = readRomAndIncrementPC();
                int offset = readRomAndIncrementPC();
                byte acc = ram.readByte(Constants.ADDRESS_ACCUMULATOR);
                if (immediate != acc)
                {
                    programCounter += offset;
                }
                ram.writeBit(Constants.BIT_ADDRESS_CARRY, BitWriteMode.of(acc < immediate));
            }
            case CJNE_ACC_MEM ->
            {
                int address = readRomAndIncrementPC() & 0xFF;
                int offset = readRomAndIncrementPC();
                byte value = ram.readByte(address);
                byte acc = ram.readByte(Constants.ADDRESS_ACCUMULATOR);
                if (value != acc)
                {
                    programCounter += offset;
                }
                ram.writeBit(Constants.BIT_ADDRESS_CARRY, BitWriteMode.of(acc < value));
            }
            case CJNE_IR0_IMM, CJNE_IR1_IMM ->
            {
                byte immediate = readRomAndIncrementPC();
                int offset = readRomAndIncrementPC();
                byte value = OpcodeHelpers.readRegisterIndirect(ram, romByte & 0x1);
                if (value != immediate)
                {
                    programCounter += offset;
                }
                ram.writeBit(Constants.BIT_ADDRESS_CARRY, BitWriteMode.of(value < immediate));
            }
            case CJNE_DR0_IMM, CJNE_DR1_IMM, CJNE_DR2_IMM, CJNE_DR3_IMM, CJNE_DR4_IMM, CJNE_DR5_IMM, CJNE_DR6_IMM, CJNE_DR7_IMM ->
            {
                byte immediate = readRomAndIncrementPC();
                int offset = readRomAndIncrementPC();
                byte value = OpcodeHelpers.readRegisterDirect(ram, romByte & 0b00000111);
                if (value != immediate)
                {
                    programCounter += offset;
                }
                ram.writeBit(Constants.BIT_ADDRESS_CARRY, BitWriteMode.of(value < immediate));
            }
            case DJNZ_MEM ->
            {
                int address = readRomAndIncrementPC() & 0xFF;
                int offset = readRomAndIncrementPC();
                byte value = (byte) (ram.readByte(address) - (byte) 1);
                ram.writeByte(address, value);
                if ((value & 0xFF) != 0)
                {
                    programCounter += offset;
                }
            }
            case DJNZ_DR0, DJNZ_DR1, DJNZ_DR2, DJNZ_DR3, DJNZ_DR4, DJNZ_DR5, DJNZ_DR6, DJNZ_DR7 ->
            {
                int offset = readRomAndIncrementPC();
                int register = romByte & 0b00000111;
                byte value = (byte) ((OpcodeHelpers.readRegisterDirect(ram, register) & 0xFF) - 1);
                OpcodeHelpers.writeRegisterDirect(ram, register, value);
                if ((value & 0xFF) != 0)
                {
                    programCounter += offset;
                }
            }
            case PUSH ->
            {
                int address = readRomAndIncrementPC() & 0xFF;
                pushStack(ram.readByte(address));
            }
            case POP ->
            {
                int address = readRomAndIncrementPC() & 0xFF;
                ram.writeByte(address, popStack());
            }
            case RR ->
                    OpcodeHelpers.readModifyWriteAccumulator(ram, (modRam, value) ->
                    {
                        int newMsb = (value << 7) & 0b10000000;
                        return newMsb | (value >> 1);
                    });
            case RRC ->
                    OpcodeHelpers.readModifyWriteAccumulator(ram, (modRam, value) ->
                    {
                        int newMsb = modRam.readBit(Constants.BIT_ADDRESS_CARRY) ? 0b10000000 : 0;
                        boolean newCarry = (value & 0x1) != 0;
                        modRam.writeBit(Constants.BIT_ADDRESS_CARRY, BitWriteMode.of(newCarry));
                        return newMsb | (value >> 1);
                    });
            case RL ->
                    OpcodeHelpers.readModifyWriteAccumulator(ram, (modRam, value) ->
                    {
                        int newLsb = (value >> 7) & 0x1;
                        return newLsb | (value << 1);
                    });
            case RLC ->
                    OpcodeHelpers.readModifyWriteAccumulator(ram, (modRam, value) ->
                    {
                        int newLsb = modRam.readBit(Constants.BIT_ADDRESS_CARRY) ? 0x1 : 0;
                        boolean newCarry = (value & 0b10000000) != 0;
                        modRam.writeBit(Constants.BIT_ADDRESS_CARRY, BitWriteMode.of(newCarry));
                        return newLsb | (value << 1);
                    });
            case INC_ACC ->
                    OpcodeHelpers.readModifyWriteAccumulator(ram, (modRam, value) -> value + 1);
            case INC_MEM ->
                    OpcodeHelpers.readModifyWriteMemory(ram, readRomAndIncrementPC(), (modRam, value) -> value + 1);
            case INC_IRO, INC_IR1 ->
                    OpcodeHelpers.readModifyWriteRegisterIndirect(ram, romByte & 0x1, (modRam, value) -> value + 1);
            case INC_DR0, INC_DR1, INC_DR2, INC_DR3, INC_DR4, INC_DR5, INC_DR6, INC_DR7 ->
                    OpcodeHelpers.readModifyWriteRegister(ram, romByte & 0b00000111, (modRam, value) -> value + 1);
            case INC_DPTR ->
            {
                int value = OpcodeHelpers.readDataPointer(ram);
                value++;
                OpcodeHelpers.writeDataPointer(ram, value);
            }
            case DEC_ACC ->
                    OpcodeHelpers.readModifyWriteAccumulator(ram, (modRam, value) -> value - 1);
            case DEC_MEM ->
                    OpcodeHelpers.readModifyWriteMemory(ram, readRomAndIncrementPC(), (modRam, value) -> value - 1);
            case DEC_IRO, DEC_IR1 ->
                    OpcodeHelpers.readModifyWriteRegisterIndirect(ram, romByte & 0x1, (modRam, value) -> value - 1);
            case DEC_DR0, DEC_DR1, DEC_DR2, DEC_DR3, DEC_DR4, DEC_DR5, DEC_DR6, DEC_DR7 ->
                    OpcodeHelpers.readModifyWriteRegister(ram, romByte & 0b00000111, (modRam, value) -> value - 1);
            case ADD_IMM ->
                    OpcodeHelpers.add(ram, readRomAndIncrementPC(), 0);
            case ADD_MEM ->
                    OpcodeHelpers.add(ram, ram.readByte(readRomAndIncrementPC() & 0xFF), 0);
            case ADD_IR0, ADD_IR1 ->
                    OpcodeHelpers.add(ram, OpcodeHelpers.readRegisterIndirect(ram, romByte & 0x1), 0);
            case ADD_DR0, ADD_DR1, ADD_DR2, ADD_DR3, ADD_DR4, ADD_DR5, ADD_DR6, ADD_DR7 ->
                    OpcodeHelpers.add(ram, OpcodeHelpers.readRegisterDirect(ram, romByte & 0b00000111), 0);
            case ADDC_IMM ->
                    OpcodeHelpers.addc(ram, readRomAndIncrementPC());
            case ADDC_MEM ->
                    OpcodeHelpers.addc(ram, ram.readByte(readRomAndIncrementPC() & 0xFF));
            case ADDC_IR0, ADDC_IR1 ->
                    OpcodeHelpers.addc(ram, OpcodeHelpers.readRegisterIndirect(ram, romByte & 0x1));
            case ADDC_DR0, ADDC_DR1, ADDC_DR2, ADDC_DR3, ADDC_DR4, ADDC_DR5, ADDC_DR6, ADDC_DR7 ->
                    OpcodeHelpers.addc(ram, OpcodeHelpers.readRegisterDirect(ram, romByte & 0b00000111));
            case ORL_MEM_ACC ->
                    OpcodeHelpers.orMem(ram, readRomAndIncrementPC() & 0xFF, ram.readByte(Constants.ADDRESS_ACCUMULATOR));
            case ORL_MEM_IMM ->
                    OpcodeHelpers.orMem(ram, readRomAndIncrementPC() & 0xFF, readRomAndIncrementPC());
            case ORL_IMM ->
                    OpcodeHelpers.orAcc(ram, readRomAndIncrementPC());
            case ORL_MEM ->
                    OpcodeHelpers.orAcc(ram, ram.readByte(readRomAndIncrementPC() & 0xFF));
            case ORL_IR0, ORL_IR1 ->
                    OpcodeHelpers.orAcc(ram, OpcodeHelpers.readRegisterIndirect(ram, romByte & 0x1));
            case ORL_DR0, ORL_DR1, ORL_DR2, ORL_DR3, ORL_DR4, ORL_DR5, ORL_DR6, ORL_DR7 ->
                    OpcodeHelpers.orAcc(ram, OpcodeHelpers.readRegisterDirect(ram, romByte & 0b00000111));
            case ORL_C_BIT ->
            {
                boolean bit = ram.readBit(readRomAndIncrementPC() & 0xFF);
                boolean carry = ram.readBit(Constants.BIT_ADDRESS_CARRY);
                ram.writeBit(Constants.BIT_ADDRESS_CARRY, BitWriteMode.of(bit || carry));
            }
            case ORL_C_NBIT ->
            {
                boolean bit = ram.readBit(readRomAndIncrementPC() & 0xFF);
                boolean carry = ram.readBit(Constants.BIT_ADDRESS_CARRY);
                ram.writeBit(Constants.BIT_ADDRESS_CARRY, BitWriteMode.of(!bit || carry));
            }
            case ANL_MEM_ACC ->
                    OpcodeHelpers.andMem(ram, readRomAndIncrementPC() & 0xFF, ram.readByte(Constants.ADDRESS_ACCUMULATOR));
            case ANL_MEM_IMM ->
                    OpcodeHelpers.andMem(ram, readRomAndIncrementPC() & 0xFF, readRomAndIncrementPC());
            case ANL_IMM ->
                    OpcodeHelpers.andAcc(ram, readRomAndIncrementPC());
            case ANL_MEM ->
                    OpcodeHelpers.andAcc(ram, ram.readByte(readRomAndIncrementPC() & 0xFF));
            case ANL_IR0, ANL_IR1 ->
                    OpcodeHelpers.andAcc(ram, OpcodeHelpers.readRegisterIndirect(ram, romByte & 0x1));
            case ANL_DR0, ANL_DR1, ANL_DR2, ANL_DR3, ANL_DR4, ANL_DR5, ANL_DR6, ANL_DR7 ->
                    OpcodeHelpers.andAcc(ram, OpcodeHelpers.readRegisterDirect(ram, romByte & 0b00000111));
            case ANL_C_BIT ->
            {
                boolean bit = ram.readBit(readRomAndIncrementPC() & 0xFF);
                boolean carry = ram.readBit(Constants.BIT_ADDRESS_CARRY);
                ram.writeBit(Constants.BIT_ADDRESS_CARRY, BitWriteMode.of(bit && carry));
            }
            case ANL_C_NBIT ->
            {
                boolean bit = ram.readBit(readRomAndIncrementPC() & 0xFF);
                boolean carry = ram.readBit(Constants.BIT_ADDRESS_CARRY);
                ram.writeBit(Constants.BIT_ADDRESS_CARRY, BitWriteMode.of(!bit && carry));
            }
            case XRL_MEM_ACC ->
                    OpcodeHelpers.xorMem(ram, readRomAndIncrementPC() & 0xFF, ram.readByte(Constants.ADDRESS_ACCUMULATOR));
            case XRL_MEM_IMM ->
                    OpcodeHelpers.xorMem(ram, readRomAndIncrementPC() & 0xFF, readRomAndIncrementPC());
            case XRL_IMM ->
                    OpcodeHelpers.xorAcc(ram, readRomAndIncrementPC());
            case XRL_MEM ->
                    OpcodeHelpers.xorAcc(ram, ram.readByte(readRomAndIncrementPC() & 0xFF));
            case XRL_IR0, XRL_IR1 ->
                    OpcodeHelpers.xorAcc(ram, OpcodeHelpers.readRegisterIndirect(ram, romByte & 0x1));
            case XRL_DR0, XRL_DR1, XRL_DR2, XRL_DR3, XRL_DR4, XRL_DR5, XRL_DR6, XRL_DR7 ->
                    OpcodeHelpers.xorAcc(ram, OpcodeHelpers.readRegisterDirect(ram, romByte & 0b00000111));
            case SUBB_IMM ->
                    OpcodeHelpers.subb(ram, readRomAndIncrementPC());
            case SUBB_MEM ->
                    OpcodeHelpers.subb(ram, ram.readByte(readRomAndIncrementPC() & 0xFF));
            case SUBB_IR0, SUBB_IR1 ->
                    OpcodeHelpers.subb(ram, OpcodeHelpers.readRegisterIndirect(ram, romByte & 0x1));
            case SUBB_DR0, SUBB_DR1, SUBB_DR2, SUBB_DR3, SUBB_DR4, SUBB_DR5, SUBB_DR6, SUBB_DR7 ->
                    OpcodeHelpers.subb(ram, OpcodeHelpers.readRegisterDirect(ram, romByte & 0b00000111));
            case MUL_AB ->
            {
                int acc = ram.read(Constants.ADDRESS_ACCUMULATOR);
                int b = ram.read(Constants.ADDRESS_REGISTER_B);
                int result = acc * b;
                ram.writeBit(Constants.BIT_ADDRESS_OVERFLOW, BitWriteMode.of(result > 255));
                ram.writeBit(Constants.BIT_ADDRESS_CARRY, BitWriteMode.CLEAR);
                ram.write(Constants.ADDRESS_ACCUMULATOR, result);
                ram.write(Constants.ADDRESS_REGISTER_B, result >> 8);
            }
            case DIV_AB ->
            {
                ram.writeBit(Constants.BIT_ADDRESS_CARRY, BitWriteMode.CLEAR);

                int acc = ram.read(Constants.ADDRESS_ACCUMULATOR);
                int b = ram.read(Constants.ADDRESS_REGISTER_B);
                if (b > 0)
                {
                    int result = acc / b;
                    ram.write(Constants.ADDRESS_ACCUMULATOR, result);
                    ram.write(Constants.ADDRESS_REGISTER_B, result >> 8);
                    ram.writeBit(Constants.BIT_ADDRESS_OVERFLOW, BitWriteMode.CLEAR);
                }
                else
                {
                    ram.writeBit(Constants.BIT_ADDRESS_OVERFLOW, BitWriteMode.SET);
                }
            }
            case SETB_BIT ->
                    ram.writeBit(readRomAndIncrementPC() & 0xFF, BitWriteMode.SET);
            case SETB_C ->
                    ram.writeBit(Constants.BIT_ADDRESS_CARRY, BitWriteMode.SET);
            case CPL_ACC ->
                    OpcodeHelpers.readModifyWriteAccumulator(ram, (modRam, value) -> ~value);
            case CPL_BIT ->
                    ram.writeBit(readRomAndIncrementPC() & 0xFF, BitWriteMode.COMPLEMENT);
            case CPL_C ->
                    ram.writeBit(Constants.BIT_ADDRESS_CARRY, BitWriteMode.COMPLEMENT);
            case CLR_ACC ->
                    ram.write(Constants.ADDRESS_ACCUMULATOR, 0);
            case CLR_BIT ->
                    ram.writeBit(readRomAndIncrementPC() & 0xFF, BitWriteMode.CLEAR);
            case CLR_C ->
                    ram.writeBit(Constants.BIT_ADDRESS_CARRY, BitWriteMode.CLEAR);
            case SWAP ->
                    OpcodeHelpers.readModifyWriteAccumulator(ram, (modRam, value) ->
                    {
                        int upper = value & 0xF0;
                        int lower = value & 0x0F;
                        return (upper >> 4) | (lower << 4);
                    });
            case XCH_MEM ->
                    OpcodeHelpers.readModifyWriteAccumulatorWithArg(ram, readRomAndIncrementPC(), (modRam, value, arg) ->
                    {
                        int address = arg & 0xFF;
                        int ramValue = modRam.read(address);
                        modRam.write(address, value);
                        return ramValue;
                    });
            case XCH_IR0, XCH_IR1 ->
                    OpcodeHelpers.readModifyWriteAccumulatorWithArg(ram, (byte) (romByte & 0x1), (modRam, value, arg) ->
                    {
                        int register = arg & 0xFF;
                        byte regValue = OpcodeHelpers.readRegisterIndirect(ram, register);
                        OpcodeHelpers.writeRegisterIndirect(ram, register, (byte) (value & 0xFF));
                        return regValue;
                    });
            case XCH_DR0, XCH_DR1, XCH_DR2, XCH_DR3, XCH_DR4, XCH_DR5, XCH_DR6, XCH_DR7 ->
                    OpcodeHelpers.readModifyWriteAccumulatorWithArg(ram, (byte) (romByte & 0b00000111), (modRam, value, arg) ->
                    {
                        int register = arg & 0xFF;
                        byte regValue = OpcodeHelpers.readRegisterDirect(ram, register);
                        OpcodeHelpers.writeRegisterDirect(ram, register, (byte) (value & 0xFF));
                        return regValue;
                    });
            case XCHD_IR0, XCHD_IR1 ->
                    OpcodeHelpers.readModifyWriteAccumulatorWithArg(ram, (byte) (romByte & 0x1), (modRam, value, arg) ->
                    {
                        int register = arg & 0xFF;
                        byte regValue = OpcodeHelpers.readRegisterIndirect(ram, register);
                        byte newRegValue = (byte) ((regValue & 0xF0) | (value & 0x0F));
                        OpcodeHelpers.writeRegisterIndirect(ram, register, newRegValue);
                        return (value & 0xF0) | (regValue & 0x0F);
                    });
            case DA ->
                    OpcodeHelpers.readModifyWriteAccumulator(ram, (modRam, value) ->
                    {
                        if ((value & 0x0F) > 9 || modRam.readBit(Constants.BIT_ADDRESS_AUX_CARRY))
                        {
                            value += 0x06;
                            if (value > 255)
                            {
                                value -= 256;
                                ram.writeBit(Constants.BIT_ADDRESS_CARRY, BitWriteMode.SET);
                            }
                        }
                        if (((value >> 4) & 0x0F) > 9 || ram.readBit(Constants.BIT_ADDRESS_CARRY))
                        {
                            value += 0x60;
                            if (value > 255)
                            {
                                value -= 256;
                                ram.writeBit(Constants.BIT_ADDRESS_CARRY, BitWriteMode.SET);
                            }
                        }
                        return value;
                    });
            case MOV_ACC_IMM ->
                    ram.writeByte(Constants.ADDRESS_ACCUMULATOR, readRomAndIncrementPC());
            case MOV_MEM_IMM ->
                    ram.writeByte(readRomAndIncrementPC() & 0xFF, readRomAndIncrementPC());
            case MOV_IR0_IMM, MOV_IR1_IMM ->
                    OpcodeHelpers.writeRegisterIndirect(ram, romByte & 0x1, readRomAndIncrementPC());
            case MOV_DR0_IMM, MOV_DR1_IMM, MOV_DR2_IMM, MOV_DR3_IMM, MOV_DR4_IMM, MOV_DR5_IMM, MOV_DR6_IMM, MOV_DR7_IMM ->
                    OpcodeHelpers.writeRegisterDirect(ram, romByte & 0b00000111, readRomAndIncrementPC());
            case MOV_MEM_MEM ->
            {
                int srcAddr = readRomAndIncrementPC() & 0xFF;
                int destAddr = readRomAndIncrementPC() & 0xFF;
                ram.writeByte(destAddr, ram.readByte(srcAddr));
            }
            case MOV_MEM_IR0, MOV_MEM_IR1 ->
                    ram.writeByte(readRomAndIncrementPC() & 0xFF, OpcodeHelpers.readRegisterIndirect(ram, romByte & 0x1));
            case MOV_MEM_DR0, MOV_MEM_DR1, MOV_MEM_DR2, MOV_MEM_DR3, MOV_MEM_DR4, MOV_MEM_DR5, MOV_MEM_DR6, MOV_MEM_DR7 ->
                    ram.writeByte(readRomAndIncrementPC() & 0xFF, OpcodeHelpers.readRegisterDirect(ram, romByte & 0b00000111));
            case MOV_IR0_MEM, MOV_IR1_MEM ->
                    OpcodeHelpers.writeRegisterIndirect(ram, romByte & 0x1, ram.readByte(readRomAndIncrementPC() & 0xFF));
            case MOV_DR0_MEM, MOV_DR1_MEM, MOV_DR2_MEM, MOV_DR3_MEM, MOV_DR4_MEM, MOV_DR5_MEM, MOV_DR6_MEM, MOV_DR7_MEM ->
                    OpcodeHelpers.writeRegisterDirect(ram, romByte & 0b00000111, ram.readByte(readRomAndIncrementPC() & 0xFF));
            case MOV_ACC_MEM ->
                    ram.writeByte(Constants.ADDRESS_ACCUMULATOR, ram.readByte(readRomAndIncrementPC() & 0xFF));
            case MOV_ACC_IR0, MOV_ACC_IR1 ->
                    ram.writeByte(Constants.ADDRESS_ACCUMULATOR, OpcodeHelpers.readRegisterIndirect(ram, romByte & 0x1));
            case MOV_ACC_DR0, MOV_ACC_DR1, MOV_ACC_DR2, MOV_ACC_DR3, MOV_ACC_DR4, MOV_ACC_DR5, MOV_ACC_DR6, MOV_ACC_DR7 ->
                    ram.writeByte(Constants.ADDRESS_ACCUMULATOR, OpcodeHelpers.readRegisterDirect(ram, romByte & 0b00000111));
            case MOV_MEM_ACC ->
                    ram.writeByte(readRomAndIncrementPC() & 0xFF, ram.readByte(Constants.ADDRESS_ACCUMULATOR));
            case MOV_IR0_ACC, MOV_IR1_ACC ->
                    OpcodeHelpers.writeRegisterIndirect(ram, romByte & 0x1, ram.readByte(Constants.ADDRESS_ACCUMULATOR));
            case MOV_DR0_ACC, MOV_DR1_ACC, MOV_DR2_ACC, MOV_DR3_ACC, MOV_DR4_ACC, MOV_DR5_ACC, MOV_DR6_ACC, MOV_DR7_ACC ->
                    OpcodeHelpers.writeRegisterDirect(ram, romByte & 0b00000111, ram.readByte(Constants.ADDRESS_ACCUMULATOR));
            case MOV_DPTR ->
            {
                ram.writeByte(Constants.ADDRESS_DATA_POINTER_UPPER, readRomAndIncrementPC());
                ram.writeByte(Constants.ADDRESS_DATA_POINTER_LOWER, readRomAndIncrementPC());
            }
            case MOV_BIT_C ->
            {
                boolean carry = ram.readBit(Constants.BIT_ADDRESS_CARRY);
                ram.writeBit(readRomAndIncrementPC(), BitWriteMode.of(carry));
            }
            case MOV_C_BIT ->
            {
                boolean bit = ram.readBit(readRomAndIncrementPC());
                ram.writeBit(Constants.BIT_ADDRESS_CARRY, BitWriteMode.of(bit));
            }
            case MOVC_ACC_IAPC ->
            {
                int acc = ram.read(Constants.ADDRESS_ACCUMULATOR);
                int address = acc + programCounter;
                ram.writeByte(Constants.ADDRESS_ACCUMULATOR, rom[address]);
            }
            case MOVC_ACC_IADPTR ->
            {
                int acc = ram.read(Constants.ADDRESS_ACCUMULATOR);
                int address = acc + OpcodeHelpers.readDataPointer(ram);
                ram.writeByte(Constants.ADDRESS_ACCUMULATOR, rom[address]);
            }
            case MOVX_ACC_IDPTR ->
                    throw new IllegalStateException("External memory unsupported");
            case MOVX_ACC_IR0, MOVX_ACC_IR1 -> //noinspection DuplicateBranchesInSwitch
                    throw new IllegalStateException("External memory unsupported");
            case MOVX_IDPTR_ACC -> //noinspection DuplicateBranchesInSwitch
                    throw new IllegalStateException("External memory unsupported");
            case MOVX_IR0_ACC, MOVX_IR1_ACC -> //noinspection DuplicateBranchesInSwitch
                    throw new IllegalStateException("External memory unsupported");
            default ->
                    throw new IllegalStateException("Unrecognized opcode: " + opcode);
        }
    }

    private byte readRomAndIncrementPC()
    {
        byte data = rom[programCounter];
        programCounter++;
        if (programCounter >= Constants.ROM_SIZE)
        {
            programCounter = 0;
        }
        return data;
    }

    private void pushStateBeforeCall()
    {
        pushStack((byte) (programCounter & 0xFF));
        pushStack((byte) (programCounter >> 8 & 0xFF));
    }

    private void pushStack(byte data)
    {
        int pointer = ram.read(Constants.ADDRESS_STACK_POINTER);
        pointer++;
        ram.write(Constants.ADDRESS_STACK_POINTER, pointer);
        ram.writeByte(pointer, data);
    }

    private byte popStack()
    {
        int pointer = ram.read(Constants.ADDRESS_STACK_POINTER);
        byte data = ram.readByte(pointer);
        ram.write(Constants.ADDRESS_STACK_POINTER, pointer - 1);
        return data;
    }

    public int getProgramCounter()
    {
        return programCounter;
    }

    public void reset(boolean clearRom)
    {
        programCounter = Constants.INITIAL_PROGRAM_COUNTER;
        ram.reset();
        if (clearRom)
        {
            Arrays.fill(rom, (byte) 0);
        }
    }

    public void loadCode(Code code)
    {
        reset(true);
        this.code = code;
        System.arraycopy(code.rom(), 0, this.rom, 0, Math.min(code.rom().length, Constants.ROM_SIZE));
    }

    public Code getCode()
    {
        return code;
    }

    public byte[] getRam()
    {
        return ram.getBackingArray();
    }

    public IOPorts getIoPorts()
    {
        return ioPorts;
    }

    public Timers getTimers()
    {
        return timers;
    }

    public void load(CompoundTag tag)
    {
        code = Code.CODEC.decode(NbtOps.INSTANCE, tag.getCompound("code")).result().map(Pair::getFirst).orElse(Code.EMPTY);
        Utils.copyByteArray(code.rom(), rom);
        Utils.copyByteArray(tag.getByteArray("ram"), ram.getBackingArray());
        ioPorts.load(tag.getCompound("io"));
        programCounter = tag.getInt("program_counter");
    }

    public CompoundTag save()
    {
        CompoundTag tag = new CompoundTag();
        tag.put("code", Code.CODEC.encodeStart(NbtOps.INSTANCE, code).result().orElseGet(CompoundTag::new));
        tag.putByteArray("rom", Arrays.copyOf(rom, rom.length));
        tag.putByteArray("ram", Arrays.copyOf(ram.getBackingArray(), ram.getBackingArray().length));
        tag.put("io", ioPorts.save());
        tag.putInt("program_counter", programCounter);
        return tag;
    }
}
