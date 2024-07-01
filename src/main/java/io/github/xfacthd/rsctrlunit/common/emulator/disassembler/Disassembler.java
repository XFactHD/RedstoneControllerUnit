package io.github.xfacthd.rsctrlunit.common.emulator.disassembler;

import io.github.xfacthd.rsctrlunit.common.emulator.opcode.Opcode;
import io.github.xfacthd.rsctrlunit.common.emulator.opcode.OpcodeHelpers;
import io.github.xfacthd.rsctrlunit.common.emulator.util.Code;
import io.github.xfacthd.rsctrlunit.common.emulator.util.Constants;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

public final class Disassembler
{
    @VisibleForTesting
    public static final String LABEL_LINE_TEMPLATE = "        %s:";
    @VisibleForTesting
    public static final String CODE_LINE_TEMPLATE = "%04X | %s";

    private Disassembler() { }

    public static Disassembly disassemble(@Nullable Code code)
    {
        if (code == null || code.equals(Code.EMPTY)) return Disassembly.EMPTY;

        Disassembly disassembly = new Disassembly();

        byte[] rom = code.rom();
        Int2ObjectMap<String> labels = generateMissingLabels(rom, code.labels());
        for (MutableInt counter = new MutableInt(); counter.intValue() < rom.length;)
        {
            int opIndex = counter.getAndIncrement();
            String label = labels.get(opIndex);
            if (label != null)
            {
                disassembly.addLabelLine(LABEL_LINE_TEMPLATE.formatted(label));
            }

            byte romByte = rom[opIndex];
            Opcode opcode = Opcode.fromRomByte(romByte);
            StringBuilder line = new StringBuilder(CODE_LINE_TEMPLATE.formatted(opIndex, opcode.getMnemonic())).append(" ");
            switch (opcode)
            {
                case NOP, RET, RETI -> {}
                case SJMP, JC, JNC, JZ, JNZ -> line.append(printJumpLabel(labels, counter, rom[counter.getAndIncrement()], false));
                case JMP -> line.append("@A+DPTR");
                case AJMP_000, AJMP_001, AJMP_010, AJMP_011, AJMP_100, AJMP_101, AJMP_110, AJMP_111,
                     ACALL_000, ACALL_001, ACALL_010, ACALL_011, ACALL_100, ACALL_101, ACALL_110, ACALL_111 ->
                {
                    int address = OpcodeHelpers.calculateAjmpAddress(romByte, rom[counter.getAndIncrement()]);
                    int target = OpcodeHelpers.calculateAjmpTarget(counter.intValue(), address);
                    line.append(printJumpLabel(labels, counter, target, true));
                }
                case LJMP, LCALL ->
                {
                    int upper = rom[counter.getAndIncrement()] & 0xFF;
                    int lower = rom[counter.getAndIncrement()] & 0xFF;
                    line.append(printJumpLabel(labels, counter, (upper << 8) | lower, true));
                }
                case JBC, JB, JNB -> line.append(printBitAddress(rom[counter.getAndIncrement()] & 0xFF))
                        .append(",")
                        .append(printJumpLabel(labels, counter, rom[counter.getAndIncrement()], false));
                case CJNE_ACC_IMM -> line.append("A,")
                        .append(printImmediate(rom[counter.getAndIncrement()]))
                        .append(",")
                        .append(printJumpLabel(labels, counter, rom[counter.getAndIncrement()], false));
                case CJNE_ACC_MEM -> line.append("A,")
                        .append(printAddress(rom[counter.getAndIncrement()]))
                        .append(",")
                        .append(printJumpLabel(labels, counter, rom[counter.getAndIncrement()], false));
                case CJNE_IR0_IMM, CJNE_IR1_IMM -> line.append(printRegisterIndirect(opcode))
                        .append(",")
                        .append(printImmediate(rom[counter.getAndIncrement()]))
                        .append(",")
                        .append(printJumpLabel(labels, counter, rom[counter.getAndIncrement()], false));
                case CJNE_DR0_IMM, CJNE_DR1_IMM, CJNE_DR2_IMM, CJNE_DR3_IMM, CJNE_DR4_IMM, CJNE_DR5_IMM, CJNE_DR6_IMM, CJNE_DR7_IMM ->
                        line.append(printRegisterDirect(opcode))
                                .append(",")
                                .append(printImmediate(rom[counter.getAndIncrement()]))
                                .append(",")
                                .append(printJumpLabel(labels, counter, rom[counter.getAndIncrement()], false));
                case DJNZ_MEM -> line.append(printAddress(rom[counter.getAndIncrement()]))
                        .append(",")
                        .append(printJumpLabel(labels, counter, rom[counter.getAndIncrement()], false));
                case DJNZ_DR0, DJNZ_DR1, DJNZ_DR2, DJNZ_DR3, DJNZ_DR4, DJNZ_DR5, DJNZ_DR6, DJNZ_DR7 ->
                        line.append(printRegisterDirect(opcode))
                                .append(",")
                                .append(printJumpLabel(labels, counter, rom[counter.getAndIncrement()], false));
                case PUSH, POP, INC_MEM, DEC_MEM -> line.append(printAddress(rom[counter.getAndIncrement()]));
                case RR, RRC, RL, RLC, INC_ACC, DEC_ACC -> line.append("A");
                case INC_IRO, INC_IR1, DEC_IRO, DEC_IR1 -> line.append(printRegisterIndirect(opcode));
                case INC_DR0, INC_DR1, INC_DR2, INC_DR3, INC_DR4, INC_DR5, INC_DR6, INC_DR7,
                     DEC_DR0, DEC_DR1, DEC_DR2, DEC_DR3, DEC_DR4, DEC_DR5, DEC_DR6, DEC_DR7 ->
                        line.append(printRegisterDirect(opcode));
                case INC_DPTR -> line.append("DPTR");
                case ADD_IMM, ADDC_IMM, ORL_IMM, ANL_IMM, XRL_IMM, SUBB_IMM, MOV_ACC_IMM ->
                        line.append("A,").append(printImmediate(rom[counter.getAndIncrement()]));
                case ADD_MEM, ADDC_MEM, ORL_MEM, ANL_MEM, XRL_MEM, SUBB_MEM, MOV_ACC_MEM ->
                        line.append("A,").append(printAddress(rom[counter.getAndIncrement()]));
                case ADD_IR0, ADD_IR1, ADDC_IR0, ADDC_IR1, ORL_IR0, ORL_IR1, ANL_IR0, ANL_IR1, XRL_IR0, XRL_IR1, SUBB_IR0, SUBB_IR1, MOV_ACC_IR0, MOV_ACC_IR1 ->
                        line.append("A,").append(printRegisterIndirect(opcode));
                case ADD_DR0, ADD_DR1, ADD_DR2, ADD_DR3, ADD_DR4, ADD_DR5, ADD_DR6, ADD_DR7,
                     ADDC_DR0, ADDC_DR1, ADDC_DR2, ADDC_DR3, ADDC_DR4, ADDC_DR5, ADDC_DR6, ADDC_DR7,
                     ORL_DR0, ORL_DR1, ORL_DR2, ORL_DR3, ORL_DR4, ORL_DR5, ORL_DR6, ORL_DR7,
                     ANL_DR0, ANL_DR1, ANL_DR2, ANL_DR3, ANL_DR4, ANL_DR5, ANL_DR6, ANL_DR7,
                     XRL_DR0, XRL_DR1, XRL_DR2, XRL_DR3, XRL_DR4, XRL_DR5, XRL_DR6, XRL_DR7,
                     SUBB_DR0, SUBB_DR1, SUBB_DR2, SUBB_DR3, SUBB_DR4, SUBB_DR5, SUBB_DR6, SUBB_DR7,
                     MOV_ACC_DR0, MOV_ACC_DR1, MOV_ACC_DR2, MOV_ACC_DR3, MOV_ACC_DR4, MOV_ACC_DR5, MOV_ACC_DR6, MOV_ACC_DR7 ->
                        line.append("A,").append(printRegisterDirect(opcode));
                case ORL_C_BIT, ANL_C_BIT -> line.append("C,").append(printBitAddress(rom[counter.getAndIncrement()]));
                case ORL_C_NBIT, ANL_C_NBIT -> line.append("C,/").append(printBitAddress(rom[counter.getAndIncrement()]));
                case ORL_MEM_IMM, ANL_MEM_IMM, XRL_MEM_IMM -> line.append(printAddress(rom[counter.getAndIncrement()]))
                        .append(",")
                        .append(printImmediate(rom[counter.getAndIncrement()]));
                case ORL_MEM_ACC, ANL_MEM_ACC, XRL_MEM_ACC -> line.append(printAddress(rom[counter.getAndIncrement()])).append(",A");
                case MUL_AB, DIV_AB -> line.append("AB");
                case SETB_BIT, CPL_BIT, CLR_BIT -> line.append(printBitAddress(rom[counter.getAndIncrement()]));
                case SETB_C, CPL_C, CLR_C -> line.append("C");
                case CPL_ACC, CLR_ACC, SWAP, DA -> line.append("A");
                case XCH_MEM -> line.append("A,").append(printAddress(rom[counter.getAndIncrement()]));
                case XCH_IR0, XCH_IR1, XCHD_IR0, XCHD_IR1 -> line.append("A,").append(printRegisterIndirect(opcode));
                case XCH_DR0, XCH_DR1, XCH_DR2, XCH_DR3, XCH_DR4, XCH_DR5, XCH_DR6, XCH_DR7 ->
                        line.append("A,").append(printRegisterDirect(opcode));
                case MOV_MEM_IMM -> line.append(printAddress(rom[counter.getAndIncrement()]))
                        .append(",")
                        .append(printImmediate(rom[counter.getAndIncrement()]));
                case MOV_IR0_IMM, MOV_IR1_IMM -> line.append(printRegisterIndirect(opcode))
                        .append(",")
                        .append(printImmediate(rom[counter.getAndIncrement()]));
                case MOV_DR0_IMM, MOV_DR1_IMM, MOV_DR2_IMM, MOV_DR3_IMM, MOV_DR4_IMM, MOV_DR5_IMM, MOV_DR6_IMM, MOV_DR7_IMM ->
                        line.append(printRegisterDirect(opcode))
                                .append(",")
                                .append(printImmediate(rom[counter.getAndIncrement()]));
                case MOV_MEM_MEM -> line.append(printAddress(rom[counter.getAndIncrement()]))
                        .append(",")
                        .append(printAddress(rom[counter.getAndIncrement()]));
                case MOV_MEM_IR0, MOV_MEM_IR1 -> line.append(printAddress(rom[counter.getAndIncrement()]))
                        .append(",")
                        .append(printRegisterIndirect(opcode));
                case MOV_MEM_DR0, MOV_MEM_DR1, MOV_MEM_DR2, MOV_MEM_DR3, MOV_MEM_DR4, MOV_MEM_DR5, MOV_MEM_DR6, MOV_MEM_DR7 ->
                        line.append(printAddress(rom[counter.getAndIncrement()]))
                                .append(",")
                                .append(printRegisterDirect(opcode));
                case MOV_MEM_ACC -> line.append(printAddress(rom[counter.getAndIncrement()])).append(",A");
                case MOV_IR0_ACC, MOV_IR1_ACC -> line.append(printRegisterIndirect(opcode)).append(",A");
                case MOV_DR0_ACC, MOV_DR1_ACC, MOV_DR2_ACC, MOV_DR3_ACC, MOV_DR4_ACC, MOV_DR5_ACC, MOV_DR6_ACC, MOV_DR7_ACC ->
                        line.append(printRegisterDirect(opcode)).append(",A");
                case MOV_DPTR ->
                {
                    int upper = rom[counter.getAndIncrement()] & 0xFF;
                    int lower = rom[counter.getAndIncrement()] & 0xFF;
                    line.append("DPTR,#").append(prefixHex("%04Xh".formatted((upper << 8) | lower)));
                }
                case MOV_BIT_C -> line.append(printBitAddress(rom[counter.getAndIncrement()])).append(",C");
                case MOV_C_BIT -> line.append("C,").append(printBitAddress(rom[counter.getAndIncrement()]));
                case MOVC_ACC_IAPC -> line.append("A,@A+PC");
                case MOVC_ACC_IADPTR -> line.append("A,@A+DPTR");
                case MOVX_ACC_IDPTR -> line.append("A,@DPTR");
                case MOVX_ACC_IR0, MOVX_ACC_IR1 -> line.append("A,").append(printRegisterIndirect(opcode));
                case MOVX_IDPTR_ACC -> line.append("@DPTR,A");
                case MOVX_IR0_ACC, MOVX_IR1_ACC -> line.append(printRegisterIndirect(opcode)).append(",A");
                default -> throw new IllegalStateException("Unrecognized opcode: " + opcode + " at offset: " + opIndex);
            }
            disassembly.addCodeLine(opIndex, line.toString().stripTrailing());
        }

        return disassembly;
    }

    private static Int2ObjectMap<String> generateMissingLabels(byte[] rom, Int2ObjectMap<String> labels)
    {
        int labelIdx = 0;
        boolean copied = false;
        for (MutableInt counter = new MutableInt(); counter.intValue() < rom.length;)
        {
            int opIndex = counter.getAndIncrement();
            byte romByte = rom[opIndex];
            Opcode opcode = Opcode.fromRomByte(romByte);
            int address = switch (opcode)
            {
                case SJMP, JC, JNC, JZ, JNZ,
                     DJNZ_DR0, DJNZ_DR1, DJNZ_DR2, DJNZ_DR3, DJNZ_DR4, DJNZ_DR5, DJNZ_DR6, DJNZ_DR7 ->
                        computeJumpTarget(counter, rom[counter.getAndIncrement()], false);
                case AJMP_000, AJMP_001, AJMP_010, AJMP_011, AJMP_100, AJMP_101, AJMP_110, AJMP_111,
                     ACALL_000, ACALL_001, ACALL_010, ACALL_011, ACALL_100, ACALL_101, ACALL_110, ACALL_111 ->
                {
                    int addr = OpcodeHelpers.calculateAjmpAddress(romByte, rom[counter.getAndIncrement()]);
                    int target = OpcodeHelpers.calculateAjmpTarget(counter.intValue(), addr);
                    yield computeJumpTarget(counter, target, true);
                }
                case LJMP, LCALL ->
                {
                    int upper = rom[counter.getAndIncrement()] & 0xFF;
                    int lower = rom[counter.getAndIncrement()] & 0xFF;
                    yield computeJumpTarget(counter, (upper << 8) | lower, true);
                }
                case JBC, JB, JNB,
                     CJNE_ACC_IMM,
                     CJNE_ACC_MEM,
                     CJNE_IR0_IMM, CJNE_IR1_IMM,
                     CJNE_DR0_IMM, CJNE_DR1_IMM, CJNE_DR2_IMM, CJNE_DR3_IMM, CJNE_DR4_IMM, CJNE_DR5_IMM, CJNE_DR6_IMM, CJNE_DR7_IMM,
                     DJNZ_MEM ->
                {
                    counter.increment(); // Jump over bit address / immediate value / memory address
                    yield computeJumpTarget(counter, rom[counter.getAndIncrement()], false);
                }
                default ->
                {
                    counter.add(opcode.getOperandBytes());
                    yield -1;
                }
            };
            if (address != -1)
            {
                String label = labels.get(address);
                if (label == null)
                {
                    if (!copied)
                    {
                        labels = new Int2ObjectOpenHashMap<>(labels);
                        copied = true;
                    }
                    labels.put(address, "__syn_label_" + labelIdx);
                    labelIdx++;
                }
            }
        }
        return labels;
    }

    private static String printJumpLabel(Int2ObjectMap<String> labels, MutableInt currCounter, int addressOrOffset, boolean absolute)
    {
        addressOrOffset = computeJumpTarget(currCounter, addressOrOffset, absolute);
        String label = labels.get(addressOrOffset);
        if (label != null)
        {
            return label;
        }
        return prefixHex("%04Xh".formatted(addressOrOffset));
    }

    private static int computeJumpTarget(MutableInt currCounter, int addressOrOffset, boolean absolute)
    {
        if (!absolute)
        {
            addressOrOffset = currCounter.intValue() + addressOrOffset;
        }
        return addressOrOffset;
    }

    private static String printBitAddress(int bitAddress)
    {
        int byteAddress = OpcodeHelpers.calculateByteAddressFromBitAddress(bitAddress);
        String regPrefix;
        if (byteAddress >= 0x80)
        {
            regPrefix = switch (byteAddress)
            {
                case Constants.ADDRESS_IO_PORT0 -> "P0";
                case Constants.ADDRESS_TCON -> "TCON";
                case Constants.ADDRESS_IO_PORT1 -> "P1";
                case Constants.ADDRESS_SCON -> "SCON";
                case Constants.ADDRESS_IO_PORT2 -> "P2";
                case Constants.ADDRESS_IE -> "IE";
                case Constants.ADDRESS_IO_PORT3 -> "P3";
                case Constants.ADDRESS_IP -> "IP";
                case Constants.ADDRESS_STATUS_WORD -> "PSW";
                case Constants.ADDRESS_ACCUMULATOR -> "A";
                case Constants.ADDRESS_REGISTER_B -> "B";
                default -> printAddress(byteAddress);
            };
        }
        else
        {
            regPrefix = printAddress(byteAddress);
        }
        return regPrefix + "." + OpcodeHelpers.calculateBitIndexFromBitAddress(bitAddress);
    }

    private static String printAddress(int byteValue)
    {
        return prefixHex("%02Xh".formatted(byteValue & 0xFF));
    }

    private static String printImmediate(byte immediate)
    {
        return "#" + printAddress(immediate);
    }

    private static String prefixHex(String value)
    {
        if (Character.isAlphabetic(value.charAt(0)))
        {
            value = "0" + value;
        }
        return value;
    }

    private static String printRegisterIndirect(Opcode opcode)
    {
        return (opcode.ordinal() & 0x1) != 0 ? "@R1" : "@R0";
    }

    private static String printRegisterDirect(Opcode opcode)
    {
        int reg = opcode.ordinal() & 0b00000111;
        return "R" + reg;
    }
}
