package io.github.xfacthd.rsctrlunit.common.emulator.assembler.node;

import io.github.xfacthd.rsctrlunit.common.emulator.opcode.Opcode;

public record JumpNode(int line, Opcode opcode, String label, byte... operands) implements OpNode
{
    @Override
    public int appendOperands(byte[] code, int pointer)
    {
        for (byte operand : operands)
        {
            code[pointer] = operand;
            pointer++;
        }
        if (opcode == Opcode.LJMP || opcode == Opcode.LCALL)
        {
            return pointer + 2;
        }
        return pointer + 1;
    }
}
