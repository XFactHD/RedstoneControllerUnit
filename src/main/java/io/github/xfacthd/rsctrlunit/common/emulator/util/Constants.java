package io.github.xfacthd.rsctrlunit.common.emulator.util;

public final class Constants
{
    public static final int ROM_SIZE = 65536;
    public static final int RAM_SIZE = 256;
    public static final int SFR_SIZE = 128; // Size of the double-mapped RAM area (SFR via direct, data via indirect)
    public static final int SFR_START = RAM_SIZE - SFR_SIZE;
    public static final int EXT_RAM_SIZE = 8192; // Could support up to 64k but this seems more reasonable
    public static final int INITIAL_PROGRAM_COUNTER = 0;
    public static final int INITIAL_STACK_POINTER = 0x07;

    public static final int[] REGISTER_BASE_ADDRESS = new int[] { 0x00, 0x08, 0x10, 0x18 };

    public static final int ADDRESS_IO_PORT0 = 0x80;
    public static final int ADDRESS_STACK_POINTER = 0x81;
    public static final int ADDRESS_DATA_POINTER_LOWER = 0x82;
    public static final int ADDRESS_DATA_POINTER_UPPER = 0x83;
    public static final int ADDRESS_PCON = 0x87;
    public static final int ADDRESS_TCON = 0x88;
    public static final int ADDRESS_TMOD = 0x89;
    public static final int ADDRESS_TL0 = 0x8A;
    public static final int ADDRESS_TL1 = 0x8B;
    public static final int ADDRESS_TH0 = 0x8C;
    public static final int ADDRESS_TH1 = 0x8D;
    public static final int ADDRESS_IO_PORT1 = 0x90;
    public static final int ADDRESS_SCON = 0x98;
    public static final int ADDRESS_SBUF = 0x99;
    public static final int ADDRESS_IO_PORT2 = 0xA0;
    public static final int ADDRESS_IE = 0xA8;
    public static final int ADDRESS_IO_PORT3 = 0xB0;
    public static final int ADDRESS_IP = 0xB8;
    public static final int ADDRESS_STATUS_WORD = 0xD0;
    public static final int ADDRESS_ACCUMULATOR = 0xE0;
    public static final int ADDRESS_REGISTER_B = 0xF0;
    public static final int[] IO_PORTS = new int[] {
            ADDRESS_IO_PORT0, ADDRESS_IO_PORT1, ADDRESS_IO_PORT2, ADDRESS_IO_PORT3
    };

    public static final int BIT_ADDRESS_TCON_IT0 = ADDRESS_TCON;
    public static final int BIT_ADDRESS_TCON_IE0 = ADDRESS_TCON + 1;
    public static final int BIT_ADDRESS_TCON_IT1 = ADDRESS_TCON + 2;
    public static final int BIT_ADDRESS_TCON_IE1 = ADDRESS_TCON + 3;
    public static final int BIT_ADDRESS_TIMER0_RUNNING = ADDRESS_TCON + 4;
    public static final int BIT_ADDRESS_TIMER0_OVERFLOW = ADDRESS_TCON + 5;
    public static final int BIT_ADDRESS_TIMER1_RUNNING = ADDRESS_TCON + 6;
    public static final int BIT_ADDRESS_TIMER1_OVERFLOW = ADDRESS_TCON + 7;
    public static final int BIT_ADDRESS_SCON_RI = ADDRESS_SCON;
    public static final int BIT_ADDRESS_SCON_TI = ADDRESS_SCON + 1;
    public static final int BIT_ADDRESS_SCON_RB8 = ADDRESS_SCON + 2;
    public static final int BIT_ADDRESS_SCRON_TB8 = ADDRESS_SCON + 3;
    public static final int BIT_ADDRESS_SCON_REN = ADDRESS_SCON + 4;
    public static final int BIT_ADDRESS_SCON_SM2 = ADDRESS_SCON + 5;
    public static final int BIT_ADDRESS_SCON_SM1 = ADDRESS_SCON + 6;
    public static final int BIT_ADDRESS_SCON_SM0 = ADDRESS_SCON + 7;
    public static final int BIT_ADDRESS_IE_EX0 = ADDRESS_IE;
    public static final int BIT_ADDRESS_IE_ET0 = ADDRESS_IE + 1;
    public static final int BIT_ADDRESS_IE_EX1 = ADDRESS_IE + 2;
    public static final int BIT_ADDRESS_IE_ET1 = ADDRESS_IE + 3;
    public static final int BIT_ADDRESS_IE_ES = ADDRESS_IE + 4;
    public static final int BIT_ADDRESS_IE_EA = ADDRESS_IE + 7;
    public static final int BIT_ADDRESS_IP_PX0 = ADDRESS_IP;
    public static final int BIT_ADDRESS_IP_PT0 = ADDRESS_IP + 1;
    public static final int BIT_ADDRESS_IP_PX1 = ADDRESS_IP + 2;
    public static final int BIT_ADDRESS_IP_PT1 = ADDRESS_IP + 3;
    public static final int BIT_ADDRESS_IP_PS = ADDRESS_IP + 4;
    public static final int BIT_ADDRESS_PARITY = ADDRESS_STATUS_WORD;
    public static final int BIT_ADDRESS_OVERFLOW = ADDRESS_STATUS_WORD + 2;
    public static final int BIT_ADDRESS_REGISTER_SELECT_0 = ADDRESS_STATUS_WORD + 3;
    public static final int BIT_ADDRESS_REGISTER_SELECT_1 = ADDRESS_STATUS_WORD + 4;
    public static final int BIT_ADDRESS_FLAG0 = ADDRESS_STATUS_WORD + 5;
    public static final int BIT_ADDRESS_AUX_CARRY = ADDRESS_STATUS_WORD + 6;
    public static final int BIT_ADDRESS_CARRY = ADDRESS_STATUS_WORD + 7;

    private Constants() { }
}
