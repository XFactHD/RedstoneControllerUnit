package io.github.xfacthd.rsctrlunit.common.emulator.assembler.node.directive;

import io.github.xfacthd.rsctrlunit.common.emulator.assembler.node.Node;

public record UsingDirectiveNode(int line, int bank) implements Node { }
