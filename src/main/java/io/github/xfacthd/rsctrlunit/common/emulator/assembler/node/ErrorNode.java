package io.github.xfacthd.rsctrlunit.common.emulator.assembler.node;

import io.github.xfacthd.rsctrlunit.common.emulator.opcode.Opcode;

import java.util.Arrays;

public record ErrorNode(int line, String error) implements Node
{
    public static ErrorNode operandCount(Opcode opcode, int operands, int line)
    {
        return new ErrorNode(line, "Expected %d operands for '%s' on line %d, got %d".formatted(
                opcode.getOperands(), opcode.getMnemonic(), line, operands
        ));
    }

    public static ErrorNode invalidOperand(Opcode opcode, String[] operands, int line)
    {
        return new ErrorNode(line, "Operands '%s' are invalid for '%s' on line %d".formatted(
                Arrays.toString(operands), opcode.getMnemonic(), line
        ));
    }

    public static ErrorNode unrecognizedOpcode(String mnemonic, int line)
    {
        return new ErrorNode(line, "Unrecognized mnemonic: '%s'".formatted(mnemonic));
    }
}
