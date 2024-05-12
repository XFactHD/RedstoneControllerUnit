package io.github.xfacthd.rsctrlunit.common.emulator.assembler.node.directive;

import io.github.xfacthd.rsctrlunit.common.emulator.assembler.node.Node;

public record OriginDirectiveNode(int line, int origin) implements Node { }
