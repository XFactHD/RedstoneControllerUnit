package io.github.xfacthd.rsctrlunit.common.emulator.assembler.node;

import io.github.xfacthd.rsctrlunit.common.emulator.opcode.Opcode;

public record SimpleOpNode(int line, Opcode opcode, byte... operands) implements OpNode
{
    @Override
    public int appendOperands(byte[] code, int pointer)
    {
        for (byte operand : operands)
        {
            code[pointer] = operand;
            pointer++;
        }
        return pointer;
    }
}
