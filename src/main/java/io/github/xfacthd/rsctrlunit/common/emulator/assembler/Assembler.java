package io.github.xfacthd.rsctrlunit.common.emulator.assembler;

import com.google.common.collect.Sets;
import io.github.xfacthd.rsctrlunit.common.emulator.util.Code;
import io.github.xfacthd.rsctrlunit.common.emulator.assembler.node.*;
import io.github.xfacthd.rsctrlunit.common.emulator.assembler.node.directive.*;
import io.github.xfacthd.rsctrlunit.common.emulator.opcode.*;
import io.github.xfacthd.rsctrlunit.common.emulator.util.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;

public final class Assembler
{
    private static final String[] EMPTY_ARRAY = new String[0];
    private static final Opcode[] AJMP_OPCODES = new Opcode[] {
            Opcode.AJMP_000, Opcode.AJMP_001, Opcode.AJMP_010, Opcode.AJMP_011, Opcode.AJMP_100, Opcode.AJMP_101, Opcode.AJMP_110, Opcode.AJMP_111
    };
    private static final Opcode[] ACALL_OPCODES = new Opcode[] {
            Opcode.ACALL_000, Opcode.ACALL_001, Opcode.ACALL_010, Opcode.ACALL_011, Opcode.ACALL_100, Opcode.ACALL_101, Opcode.ACALL_110, Opcode.ACALL_111
    };

    private Assembler() { }

    public static Code assemble(String name, String source, ErrorPrinter errorPrinter)
    {
        List<Node> nodes = parseSource(source, errorPrinter);
        if (nodes.isEmpty()) return Code.EMPTY;
        if (!validateLabels(nodes, errorPrinter)) return Code.EMPTY;

        int codeSize = computeCodeSize(nodes, errorPrinter);
        if (codeSize <= 0) return Code.EMPTY;
        byte[] code = new byte[codeSize];
        return buildRomImage(name, nodes, code, errorPrinter);
    }

    private static List<Node> parseSource(String source, ErrorPrinter errorPrinter)
    {
        List<Node> nodes = new ArrayList<>();
        try (LineNumberReader reader = new LineNumberReader(new StringReader(source)))
        {
            while (reader.ready())
            {
                int lineNum = reader.getLineNumber();
                String line = reader.readLine();
                int commentStart = line.indexOf(';');
                if (commentStart >= 0)
                {
                    line = line.substring(0, commentStart);
                }
                line = line.trim();
                if (line.isEmpty())
                {
                    continue;
                }

                if (line.endsWith(":"))
                {
                    nodes.add(new LabelNode(lineNum, line.substring(0, line.length() - 1)));
                    continue;
                }

                String[] parts = line.split(" ");
                Node directiveNode = parseDirective(lineNum, parts[0], Arrays.copyOfRange(parts, 1, parts.length));
                if (directiveNode instanceof ErrorNode error)
                {
                    errorPrinter.error(error.error());
                    return List.of();
                }
                else if (directiveNode instanceof EndDirectiveNode)
                {
                    break;
                }
                else if (directiveNode != null)
                {
                    nodes.add(directiveNode);
                }

                String[] operands = extractOperands(parts);
                Node node = Opcode.parse(lineNum, parts[0].toLowerCase(Locale.ROOT), operands);
                if (node instanceof ErrorNode error)
                {
                    errorPrinter.error(error.error());
                    return List.of();
                }
                nodes.add(node);
            }
        }
        catch (IOException e)
        {
            return List.of();
        }
        return nodes;
    }

    @Nullable
    private static Node parseDirective(int line, String directive, String[] params)
    {
        return switch (directive.toLowerCase(Locale.ROOT))
        {
            case "org" ->
            {
                if (params.length == 0)
                {
                    yield new ErrorNode(line, "Expected address parameter for ORG directive");
                }
                if (params.length == 1)
                {
                    String lowerParam = params[0].toLowerCase(Locale.ROOT);
                    if (ParseHelpers.isNumber(lowerParam))
                    {
                        int address = ParseHelpers.parseInt(lowerParam);
                        if (address >= 0 && address < Constants.ROM_SIZE)
                        {
                            yield new OriginDirectiveNode(line, address);
                        }
                    }
                }
                yield new ErrorNode(line, "Unexpected or invalid parameters for ORG directive: " + Arrays.toString(params));
            }
            case "equ" -> new ErrorNode(line, "EQU directive unsupported");
            case "set" -> new ErrorNode(line, "SET directive unsupported");
            case "bit" -> new ErrorNode(line, "BIT directive unsupported");
            case "code" -> new ErrorNode(line, "CODE directive unsupported");
            case "data" -> new ErrorNode(line, "DATA directive unsupported");
            case "idata" -> new ErrorNode(line, "IDATA directive unsupported");
            case "xdata" -> new ErrorNode(line, "XDATA directive unsupported");
            case "using" ->
            {
                if (params.length == 1)
                {
                    String bank = params[0];
                    switch (bank)
                    {
                        case "0": yield new UsingDirectiveNode(line, 0);
                        case "1": yield new UsingDirectiveNode(line, 1);
                        case "2": yield new UsingDirectiveNode(line, 2);
                        case "3": yield new UsingDirectiveNode(line, 3);
                    }
                }
                yield new ErrorNode(line, "Unexpected or invalid parameters for USING directive: " + Arrays.toString(params));
            }
            case "end" ->
            {
                if (params.length == 0)
                {
                    yield new EndDirectiveNode(line);
                }
                yield new ErrorNode(line, "Unexpected parameters for END directive: " + Arrays.toString(params));
            }
            case "db" -> new ErrorNode(line, "DB directive unsupported");
            default -> null;
        };
    }

    private static String[] extractOperands(String[] parts)
    {
        if (parts.length == 1) return EMPTY_ARRAY;

        String operands = parts[1];
        if (parts.length > 2)
        {
            StringBuilder builder = new StringBuilder(operands);
            for (int i = 2; i < parts.length; i++)
            {
                builder.append(parts[i]);
            }
            operands = builder.toString();
        }
        return operands.split(",");
    }

    private static boolean validateLabels(List<Node> nodes, ErrorPrinter errorPrinter)
    {
        boolean valid = true;

        Map<String, LabelNode> existingLabels = new HashMap<>();
        for (Node node : nodes)
        {
            if (!(node instanceof LabelNode labelNode)) continue;

            String label = labelNode.label();
            if (existingLabels.containsKey(label))
            {
                errorPrinter.error("Duplicate label '%s' defined on line %d", label, labelNode.label());
                valid = false;
                continue;
            }
            existingLabels.put(label, labelNode);
        }

        Set<String> usedLabels = new HashSet<>();
        for (Node node : nodes)
        {
            if (!(node instanceof JumpNode jumpNode)) continue;

            String target = jumpNode.label();
            if (!existingLabels.containsKey(target))
            {
                errorPrinter.error("Undefined label '%s' on line %d", target, node.line());
                valid = false;
            }
            usedLabels.add(target);
        }

        for (String unusedLabel : Sets.difference(existingLabels.keySet(), usedLabels))
        {
            int line = existingLabels.get(unusedLabel).line();
            errorPrinter.warning("Unused label '%s' on line %d", unusedLabel, line);
        }

        return valid;
    }

    private static int computeCodeSize(List<Node> nodes, ErrorPrinter errorPrinter)
    {
        int size = 0;
        for (Node node : nodes)
        {
            if (node instanceof OpNode opNode)
            {
                size += 1 + opNode.opcode().getOperandBytes();
            }
            else if (node instanceof OriginDirectiveNode org && size < org.origin())
            {
                size = org.origin();
            }
        }
        if (size > Constants.ROM_SIZE)
        {
            errorPrinter.error("Assembled code size %d exceeds maximum ROM size %d", size, Constants.ROM_SIZE);
            return -1;
        }
        return size;
    }

    private static Code buildRomImage(String name, List<Node> nodes, byte[] codeBytes, ErrorPrinter errorPrinter)
    {
        record UnresolvedJump(int opPointer, JumpNode node) { }

        int pointer = 0;
        Object2IntMap<String> resolvedLabels = new Object2IntOpenHashMap<>();
        Map<String, List<UnresolvedJump>> unresolvedJumps = new HashMap<>();
        for (Node node : nodes)
        {
            if (node instanceof LabelNode labelNode)
            {
                resolvedLabels.put(labelNode.label(), pointer);
                continue;
            }
            if (node instanceof OriginDirectiveNode orgNode)
            {
                pointer = orgNode.origin();
                continue;
            }

            if (!(node instanceof OpNode opNode)) continue;

            int opPointer = pointer;
            codeBytes[pointer] = opNode.opcode().toByte();
            pointer++;
            pointer = opNode.appendOperands(codeBytes, pointer);

            if (node instanceof JumpNode jumpNode)
            {
                unresolvedJumps.computeIfAbsent(jumpNode.label(), $ -> new ArrayList<>())
                        .add(new UnresolvedJump(opPointer, jumpNode));
            }
        }

        Int2ObjectMap<String> labelsByPosition = new Int2ObjectOpenHashMap<>();

        for (Map.Entry<String, List<UnresolvedJump>> entry : unresolvedJumps.entrySet())
        {
            String label = entry.getKey();
            int labelPointer = resolvedLabels.getInt(label);

            labelsByPosition.put(labelPointer, label);

            for (UnresolvedJump jump : entry.getValue())
            {
                switch (jump.node.opcode())
                {
                    case AJMP_000, ACALL_000 ->
                    {
                        int bits = 32 - Integer.numberOfLeadingZeros(labelPointer);
                        if (bits > 11 || OpcodeHelpers.calculateAjmpTarget(jump.opPointer + 2, labelPointer) != labelPointer)
                        {
                            errorPrinter.error("%s target %d on line %d exceeds max range", jump.node.opcode().getMnemonic(), labelPointer, jump.node.line());
                            return Code.EMPTY;
                        }

                        int topBits = (labelPointer >> 8) & 0x00000111;
                        Opcode op = jump.node.opcode() == Opcode.AJMP_000 ? AJMP_OPCODES[topBits] : ACALL_OPCODES[topBits];
                        codeBytes[jump.opPointer] = op.toByte();
                        codeBytes[jump.opPointer + 1] = (byte) (labelPointer & 0xFF);
                    }
                    case LJMP, LCALL ->
                    {
                        codeBytes[jump.opPointer + 1] = (byte) ((labelPointer >> 8) & 0xFF);
                        codeBytes[jump.opPointer + 2] = (byte) (labelPointer & 0xFF);
                    }
                    case JC, JNC, JZ, JNZ, SJMP, DJNZ_DR0, DJNZ_DR1, DJNZ_DR2, DJNZ_DR3, DJNZ_DR4, DJNZ_DR5, DJNZ_DR6, DJNZ_DR7 ->
                    {
                        int offset = calculateShortJumpOffset(jump.opPointer + 2, labelPointer);
                        if (offset == Integer.MAX_VALUE)
                        {
                            errorPrinter.error("%s target %d on line %d exceeds max range", jump.node.opcode().getMnemonic(), labelPointer, jump.node.line());
                            return Code.EMPTY;
                        }
                        codeBytes[jump.opPointer + 1] = (byte) offset;
                    }
                    case JBC, JB, JNB, JMP, CJNE_ACC_IMM,
                         CJNE_ACC_MEM,
                         CJNE_IR0_IMM, CJNE_IR1_IMM,
                         CJNE_DR0_IMM, CJNE_DR1_IMM, CJNE_DR2_IMM, CJNE_DR3_IMM, CJNE_DR4_IMM, CJNE_DR5_IMM, CJNE_DR6_IMM, CJNE_DR7_IMM,
                         DJNZ_MEM ->
                    {
                        int offset = calculateShortJumpOffset(jump.opPointer + 3, labelPointer);
                        if (offset == Integer.MAX_VALUE)
                        {
                            errorPrinter.error("%s target %d on line %d exceeds max range", jump.node.opcode().getMnemonic(), labelPointer, jump.node.line());
                            return Code.EMPTY;
                        }
                        codeBytes[jump.opPointer + 2] = (byte) offset;
                    }
                    default -> throw new IllegalArgumentException("Unrecognized jump opcode: " + jump.node.opcode());
                }
            }
        }

        return new Code(Component.literal(name), codeBytes, labelsByPosition);
    }

    private static int calculateShortJumpOffset(int src, int dest)
    {
        int diff = dest - src;
        return (diff > 127 || diff < -128) ? Integer.MAX_VALUE : ((byte) diff);
    }
}
