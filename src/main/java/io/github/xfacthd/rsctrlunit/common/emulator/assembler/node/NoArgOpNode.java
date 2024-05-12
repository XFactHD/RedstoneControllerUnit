package io.github.xfacthd.rsctrlunit.common.emulator.assembler.node;

import io.github.xfacthd.rsctrlunit.common.emulator.opcode.Opcode;

public record NoArgOpNode(int line, Opcode opcode) implements OpNode
{
    @Override
    public int appendOperands(byte[] code, int pointer)
    {
        return pointer;
    }

    public static NoArgOpNode create(int line, Opcode opcode, @SuppressWarnings("unused") String[] operands)
    {
        return new NoArgOpNode(line, opcode);
    }
}
