package io.github.xfacthd.rsctrlunit.common.emulator.util;

import io.github.xfacthd.rsctrlunit.common.emulator.assembler.node.Node;
import io.github.xfacthd.rsctrlunit.common.emulator.opcode.Opcode;
import org.jetbrains.annotations.Nullable;

public interface NodeParser
{
    @Nullable
    Node parse(int line, Opcode op, String[] operands);
}
