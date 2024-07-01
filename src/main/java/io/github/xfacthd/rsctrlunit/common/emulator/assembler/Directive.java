package io.github.xfacthd.rsctrlunit.common.emulator.assembler;

import io.github.xfacthd.rsctrlunit.common.emulator.assembler.node.ErrorNode;
import io.github.xfacthd.rsctrlunit.common.emulator.assembler.node.Node;
import io.github.xfacthd.rsctrlunit.common.emulator.assembler.node.directive.*;
import io.github.xfacthd.rsctrlunit.common.emulator.opcode.ParseHelpers;
import io.github.xfacthd.rsctrlunit.common.emulator.util.Constants;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.*;

public enum Directive
{
    ORG((line, parts) ->
    {
        if (parts[0].equalsIgnoreCase("org"))
        {
            if (parts.length == 1)
            {
                return new ErrorNode(line, "Expected address parameter for ORG directive");
            }
            if (parts.length == 2)
            {
                String lowerParam = parts[1].toLowerCase(Locale.ROOT);
                if (ParseHelpers.isNumber(lowerParam))
                {
                    int address = ParseHelpers.parseInt(lowerParam);
                    if (address >= 0 && address < Constants.ROM_SIZE)
                    {
                        return new OriginDirectiveNode(line, address);
                    }
                }
            }
            return new ErrorNode(line, "Unexpected or invalid parameters for ORG directive: " + Arrays.toString(parts));
        }
        return null;
    }),
    EQU(DirectiveParser.unsupported(0, "EQU")),
    SET(DirectiveParser.unsupported(0, "SET")),
    BIT(DirectiveParser.unsupported(0, "BIT")),
    CODE(DirectiveParser.unsupported(0, "CODE")),
    DATA(DirectiveParser.unsupported(0, "DATA")),
    IDATA(DirectiveParser.unsupported(0, "IDATA")),
    XDATA(DirectiveParser.unsupported(0, "XDATA")),
    USING((line, parts) ->
    {
        if (parts[0].equalsIgnoreCase("using"))
        {
            if (parts.length == 2)
            {
                String bank = parts[1];
                return switch (bank)
                {
                    case "0" -> new UsingDirectiveNode(line, 0);
                    case "1" -> new UsingDirectiveNode(line, 1);
                    case "2" -> new UsingDirectiveNode(line, 2);
                    case "3" -> new UsingDirectiveNode(line, 3);
                    default -> new ErrorNode(line, "Invalid register bank " + bank + " for USING directive");
                };
            }
            return new ErrorNode(line, "Unexpected or invalid parameters for USING directive: " + Arrays.toString(parts));
        }
        return null;
    }),
    END((line, parts) ->
    {
        if (parts[0].equalsIgnoreCase("end"))
        {
            if (parts.length == 1)
            {
                return new EndDirectiveNode(line);
            }
            return new ErrorNode(line, "Unexpected parameters for END directive: " + Arrays.toString(parts));
        }
        return null;
    }),
    DB(new DirectiveParser()
    {
        @Override
        @Nullable
        public Node parse(int line, String[] parts)
        {
            if (parts.length >= 4 && parts[1].equals(":") && parts[2].equalsIgnoreCase("db"))
            {
                return parseData(line, Arrays.copyOfRange(parts, 3, parts.length), parts[0]);
            }
            else if (parts.length >= 3 && parts[1].equalsIgnoreCase("db"))
            {
                if (parts[0].endsWith(":") == parts[1].startsWith(":"))
                {
                    return new ErrorNode(line, "Invalid parameters for DB directive: " + Arrays.toString(parts));
                }
                String label = parts[0];
                if (label.endsWith(":"))
                {
                    label = label.substring(0, label.length() - 1);
                }
                return parseData(line, Arrays.copyOfRange(parts, 2, parts.length), label);
            }
            else if (parts.length >= 2 && parts[0].equalsIgnoreCase("db"))
            {
                return parseData(line, Arrays.copyOfRange(parts, 1, parts.length), null);
            }
            return null;
        }

        private static Node parseData(int line, String[] parts, @Nullable String label)
        {
            List<String> entries = new ArrayList<>(parts.length);
            for (String part : parts)
            {
                for (String subPart : part.split(","))
                {
                    subPart = subPart.trim();
                    if (!subPart.isEmpty())
                    {
                        entries.add(subPart);
                    }
                }
            }

            ByteList data = new ByteArrayList();
            for (String entry : entries)
            {
                if (ParseHelpers.isNumber(entry))
                {
                    data.add(ParseHelpers.parseByte(entry));
                }
                else if (entry.startsWith("'") && entry.endsWith("'"))
                {
                    try
                    {
                        CharBuffer charBuf = CharBuffer.wrap(entry.substring(1, entry.length() - 1));
                        ByteBuffer outBuf = StandardCharsets.US_ASCII.newEncoder()
                                .onMalformedInput(CodingErrorAction.REPORT)
                                .encode(charBuf);
                        data.addAll(ByteList.of(outBuf.array()));
                    }
                    catch (Throwable e)
                    {
                        return new ErrorNode(line, "Non-ASCII string in DB directive: " + entry);
                    }
                }
                else
                {
                    return new ErrorNode(line, "Invalid parameter in DB directive (not a number or quoted ASCII string): " + entry);
                }
            }
            return new DefineByteDirectiveNode(line, data.toByteArray(), Optional.ofNullable(label));
        }
    });

    private static final Directive[] DIRECTIVES = values();

    private final DirectiveParser parser;

    Directive(DirectiveParser parser)
    {
        this.parser = parser;
    }

    @Nullable
    public static Node parseDirective(int line, String[] parts)
    {
        for (Directive directive : DIRECTIVES)
        {
            Node node = directive.parser.parse(line, parts);
            if (node != null)
            {
                return node;
            }
        }
        return null;
    }

    @FunctionalInterface
    private interface DirectiveParser
    {
        @Nullable
        Node parse(int line, String[] parts);

        static DirectiveParser unsupported(int index, String name)
        {
            return (line, parts) ->
            {
                if (index < parts.length && parts[index].equalsIgnoreCase(name))
                {
                    return new ErrorNode(line, String.format(Locale.ROOT, "%s directive unsupported", name));
                }
                return null;
            };
        }
    }
}
