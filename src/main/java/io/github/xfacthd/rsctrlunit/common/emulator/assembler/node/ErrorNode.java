package io.github.xfacthd.rsctrlunit.common.emulator.assembler.node;

import io.github.xfacthd.rsctrlunit.common.emulator.opcode.Opcode;

import java.util.Arrays;
import java.util.Locale;

public record ErrorNode(int line, String error) implements Node
{
    public static ErrorNode operandCount(Opcode opcode, int operands, int line)
    {
        return new ErrorNode(line, String.format(
                Locale.ROOT,
                "Expected %d operands for '%s' on line %d, got %d",
                opcode.getOperands(), opcode.getMnemonic(), line, operands
        ));
    }

    public static ErrorNode invalidOperand(Opcode opcode, String[] operands, int line)
    {
        return new ErrorNode(line, String.format(
                Locale.ROOT,
                "Operands '%s' are invalid for '%s' on line %d",
                Arrays.toString(operands), opcode.getMnemonic(), line
        ));
    }

    public static ErrorNode unrecognizedOpcode(String mnemonic, int line)
    {
        return new ErrorNode(line, String.format(Locale.ROOT, "Unrecognized mnemonic: '%s'", mnemonic));
    }
}
