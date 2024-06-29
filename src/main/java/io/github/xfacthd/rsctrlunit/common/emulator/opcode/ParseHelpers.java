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
    private static final Pattern HEX_PATTERN = Pattern.compile("[0-9][0-9a-fA-F]*h");
    private static final Pattern BIN_PATTERN = Pattern.compile("[01]{1,16}b");

    private ParseHelpers() { }

    public static NodeParser makeOneConstArgParser(String operand)
    {
        return (line, op, operands) ->
        {
            if (operands[0].equalsIgnoreCase(operand))
            {
                return new NoArgOpNode(line, op);
            }
            return null;
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
            return null;
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
            Byte addrOperand = parseAddressOperand(operands[addrOpIndex]);
            if (addrOperand != null)
            {
                return new SimpleOpNode(line, op, addrOperand);
            }
            return null;
        };
    }

    public static NodeParser makeTwoArgOneAddressOneImmediateParser()
    {
        return (line, op, operands) ->
        {
            Byte addrOperand = parseAddressOperand(operands[0]);
            Byte immediateOp = parseImmediateOperand(operands[1]);
            if (addrOperand != null && immediateOp != null)
            {
                return new SimpleOpNode(line, op, addrOperand, immediateOp);
            }
            return null;
        };
    }

    public static NodeParser makeOneAddressArgParser()
    {
        return (line, op, operands) ->
        {
            Byte operandZero = parseAddressOperand(operands[0]);
            if (operandZero != null)
            {
                return new SimpleOpNode(line, op, operandZero);
            }
            return null;
        };
    }

    public static NodeParser makeTwoAddressArgParser()
    {
        return (line, op, operands) ->
        {
            Byte operandZero = parseAddressOperand(operands[0]);
            Byte operandOne = parseAddressOperand(operands[1]);
            if (operandZero != null && operandOne != null)
            {
                return new SimpleOpNode(line, op, operandZero, operandOne);
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
        return switch (operand)
        {
            case "p0" ->   (byte) Constants.ADDRESS_IO_PORT0;
            case "sp" ->   (byte) Constants.ADDRESS_STACK_POINTER;
            case "dpl" ->  (byte) Constants.ADDRESS_DATA_POINTER_LOWER;
            case "dph" ->  (byte) Constants.ADDRESS_DATA_POINTER_UPPER;
            case "pcon" -> (byte) Constants.ADDRESS_PCON;
            case "tcon" -> (byte) Constants.ADDRESS_TCON;
            case "tmod" -> (byte) Constants.ADDRESS_TMOD;
            case "tl0" ->  (byte) Constants.ADDRESS_TL0;
            case "tl1" ->  (byte) Constants.ADDRESS_TL1;
            case "th0" ->  (byte) Constants.ADDRESS_TH0;
            case "th1" ->  (byte) Constants.ADDRESS_TH1;
            case "p1" ->   (byte) Constants.ADDRESS_IO_PORT1;
            case "scon" -> (byte) Constants.ADDRESS_SCON;
            case "sbuf" -> (byte) Constants.ADDRESS_SBUF;
            case "p2" ->   (byte) Constants.ADDRESS_IO_PORT2;
            case "ie" ->   (byte) Constants.ADDRESS_IE;
            case "p3" ->   (byte) Constants.ADDRESS_IO_PORT3;
            case "ip" ->   (byte) Constants.ADDRESS_IP;
            case "psw" ->  (byte) Constants.ADDRESS_STATUS_WORD;
            case "a" ->    (byte) Constants.ADDRESS_ACCUMULATOR;
            case "b" ->    (byte) Constants.ADDRESS_REGISTER_B;
            default -> null;
        };
    }

    public static Byte parseImmediateOperand(String operand)
    {
        if (!operand.startsWith("#")) return null;

        String immOperand = operand.substring(1);
        if (isNumber(immOperand))
        {
            return parseByte(immOperand);
        }
        return null;
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
                return null;
            }
            return switch (operand.toLowerCase())
            {
                case "it0" -> (byte) Constants.BIT_ADDRESS_TCON_IT0;
                case "ie0" -> (byte) Constants.BIT_ADDRESS_TCON_IE0;
                case "it1" -> (byte) Constants.BIT_ADDRESS_TCON_IT1;
                case "ie1" -> (byte) Constants.BIT_ADDRESS_TCON_IE1;
                case "tr0" -> (byte) Constants.BIT_ADDRESS_TIMER0_RUNNING;
                case "tf0" -> (byte) Constants.BIT_ADDRESS_TIMER0_OVERFLOW;
                case "tr1" -> (byte) Constants.BIT_ADDRESS_TIMER1_RUNNING;
                case "tf1" -> (byte) Constants.BIT_ADDRESS_TIMER1_OVERFLOW;
                case "ri" -> (byte) Constants.BIT_ADDRESS_SCON_RI;
                case "ti" -> (byte) Constants.BIT_ADDRESS_SCON_TI;
                case "rb8" -> (byte) Constants.BIT_ADDRESS_SCON_RB8;
                case "tb8" -> (byte) Constants.BIT_ADDRESS_SCRON_TB8;
                case "ren" -> (byte) Constants.BIT_ADDRESS_SCON_REN;
                case "sm2" -> (byte) Constants.BIT_ADDRESS_SCON_SM2;
                case "sm1" -> (byte) Constants.BIT_ADDRESS_SCON_SM1;
                case "sm0" -> (byte) Constants.BIT_ADDRESS_SCON_SM0;
                case "ex0" -> (byte) Constants.BIT_ADDRESS_IE_EX0;
                case "et0" -> (byte) Constants.BIT_ADDRESS_IE_ET0;
                case "ex1" -> (byte) Constants.BIT_ADDRESS_IE_EX1;
                case "et1" -> (byte) Constants.BIT_ADDRESS_IE_ET1;
                case "es" -> (byte) Constants.BIT_ADDRESS_IE_ES;
                case "ea" -> (byte) Constants.BIT_ADDRESS_IE_EA;
                case "px0" -> (byte) Constants.BIT_ADDRESS_IP_PX0;
                case "pt0" -> (byte) Constants.BIT_ADDRESS_IP_PT0;
                case "px1" -> (byte) Constants.BIT_ADDRESS_IP_PX1;
                case "pt1" -> (byte) Constants.BIT_ADDRESS_IP_PT1;
                case "ps" -> (byte) Constants.BIT_ADDRESS_IP_PS;
                case "p" -> (byte) Constants.BIT_ADDRESS_PARITY;
                case "ov" -> (byte) Constants.BIT_ADDRESS_OVERFLOW;
                case "rs0" -> (byte) Constants.BIT_ADDRESS_REGISTER_SELECT_0;
                case "rs1" -> (byte) Constants.BIT_ADDRESS_REGISTER_SELECT_1;
                case "f0" -> (byte) Constants.BIT_ADDRESS_FLAG0;
                case "ac" -> (byte) Constants.BIT_ADDRESS_AUX_CARRY;
                case "cy" -> (byte) Constants.BIT_ADDRESS_CARRY;
                default -> null;
            };
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
