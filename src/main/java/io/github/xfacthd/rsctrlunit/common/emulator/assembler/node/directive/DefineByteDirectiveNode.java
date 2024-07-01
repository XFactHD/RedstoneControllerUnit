package io.github.xfacthd.rsctrlunit.common.emulator.assembler.node.directive;

import io.github.xfacthd.rsctrlunit.common.emulator.assembler.node.Node;

import java.util.Optional;

public record DefineByteDirectiveNode(int line, byte[] data, Optional<String> label) implements Node { }
