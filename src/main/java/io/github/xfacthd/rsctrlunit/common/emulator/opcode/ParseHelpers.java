package io.github.xfacthd.rsctrlunit.common.emulator.opcode;

import io.github.xfacthd.rsctrlunit.common.emulator.assembler.node.*;
import io.github.xfacthd.rsctrlunit.common.emulator.util.Constants;
import io.github.xfacthd.rsctrlunit.common.emulator.util.NodeParser;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;
import java.util.function.*;
import java.util.regex.Pattern;

public final class ParseHelpers
{
    private static final Pattern HEX_PATTERN = Pattern.compile("[0-9][0-9a-f]*h");
    private static final Pattern BIN_PATTERN = Pattern.compile("[01]+b");

    private ParseHelpers() { }

    public static NodeParser makeOneConstArgParser(String operand)
    {
        return (line, op, operands) ->
        {
            if (operands[0].equalsIgnoreCase(operand))
            {
                return new NoArgOpNode(line, op);
            }
            return ErrorNode.invalidOperand(op, operands, line);
        };
    }

    public static NodeParser makeTwoConstArgParser(String firstOperand, String secondOperand)
    {
        return (line, op, operands) ->
        {
            if (operands[0].equalsIgnoreCase(firstOperand) && operands[1].equalsIgnoreCase(secondOperand))
            {
                return new NoArgOpNode(line, op);
            }
            return ErrorNode.invalidOperand(op, operands, line);
        };
    }

    public static NodeParser makeTwoArgOneConstOneBitParser(String constOperand, boolean secondConst, boolean bitComplement)
    {
        return (line, op, operands) ->
        {
            int constOpIndex = secondConst ? 1 : 0;
            if (!operands[constOpIndex].equalsIgnoreCase(constOperand)) return null;

            int bitOpIndex = secondConst ? 0 : 1;
            Byte bitOperand = parseBitOperand(operands[bitOpIndex], bitComplement);
            return bitOperand != null ? new SimpleOpNode(line, op, bitOperand) : null;
        };
    }

    public static NodeParser makeTwoArgOneConstOneImmediateParser(String constOperand, boolean secondConst)
    {
        return (line, op, operands) ->
        {
            int constOpIndex = secondConst ? 1 : 0;
            if (!operands[constOpIndex].equalsIgnoreCase(constOperand)) return null;

            Byte immediateOp = parseImmediateOperand(operands[secondConst ? 0 : 1]);
            if (immediateOp != null)
            {
                return new SimpleOpNode(line, op, immediateOp);
            }
            return null;
        };
    }

    public static NodeParser makeOneBitArgParser(boolean bitComplement)
    {
        return (line, op, operands) ->
        {
            Byte bitOperand = parseBitOperand(operands[0], bitComplement);
            return bitOperand != null ? new SimpleOpNode(line, op, bitOperand) : null;
        };
    }

    public static NodeParser makeTwoArgOneConstOneAddressParser(String constOperand, boolean secondConst)
    {
        return (line, op, operands) ->
        {
            int constOpIndex = secondConst ? 1 : 0;
            if (!operands[constOpIndex].equalsIgnoreCase(constOperand)) return null;

            int addrOpIndex = secondConst ? 0 : 1;
            if (isNumber(operands[addrOpIndex]))
            {
                return new SimpleOpNode(line, op, parseByte(operands[addrOpIndex]));
            }
            return null;
        };
    }

    public static NodeParser makeTwoArgOneAddressOneImmediateParser()
    {
        return (line, op, operands) ->
        {
            Byte immediateOp = parseImmediateOperand(operands[1]);
            if (immediateOp != null && isNumber(operands[0]))
            {
                return new SimpleOpNode(line, op, parseByte(operands[0]), immediateOp);
            }
            return null;
        };
    }

    public static NodeParser makeOneAddressArgParser()
    {
        return (line, op, operands) ->
        {
            if (isNumber(operands[0]))
            {
                return new SimpleOpNode(line, op, parseByte(operands[0]));
            }
            return null;
        };
    }

    public static NodeParser makeTwoAddressArgParser()
    {
        return (line, op, operands) ->
        {
            if (isNumber(operands[0]) && isNumber(operands[1]))
            {
                return new SimpleOpNode(line, op, parseByte(operands[0]), parseByte(operands[1]));
            }
            return null;
        };
    }

    public static NodeParser makeOneLabelArgJumpParser()
    {
        return (line, op, operands) -> new JumpNode(line, op, operands[0]);
    }

    public static NodeParser makeOneBitArgJumpParser()
    {
        return (line, op, operands) -> new JumpNode(line, op, operands[1], parseBitOperand(operands[0], false));
    }

    public static NodeParser makeTwoArgJumpParser(Function<String, Byte> firstOperandParser)
    {
        return (line, op, operands) ->
        {
            Byte firstOperand = firstOperandParser.apply(operands[0]);
            if (firstOperand != null)
            {
                return new JumpNode(line, op, operands[1], firstOperand);
            }
            return null;
        };
    }

    public static NodeParser makeTwoArgJumpParser(String firstOperand)
    {
        return (line, op, operands) ->
        {
            if (operands[0].equalsIgnoreCase(firstOperand))
            {
                return new JumpNode(line, op, operands[1]);
            }
            return null;
        };
    }

    public static NodeParser makeThreeArgJumpParser(String firstOperand, Function<String, Byte> secondOperandParser)
    {
        return (line, op, operands) ->
        {
            Byte secondOperand = secondOperandParser.apply(operands[1]);
            if (secondOperand != null && operands[0].equalsIgnoreCase(firstOperand))
            {
                return new JumpNode(line, op, operands[2], secondOperand);
            }
            return null;
        };
    }

    public static NodeParser makeMovDptrParser()
    {
        return (line, op, operands) ->
        {
            if (!operands[0].equalsIgnoreCase("DPTR") || !operands[1].startsWith("#")) return null;

            String immOperand = operands[1].substring(1);
            if (isNumber(immOperand))
            {
                int value = parseInt(immOperand);
                if (value <= 65535)
                {
                    return new SimpleOpNode(line, op, (byte) (value >> 8 & 0xFF), (byte) (value & 0xFF));
                }
            }
            return null;
        };
    }

    public static Byte parseAddressOperand(String operand)
    {
        if (isNumber(operand))
        {
            return parseByte(operand);
        }
        return null;
    }

    public static Byte parseImmediateOperand(String operand)
    {
        if (!operand.startsWith("#")) return null;

        String immOperand = operand.substring(1);
        return parseAddressOperand(immOperand);
    }

    public static Byte parseBitOperand(String operand, boolean bitComplement)
    {
        if (bitComplement != operand.startsWith("/")) return null;

        if (bitComplement)
        {
            operand = operand.substring(1);
        }

        if (!operand.contains("."))
        {
            if (isNumber(operand))
            {
                int value = parseInt(operand);
                if (value >= 0 && value <= 0x7F)
                {
                    return (byte) (value & 0xFF);
                }
            }
            return null;
        }

        String[] parts = operand.split("\\.");
        if (parts.length != 2) return null;

        int baseAdress;
        String register = parts[0].toLowerCase(Locale.ROOT);
        switch (register)
        {
            case "p0" -> baseAdress = Constants.ADDRESS_IO_PORT0;
            case "tcon" -> baseAdress = Constants.ADDRESS_TCON;
            case "p1" -> baseAdress = Constants.ADDRESS_IO_PORT1;
            case "scon" -> baseAdress = Constants.ADDRESS_SCON;
            case "p2" -> baseAdress = Constants.ADDRESS_IO_PORT2;
            case "ie" -> baseAdress = Constants.ADDRESS_IE;
            case "p3" -> baseAdress = Constants.ADDRESS_IO_PORT3;
            case "ip" -> baseAdress = Constants.ADDRESS_IP;
            case "psw" -> baseAdress = Constants.ADDRESS_STATUS_WORD;
            case "a" -> baseAdress = Constants.ADDRESS_ACCUMULATOR;
            case "b" -> baseAdress = Constants.ADDRESS_REGISTER_B;
            default ->
            {
                if (isNumber(register))
                {
                    int value = parseInt(register);
                    if (value >= 0x20 && value <= 0x2F)
                    {
                        baseAdress = (value - 0x20) * 8;
                        break;
                    }
                }
                return null;
            }
        }

        int bitAddress = Integer.parseInt(parts[1]);
        if (bitAddress >= 0 && bitAddress <= 7)
        {
            return (byte) (baseAdress | bitAddress);
        }
        return null;
    }

    public static boolean isNumber(String operand)
    {
        if (operand.endsWith("h"))
        {
            return HEX_PATTERN.matcher(operand).matches();
        }
        if (operand.endsWith("b"))
        {
            return BIN_PATTERN.matcher(operand).matches();
        }
        return StringUtils.isNumeric(operand);
    }

    private static byte parseByte(String operand)
    {
        return (byte) (parseInt(operand) & 0xFF);
    }

    public static int parseInt(String operand)
    {
        if (operand.endsWith("h"))
        {
            return Integer.parseInt(operand.substring(0, operand.length() - 1), 16);
        }
        if (operand.endsWith("b"))
        {
            return Integer.parseInt(operand.substring(0, operand.length() - 1), 2);
        }
        return Integer.parseInt(operand);
    }
}
