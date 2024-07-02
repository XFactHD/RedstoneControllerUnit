package io.github.xfacthd.rsctrlunit.common.emulator.opcode;

import io.github.xfacthd.rsctrlunit.common.emulator.assembler.node.*;
import io.github.xfacthd.rsctrlunit.common.emulator.util.NodeParser;
import net.minecraft.Util;

import java.util.*;

public enum Opcode
{
    // Irregular 0x00-0x03
    NOP             ("NOP",     0, 0, NoArgOpNode::create),
    AJMP_000        ("AJMP",    1, 1, ParseHelpers.makeOneLabelArgJumpParser()),
    LJMP            ("LJMP",    1, 2, ParseHelpers.makeOneLabelArgJumpParser()),
    RR              ("RR",      1, 0, ParseHelpers.makeOneConstArgParser("A")),
    //Regular 0x04-0x0F
    INC_ACC         ("INC",     1, 0, ParseHelpers.makeOneConstArgParser("A")),
    INC_MEM         ("INC",     1, 1, ParseHelpers.makeOneAddressArgParser()),
    INC_IRO         ("INC",     1, 0, ParseHelpers.makeOneConstArgParser("@R0")),
    INC_IR1         ("INC",     1, 0, ParseHelpers.makeOneConstArgParser("@R1")),
    INC_DR0         ("INC",     1, 0, ParseHelpers.makeOneConstArgParser("R0")),
    INC_DR1         ("INC",     1, 0, ParseHelpers.makeOneConstArgParser("R1")),
    INC_DR2         ("INC",     1, 0, ParseHelpers.makeOneConstArgParser("R2")),
    INC_DR3         ("INC",     1, 0, ParseHelpers.makeOneConstArgParser("R3")),
    INC_DR4         ("INC",     1, 0, ParseHelpers.makeOneConstArgParser("R4")),
    INC_DR5         ("INC",     1, 0, ParseHelpers.makeOneConstArgParser("R5")),
    INC_DR6         ("INC",     1, 0, ParseHelpers.makeOneConstArgParser("R6")),
    INC_DR7         ("INC",     1, 0, ParseHelpers.makeOneConstArgParser("R7")),
    //Irregular 0x10-0x13
    JBC             ("JBC",     2, 2, ParseHelpers.makeOneBitArgJumpParser()),
    ACALL_000       ("ACALL",   1, 1, ParseHelpers.makeOneLabelArgJumpParser()),
    LCALL           ("LCALL",   1, 2, ParseHelpers.makeOneLabelArgJumpParser()),
    RRC             ("RRC",     1, 0, ParseHelpers.makeOneConstArgParser("A")),
    //Regular 0x14-0x1F
    DEC_ACC         ("DEC",     1, 0, ParseHelpers.makeOneConstArgParser("A")),
    DEC_MEM         ("DEC",     1, 1, ParseHelpers.makeOneAddressArgParser()),
    DEC_IRO         ("DEC",     1, 0, ParseHelpers.makeOneConstArgParser("@R0")),
    DEC_IR1         ("DEC",     1, 0, ParseHelpers.makeOneConstArgParser("@R1")),
    DEC_DR0         ("DEC",     1, 0, ParseHelpers.makeOneConstArgParser("R0")),
    DEC_DR1         ("DEC",     1, 0, ParseHelpers.makeOneConstArgParser("R1")),
    DEC_DR2         ("DEC",     1, 0, ParseHelpers.makeOneConstArgParser("R2")),
    DEC_DR3         ("DEC",     1, 0, ParseHelpers.makeOneConstArgParser("R3")),
    DEC_DR4         ("DEC",     1, 0, ParseHelpers.makeOneConstArgParser("R4")),
    DEC_DR5         ("DEC",     1, 0, ParseHelpers.makeOneConstArgParser("R5")),
    DEC_DR6         ("DEC",     1, 0, ParseHelpers.makeOneConstArgParser("R6")),
    DEC_DR7         ("DEC",     1, 0, ParseHelpers.makeOneConstArgParser("R7")),
    //Irregular 0x20-0x23
    JB              ("JB",      2, 2, ParseHelpers.makeOneBitArgJumpParser()),
    AJMP_001        ("AJMP",    1, 1, ParseHelpers.makeOneLabelArgJumpParser()),
    RET             ("RET",     0, 0, NoArgOpNode::create),
    RL              ("RL",      1, 0, ParseHelpers.makeOneConstArgParser("A")),
    //Regular 0x24-0x2F
    ADD_IMM         ("ADD",     2, 1, ParseHelpers.makeTwoArgOneConstOneImmediateParser("A", false)),
    ADD_MEM         ("ADD",     2, 1, ParseHelpers.makeTwoArgOneConstOneAddressParser("A", false)),
    ADD_IR0         ("ADD",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "@R0")),
    ADD_IR1         ("ADD",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "@R1")),
    ADD_DR0         ("ADD",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R0")),
    ADD_DR1         ("ADD",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R1")),
    ADD_DR2         ("ADD",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R2")),
    ADD_DR3         ("ADD",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R3")),
    ADD_DR4         ("ADD",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R4")),
    ADD_DR5         ("ADD",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R5")),
    ADD_DR6         ("ADD",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R6")),
    ADD_DR7         ("ADD",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R7")),
    //Irregular 0x30-0x33
    JNB             ("JNB",     2, 2, ParseHelpers.makeOneBitArgJumpParser()),
    ACALL_001       ("ACALL",   1, 1, ParseHelpers.makeOneLabelArgJumpParser()),
    RETI            ("RETI",    0, 0, NoArgOpNode::create),
    RLC             ("RLC",     1, 0, ParseHelpers.makeOneConstArgParser("A")),
    //Regular 0x34-0x3F
    ADDC_IMM        ("ADDC",    2, 1, ParseHelpers.makeTwoArgOneConstOneImmediateParser("A", false)),
    ADDC_MEM        ("ADDC",    2, 1, ParseHelpers.makeTwoArgOneConstOneAddressParser("A", false)),
    ADDC_IR0        ("ADDC",    2, 0, ParseHelpers.makeTwoConstArgParser("A", "@R0")),
    ADDC_IR1        ("ADDC",    2, 0, ParseHelpers.makeTwoConstArgParser("A", "@R1")),
    ADDC_DR0        ("ADDC",    2, 0, ParseHelpers.makeTwoConstArgParser("A", "R0")),
    ADDC_DR1        ("ADDC",    2, 0, ParseHelpers.makeTwoConstArgParser("A", "R1")),
    ADDC_DR2        ("ADDC",    2, 0, ParseHelpers.makeTwoConstArgParser("A", "R2")),
    ADDC_DR3        ("ADDC",    2, 0, ParseHelpers.makeTwoConstArgParser("A", "R3")),
    ADDC_DR4        ("ADDC",    2, 0, ParseHelpers.makeTwoConstArgParser("A", "R4")),
    ADDC_DR5        ("ADDC",    2, 0, ParseHelpers.makeTwoConstArgParser("A", "R5")),
    ADDC_DR6        ("ADDC",    2, 0, ParseHelpers.makeTwoConstArgParser("A", "R6")),
    ADDC_DR7        ("ADDC",    2, 0, ParseHelpers.makeTwoConstArgParser("A", "R7")),
    //Irregular 0x40-0x43
    JC              ("JC",      1, 1, ParseHelpers.makeOneLabelArgJumpParser()),
    AJMP_010        ("AJMP",    1, 1, ParseHelpers.makeOneLabelArgJumpParser()),
    ORL_MEM_ACC     ("ORL",     2, 1, ParseHelpers.makeTwoArgOneConstOneAddressParser("A", true)),
    ORL_MEM_IMM     ("ORL",     2, 2, ParseHelpers.makeTwoArgOneAddressOneImmediateParser()),
    //Regular 0x44-0x4F
    ORL_IMM         ("ORL",     2, 1, ParseHelpers.makeTwoArgOneConstOneImmediateParser("A", false), 1),
    ORL_MEM         ("ORL",     2, 1, ParseHelpers.makeTwoArgOneConstOneAddressParser("A", false)),
    ORL_IR0         ("ORL",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "@R0")),
    ORL_IR1         ("ORL",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "@R1")),
    ORL_DR0         ("ORL",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R0")),
    ORL_DR1         ("ORL",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R1")),
    ORL_DR2         ("ORL",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R2")),
    ORL_DR3         ("ORL",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R3")),
    ORL_DR4         ("ORL",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R4")),
    ORL_DR5         ("ORL",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R5")),
    ORL_DR6         ("ORL",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R6")),
    ORL_DR7         ("ORL",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R7")),
    //Irregular 0x50-0x53
    JNC             ("JNC",     1, 1, ParseHelpers.makeOneLabelArgJumpParser()),
    ACALL_010       ("ACALL",   1, 1, ParseHelpers.makeOneLabelArgJumpParser()),
    ANL_MEM_ACC     ("ANL",     2, 1, ParseHelpers.makeTwoArgOneConstOneAddressParser("A", true)),
    ANL_MEM_IMM     ("ANL",     2, 2, ParseHelpers.makeTwoArgOneAddressOneImmediateParser()),
    //Regular 0x54-0x5F
    ANL_IMM         ("ANL",     2, 1, ParseHelpers.makeTwoArgOneConstOneImmediateParser("A", false), 1),
    ANL_MEM         ("ANL",     2, 1, ParseHelpers.makeTwoArgOneConstOneAddressParser("A", false)),
    ANL_IR0         ("ANL",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "@R0")),
    ANL_IR1         ("ANL",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "@R1")),
    ANL_DR0         ("ANL",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R0")),
    ANL_DR1         ("ANL",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R1")),
    ANL_DR2         ("ANL",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R2")),
    ANL_DR3         ("ANL",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R3")),
    ANL_DR4         ("ANL",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R4")),
    ANL_DR5         ("ANL",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R5")),
    ANL_DR6         ("ANL",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R6")),
    ANL_DR7         ("ANL",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R7")),
    //Irregular 0x60-0x63
    JZ              ("JZ",      1, 1, ParseHelpers.makeOneLabelArgJumpParser()),
    AJMP_011        ("AJMP",    1, 1, ParseHelpers.makeOneLabelArgJumpParser()),
    XRL_MEM_ACC     ("XRL",     2, 1, ParseHelpers.makeTwoArgOneConstOneAddressParser("A", true)),
    XRL_MEM_IMM     ("XRL",     2, 2, ParseHelpers.makeTwoArgOneAddressOneImmediateParser()),
    //Regular 0x64-0x6F
    XRL_IMM         ("XRL",     2, 1, ParseHelpers.makeTwoArgOneConstOneImmediateParser("A", false), 1),
    XRL_MEM         ("XRL",     2, 1, ParseHelpers.makeTwoArgOneConstOneAddressParser("A", false)),
    XRL_IR0         ("XRL",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "@R0")),
    XRL_IR1         ("XRL",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "@R1")),
    XRL_DR0         ("XRL",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R0")),
    XRL_DR1         ("XRL",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R1")),
    XRL_DR2         ("XRL",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R2")),
    XRL_DR3         ("XRL",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R3")),
    XRL_DR4         ("XRL",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R4")),
    XRL_DR5         ("XRL",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R5")),
    XRL_DR6         ("XRL",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R6")),
    XRL_DR7         ("XRL",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R7")),
    //Irregular 0x70-0x73
    JNZ             ("JNZ",     1, 1, ParseHelpers.makeOneLabelArgJumpParser()),
    ACALL_011       ("ACALL",   1, 1, ParseHelpers.makeOneLabelArgJumpParser()),
    ORL_C_BIT       ("ORL",     2, 1, ParseHelpers.makeTwoArgOneConstOneBitParser("C", false, false)),
    JMP             ("JMP",     1, 0, ParseHelpers.makeOneConstArgParser("@A+DPTR")),
    //Regular 0x74-0x7F
    MOV_ACC_IMM     ("MOV",     2, 1, ParseHelpers.makeTwoArgOneConstOneImmediateParser("A", false)),
    MOV_MEM_IMM     ("MOV",     2, 2, ParseHelpers.makeTwoArgOneAddressOneImmediateParser()),
    MOV_IR0_IMM     ("MOV",     2, 1, ParseHelpers.makeTwoArgOneConstOneImmediateParser("@R0", false)),
    MOV_IR1_IMM     ("MOV",     2, 1, ParseHelpers.makeTwoArgOneConstOneImmediateParser("@R1", false)),
    MOV_DR0_IMM     ("MOV",     2, 1, ParseHelpers.makeTwoArgOneConstOneImmediateParser("R0", false), 1),
    MOV_DR1_IMM     ("MOV",     2, 1, ParseHelpers.makeTwoArgOneConstOneImmediateParser("R1", false), 1),
    MOV_DR2_IMM     ("MOV",     2, 1, ParseHelpers.makeTwoArgOneConstOneImmediateParser("R2", false), 1),
    MOV_DR3_IMM     ("MOV",     2, 1, ParseHelpers.makeTwoArgOneConstOneImmediateParser("R3", false), 1),
    MOV_DR4_IMM     ("MOV",     2, 1, ParseHelpers.makeTwoArgOneConstOneImmediateParser("R4", false), 1),
    MOV_DR5_IMM     ("MOV",     2, 1, ParseHelpers.makeTwoArgOneConstOneImmediateParser("R5", false), 1),
    MOV_DR6_IMM     ("MOV",     2, 1, ParseHelpers.makeTwoArgOneConstOneImmediateParser("R6", false), 1),
    MOV_DR7_IMM     ("MOV",     2, 1, ParseHelpers.makeTwoArgOneConstOneImmediateParser("R7", false), 1),
    //Irregular 0x80-0x83
    SJMP            ("SJMP",    1, 1, ParseHelpers.makeOneLabelArgJumpParser()),
    AJMP_100        ("AJMP",    1, 1, ParseHelpers.makeOneLabelArgJumpParser()),
    ANL_C_BIT       ("ANL",     2, 1, ParseHelpers.makeTwoArgOneConstOneBitParser("C", false, false)),
    MOVC_ACC_IAPC   ("MOVC",    2, 0, ParseHelpers.makeTwoConstArgParser("A", "@A+PC")),
    //Regular 0x84-0x8F
    DIV_AB          ("DIV",     1, 0, ParseHelpers.makeOneConstArgParser("AB")),
    MOV_MEM_MEM     ("MOV",     2, 2, ParseHelpers.makeTwoAddressArgParser()),
    MOV_MEM_IR0     ("MOV",     2, 1, ParseHelpers.makeTwoArgOneConstOneAddressParser("@R0", true)),
    MOV_MEM_IR1     ("MOV",     2, 1, ParseHelpers.makeTwoArgOneConstOneAddressParser("@R1", true)),
    MOV_MEM_DR0     ("MOV",     2, 1, ParseHelpers.makeTwoArgOneConstOneAddressParser("R0", true), 1),
    MOV_MEM_DR1     ("MOV",     2, 1, ParseHelpers.makeTwoArgOneConstOneAddressParser("R1", true), 1),
    MOV_MEM_DR2     ("MOV",     2, 1, ParseHelpers.makeTwoArgOneConstOneAddressParser("R2", true), 1),
    MOV_MEM_DR3     ("MOV",     2, 1, ParseHelpers.makeTwoArgOneConstOneAddressParser("R3", true), 1),
    MOV_MEM_DR4     ("MOV",     2, 1, ParseHelpers.makeTwoArgOneConstOneAddressParser("R4", true), 1),
    MOV_MEM_DR5     ("MOV",     2, 1, ParseHelpers.makeTwoArgOneConstOneAddressParser("R5", true), 1),
    MOV_MEM_DR6     ("MOV",     2, 1, ParseHelpers.makeTwoArgOneConstOneAddressParser("R6", true), 1),
    MOV_MEM_DR7     ("MOV",     2, 1, ParseHelpers.makeTwoArgOneConstOneAddressParser("R7", true), 1),
    //Irregular 0x90-0x93
    MOV_DPTR        ("MOV",     2, 2, ParseHelpers.makeMovDptrParser()),
    ACALL_100       ("ACALL",   1, 1, ParseHelpers.makeOneLabelArgJumpParser()),
    MOV_BIT_C       ("MOV",     2, 1, ParseHelpers.makeTwoArgOneConstOneBitParser("C", true, false)),
    MOVC_ACC_IADPTR ("MOVC",    2, 0, ParseHelpers.makeTwoConstArgParser("A", "@A+DPTR")),
    //Regular 0x94-0x9F
    SUBB_IMM        ("SUBB",    2, 1, ParseHelpers.makeTwoArgOneConstOneImmediateParser("A", false)),
    SUBB_MEM        ("SUBB",    2, 1, ParseHelpers.makeTwoArgOneConstOneAddressParser("A", false)),
    SUBB_IR0        ("SUBB",    2, 0, ParseHelpers.makeTwoConstArgParser("A", "@R0")),
    SUBB_IR1        ("SUBB",    2, 0, ParseHelpers.makeTwoConstArgParser("A", "@R1")),
    SUBB_DR0        ("SUBB",    2, 0, ParseHelpers.makeTwoConstArgParser("A", "R0")),
    SUBB_DR1        ("SUBB",    2, 0, ParseHelpers.makeTwoConstArgParser("A", "R1")),
    SUBB_DR2        ("SUBB",    2, 0, ParseHelpers.makeTwoConstArgParser("A", "R2")),
    SUBB_DR3        ("SUBB",    2, 0, ParseHelpers.makeTwoConstArgParser("A", "R3")),
    SUBB_DR4        ("SUBB",    2, 0, ParseHelpers.makeTwoConstArgParser("A", "R4")),
    SUBB_DR5        ("SUBB",    2, 0, ParseHelpers.makeTwoConstArgParser("A", "R5")),
    SUBB_DR6        ("SUBB",    2, 0, ParseHelpers.makeTwoConstArgParser("A", "R6")),
    SUBB_DR7        ("SUBB",    2, 0, ParseHelpers.makeTwoConstArgParser("A", "R7")),
    //Irregular 0xA0-0xA3
    ORL_C_NBIT      ("ORL",     2, 1, ParseHelpers.makeTwoArgOneConstOneBitParser("C", false, true)),
    AJMP_101        ("AJMP",    1, 1, ParseHelpers.makeOneLabelArgJumpParser()),
    MOV_C_BIT       ("MOV",     2, 1, ParseHelpers.makeTwoArgOneConstOneBitParser("C", false, false)),
    INC_DPTR        ("INC",     1, 0, ParseHelpers.makeOneConstArgParser("DPTR")),
    //Regular 0xA4-0xAF
    MUL_AB          ("MUL",     1, 0, ParseHelpers.makeOneConstArgParser("AB")),
    RESERVED        ("",        0, 0, (line, op, operands) -> null),
    MOV_IR0_MEM     ("MOV",     2, 1, ParseHelpers.makeTwoArgOneConstOneAddressParser("@R0", false)),
    MOV_IR1_MEM     ("MOV",     2, 1, ParseHelpers.makeTwoArgOneConstOneAddressParser("@R1", false)),
    MOV_DR0_MEM     ("MOV",     2, 1, ParseHelpers.makeTwoArgOneConstOneAddressParser("R0", false), 1),
    MOV_DR1_MEM     ("MOV",     2, 1, ParseHelpers.makeTwoArgOneConstOneAddressParser("R1", false), 1),
    MOV_DR2_MEM     ("MOV",     2, 1, ParseHelpers.makeTwoArgOneConstOneAddressParser("R2", false), 1),
    MOV_DR3_MEM     ("MOV",     2, 1, ParseHelpers.makeTwoArgOneConstOneAddressParser("R3", false), 1),
    MOV_DR4_MEM     ("MOV",     2, 1, ParseHelpers.makeTwoArgOneConstOneAddressParser("R4", false), 1),
    MOV_DR5_MEM     ("MOV",     2, 1, ParseHelpers.makeTwoArgOneConstOneAddressParser("R5", false), 1),
    MOV_DR6_MEM     ("MOV",     2, 1, ParseHelpers.makeTwoArgOneConstOneAddressParser("R6", false), 1),
    MOV_DR7_MEM     ("MOV",     2, 1, ParseHelpers.makeTwoArgOneConstOneAddressParser("R7", false), 1),
    //Irregular 0xB0-0xB3
    ANL_C_NBIT      ("ANL",     2, 1, ParseHelpers.makeTwoArgOneConstOneBitParser("C", false, true)),
    ACALL_101       ("ACALL",   1, 1, ParseHelpers.makeOneLabelArgJumpParser()),
    CPL_BIT         ("CPL",     1, 1, ParseHelpers.makeOneBitArgParser(false)),
    CPL_C           ("CPL",     1, 0, ParseHelpers.makeOneConstArgParser("C"), 1),
    //Regular 0xB4-0xBF
    CJNE_ACC_IMM    ("CJNE",    3, 2, ParseHelpers.makeThreeArgJumpParser("A", ParseHelpers::parseImmediateOperand)),
    CJNE_ACC_MEM    ("CJNE",    3, 2, ParseHelpers.makeThreeArgJumpParser("A", ParseHelpers::parseAddressOperand)),
    CJNE_IR0_IMM    ("CJNE",    3, 2, ParseHelpers.makeThreeArgJumpParser("@R0", ParseHelpers::parseImmediateOperand)),
    CJNE_IR1_IMM    ("CJNE",    3, 2, ParseHelpers.makeThreeArgJumpParser("@R1", ParseHelpers::parseImmediateOperand)),
    CJNE_DR0_IMM    ("CJNE",    3, 2, ParseHelpers.makeThreeArgJumpParser("R0", ParseHelpers::parseImmediateOperand)),
    CJNE_DR1_IMM    ("CJNE",    3, 2, ParseHelpers.makeThreeArgJumpParser("R1", ParseHelpers::parseImmediateOperand)),
    CJNE_DR2_IMM    ("CJNE",    3, 2, ParseHelpers.makeThreeArgJumpParser("R2", ParseHelpers::parseImmediateOperand)),
    CJNE_DR3_IMM    ("CJNE",    3, 2, ParseHelpers.makeThreeArgJumpParser("R3", ParseHelpers::parseImmediateOperand)),
    CJNE_DR4_IMM    ("CJNE",    3, 2, ParseHelpers.makeThreeArgJumpParser("R4", ParseHelpers::parseImmediateOperand)),
    CJNE_DR5_IMM    ("CJNE",    3, 2, ParseHelpers.makeThreeArgJumpParser("R5", ParseHelpers::parseImmediateOperand)),
    CJNE_DR6_IMM    ("CJNE",    3, 2, ParseHelpers.makeThreeArgJumpParser("R6", ParseHelpers::parseImmediateOperand)),
    CJNE_DR7_IMM    ("CJNE",    3, 2, ParseHelpers.makeThreeArgJumpParser("R7", ParseHelpers::parseImmediateOperand)),
    //Irregular 0xC0-0xC3
    PUSH            ("PUSH",    1, 1, ParseHelpers.makeOneAddressArgParser()),
    AJMP_110        ("AJMP",    1, 1, ParseHelpers.makeOneLabelArgJumpParser()),
    CLR_BIT         ("CLR",     1, 1, ParseHelpers.makeOneBitArgParser(false)),
    CLR_C           ("CLR",     1, 0, ParseHelpers.makeOneConstArgParser("C"), 1),
    //Regular 0xC4-0xCF
    SWAP            ("SWAP",    1, 0, ParseHelpers.makeOneConstArgParser("A")),
    XCH_MEM         ("XCH",     2, 1, ParseHelpers.makeTwoArgOneConstOneAddressParser("A", false)),
    XCH_IR0         ("XCH",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "@R0")),
    XCH_IR1         ("XCH",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "@R1")),
    XCH_DR0         ("XCH",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R0")),
    XCH_DR1         ("XCH",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R1")),
    XCH_DR2         ("XCH",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R2")),
    XCH_DR3         ("XCH",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R3")),
    XCH_DR4         ("XCH",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R4")),
    XCH_DR5         ("XCH",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R5")),
    XCH_DR6         ("XCH",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R6")),
    XCH_DR7         ("XCH",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R7")),
    //Irregular 0xD0-0xD3
    POP             ("POP",     1, 1, ParseHelpers.makeOneAddressArgParser()),
    ACALL_110       ("ACALL",   1, 1, ParseHelpers.makeOneLabelArgJumpParser()),
    SETB_BIT        ("SETB",    1, 1, ParseHelpers.makeOneBitArgParser(false)),
    SETB_C          ("SETB",    1, 0, ParseHelpers.makeOneConstArgParser("C"), 1),
    //Regular 0xD4-0xDF
    DA              ("DA",      1, 0, ParseHelpers.makeOneConstArgParser("A")),
    DJNZ_MEM        ("DJNZ",    2, 2, ParseHelpers.makeTwoArgJumpParser(ParseHelpers::parseAddressOperand)),
    XCHD_IR0        ("XCHD",    2, 0, ParseHelpers.makeTwoConstArgParser("A", "@R0")),
    XCHD_IR1        ("XCHD",    2, 0, ParseHelpers.makeTwoConstArgParser("A", "@R1")),
    DJNZ_DR0        ("DJNZ",    2, 1, ParseHelpers.makeTwoArgJumpParser("R0")),
    DJNZ_DR1        ("DJNZ",    2, 1, ParseHelpers.makeTwoArgJumpParser("R1")),
    DJNZ_DR2        ("DJNZ",    2, 1, ParseHelpers.makeTwoArgJumpParser("R2")),
    DJNZ_DR3        ("DJNZ",    2, 1, ParseHelpers.makeTwoArgJumpParser("R3")),
    DJNZ_DR4        ("DJNZ",    2, 1, ParseHelpers.makeTwoArgJumpParser("R4")),
    DJNZ_DR5        ("DJNZ",    2, 1, ParseHelpers.makeTwoArgJumpParser("R5")),
    DJNZ_DR6        ("DJNZ",    2, 1, ParseHelpers.makeTwoArgJumpParser("R6")),
    DJNZ_DR7        ("DJNZ",    2, 1, ParseHelpers.makeTwoArgJumpParser("R7")),
    //Irregular 0xE0-0xE3
    MOVX_ACC_IDPTR  ("MOVX",    2, 0, ParseHelpers.makeTwoConstArgParser("A", "@DPTR")),
    AJMP_111        ("AJMP",    1, 1, ParseHelpers.makeOneLabelArgJumpParser()),
    MOVX_ACC_IR0    ("MOVX",    2, 1, ParseHelpers.makeTwoConstArgParser("A", "@R0")),
    MOVX_ACC_IR1    ("MOVX",    2, 1, ParseHelpers.makeTwoConstArgParser("A", "@R1")),
    //Regular 0xE4-0xEF
    CLR_ACC         ("CLR",     1, 0, ParseHelpers.makeOneConstArgParser("A")),
    MOV_ACC_MEM     ("MOV",     2, 1, ParseHelpers.makeTwoArgOneConstOneAddressParser("A", false), 2),
    MOV_ACC_IR0     ("MOV",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "@R0")),
    MOV_ACC_IR1     ("MOV",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "@R1")),
    MOV_ACC_DR0     ("MOV",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R0"), 2),
    MOV_ACC_DR1     ("MOV",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R1"), 2),
    MOV_ACC_DR2     ("MOV",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R2"), 2),
    MOV_ACC_DR3     ("MOV",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R3"), 2),
    MOV_ACC_DR4     ("MOV",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R4"), 2),
    MOV_ACC_DR5     ("MOV",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R5"), 2),
    MOV_ACC_DR6     ("MOV",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R6"), 2),
    MOV_ACC_DR7     ("MOV",     2, 0, ParseHelpers.makeTwoConstArgParser("A", "R7"), 2),
    //Irregular 0xF0-0xF3
    MOVX_IDPTR_ACC  ("MOVX",    2, 0, ParseHelpers.makeTwoConstArgParser("@DPTR", "A")),
    ACALL_111       ("ACALL",   1, 1, ParseHelpers.makeOneLabelArgJumpParser()),
    MOVX_IR0_ACC    ("MOVX",    2, 1, ParseHelpers.makeTwoConstArgParser("@R0", "A")),
    MOVX_IR1_ACC    ("MOVX",    2, 1, ParseHelpers.makeTwoConstArgParser("@R1", "A")),
    //Regular 0xF4-0xFF
    CPL_ACC         ("CPL",     1, 0, ParseHelpers.makeOneConstArgParser("A")),
    MOV_MEM_ACC     ("MOV",     2, 1, ParseHelpers.makeTwoArgOneConstOneAddressParser("A", true), 2),
    MOV_IR0_ACC     ("MOV",     2, 0, ParseHelpers.makeTwoConstArgParser("@R0", "A")),
    MOV_IR1_ACC     ("MOV",     2, 0, ParseHelpers.makeTwoConstArgParser("@R1", "A")),
    MOV_DR0_ACC     ("MOV",     2, 0, ParseHelpers.makeTwoConstArgParser("R0", "A"), 2),
    MOV_DR1_ACC     ("MOV",     2, 0, ParseHelpers.makeTwoConstArgParser("R1", "A"), 2),
    MOV_DR2_ACC     ("MOV",     2, 0, ParseHelpers.makeTwoConstArgParser("R2", "A"), 2),
    MOV_DR3_ACC     ("MOV",     2, 0, ParseHelpers.makeTwoConstArgParser("R3", "A"), 2),
    MOV_DR4_ACC     ("MOV",     2, 0, ParseHelpers.makeTwoConstArgParser("R4", "A"), 2),
    MOV_DR5_ACC     ("MOV",     2, 0, ParseHelpers.makeTwoConstArgParser("R5", "A"), 2),
    MOV_DR6_ACC     ("MOV",     2, 0, ParseHelpers.makeTwoConstArgParser("R6", "A"), 2),
    MOV_DR7_ACC     ("MOV",     2, 0, ParseHelpers.makeTwoConstArgParser("R7", "A"), 2),
    ;

    private static final Opcode[] OPCODES = values();
    private static final Map<String, List<Opcode>> OPCODES_BY_MNEMONIC = Util.make(new HashMap<>(), map ->
    {
        for (Opcode opcode : OPCODES)
        {
            if (opcode == RESERVED) continue;
            map.computeIfAbsent(opcode.mnemonic.toLowerCase(Locale.ROOT), $ -> new ArrayList<>()).add(opcode);
        }
        map.values().forEach(list -> list.sort((a, b) ->
        {
            if (a.priority != b.priority)
            {
                return Integer.compare(b.priority, a.priority);
            }
            return Integer.compare(a.ordinal(), b.ordinal());
        }));
    });

    private final String mnemonic;
    // The amount of operands expected in mnemonic representation
    private final int operands;
    // The amount of program memory bytes consumed by operands in machine code representation
    private final int operandBytes;
    private final NodeParser parser;
    // Parser priority, opcodes with higher values are tried first
    private final int priority;

    Opcode(String mnemonic, int operands, int operandBytes, NodeParser parser)
    {
        this(mnemonic, operands, operandBytes, parser, 0);
    }

    Opcode(String mnemonic, int operands, int operandBytes, NodeParser parser, int priority)
    {
        this.mnemonic = mnemonic;
        this.operands = operands;
        this.operandBytes = operandBytes;
        this.parser = parser;
        this.priority = priority;
    }

    public String getMnemonic()
    {
        return mnemonic;
    }

    public int getOperands()
    {
        return operands;
    }

    public int getOperandBytes()
    {
        return operandBytes;
    }

    public byte toByte()
    {
        return (byte) ordinal();
    }



    public static Node parse(int line, String mnemonic, String[] operands)
    {
        List<Opcode> opcodes = OPCODES_BY_MNEMONIC.get(mnemonic);
        if (opcodes == null)
        {
            return ErrorNode.unrecognizedOpcode(mnemonic, line);
        }

        for (Opcode opcode : opcodes)
        {
            if (operands.length != opcode.getOperands()) continue;

            try
            {
                Node node = opcode.parser.parse(line, opcode, operands);
                if (node != null)
                {
                    return node;
                }
            }
            catch (Throwable t)
            {
                return ErrorNode.invalidOperand(opcode, operands, line);
            }
        }
        return ErrorNode.invalidOperand(opcodes.getFirst(), operands, line);
    }

    public static Opcode fromRomByte(byte romByte)
    {
        return OPCODES[romByte & 0xFF];
    }
}
