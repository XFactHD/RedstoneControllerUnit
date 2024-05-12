package io.github.xfacthd.rsctrlunit.common.emulator.assembler.node;

import io.github.xfacthd.rsctrlunit.common.emulator.opcode.Opcode;

public interface OpNode extends Node
{
    Opcode opcode();

    int appendOperands(byte[] code, int pointer);
}
