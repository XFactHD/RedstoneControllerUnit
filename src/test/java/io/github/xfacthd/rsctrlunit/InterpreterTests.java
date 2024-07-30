package io.github.xfacthd.rsctrlunit;

import io.github.xfacthd.rsctrlunit.common.emulator.interpreter.Interpreter;
import io.github.xfacthd.rsctrlunit.common.emulator.opcode.Opcode;
import io.github.xfacthd.rsctrlunit.common.emulator.util.Code;
import io.github.xfacthd.rsctrlunit.common.emulator.util.Constants;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("CodeBlock2Expr")
public class InterpreterTests
{
    private static final Opcode[] AJMP = new Opcode[] { Opcode.AJMP_000, Opcode.AJMP_001, Opcode.AJMP_010, Opcode.AJMP_011, Opcode.AJMP_100, Opcode.AJMP_101, Opcode.AJMP_110, Opcode.AJMP_111 };
    private static final Opcode[] ACALL = new Opcode[] { Opcode.ACALL_000, Opcode.ACALL_001, Opcode.ACALL_010, Opcode.ACALL_011, Opcode.ACALL_100, Opcode.ACALL_101, Opcode.ACALL_110, Opcode.ACALL_111 };
    private static final Opcode[] CJNE_IRN_IMM = new Opcode[] { Opcode.CJNE_IR0_IMM, Opcode.CJNE_IR1_IMM };
    private static final Opcode[] CJNE_DRN_IMM = new Opcode[] { Opcode.CJNE_DR0_IMM, Opcode.CJNE_DR1_IMM, Opcode.CJNE_DR2_IMM, Opcode.CJNE_DR3_IMM, Opcode.CJNE_DR4_IMM, Opcode.CJNE_DR5_IMM, Opcode.CJNE_DR6_IMM, Opcode.CJNE_DR7_IMM };
    private static final Opcode[] DJNZ_DRN = new Opcode[] { Opcode.DJNZ_DR0, Opcode.DJNZ_DR1, Opcode.DJNZ_DR2, Opcode.DJNZ_DR3, Opcode.DJNZ_DR4, Opcode.DJNZ_DR5, Opcode.DJNZ_DR6, Opcode.DJNZ_DR7, };
    private static final Opcode[] INC_IRN = new Opcode[] { Opcode.INC_IR0, Opcode.INC_IR1 };
    private static final Opcode[] INC_DRN = new Opcode[] { Opcode.INC_DR0, Opcode.INC_DR1, Opcode.INC_DR2, Opcode.INC_DR3, Opcode.INC_DR4, Opcode.INC_DR5, Opcode.INC_DR6, Opcode.INC_DR7 };
    private static final Opcode[] DEC_IRN = new Opcode[] { Opcode.DEC_IR0, Opcode.DEC_IR1 };
    private static final Opcode[] DEC_DRN = new Opcode[] { Opcode.DEC_DR0, Opcode.DEC_DR1, Opcode.DEC_DR2, Opcode.DEC_DR3, Opcode.DEC_DR4, Opcode.DEC_DR5, Opcode.DEC_DR6, Opcode.DEC_DR7 };
    private static final Opcode[] ADD_IRN = new Opcode[] { Opcode.ADD_IR0, Opcode.ADD_IR1 };
    private static final Opcode[] ADD_DRN = new Opcode[] { Opcode.ADD_DR0, Opcode.ADD_DR1, Opcode.ADD_DR2, Opcode.ADD_DR3, Opcode.ADD_DR4, Opcode.ADD_DR5, Opcode.ADD_DR6, Opcode.ADD_DR7 };
    private static final Opcode[] ADDC_IRN = new Opcode[] { Opcode.ADDC_IR0, Opcode.ADDC_IR1 };
    private static final Opcode[] ADDC_DRN = new Opcode[] { Opcode.ADDC_DR0, Opcode.ADDC_DR1, Opcode.ADDC_DR2, Opcode.ADDC_DR3, Opcode.ADDC_DR4, Opcode.ADDC_DR5, Opcode.ADDC_DR6, Opcode.ADDC_DR7 };
    private static final Opcode[] ORL_IRN = new Opcode[] { Opcode.ORL_IR0, Opcode.ORL_IR1 };
    private static final Opcode[] ORL_DRN = new Opcode[] { Opcode.ORL_DR0, Opcode.ORL_DR1, Opcode.ORL_DR2, Opcode.ORL_DR3, Opcode.ORL_DR4, Opcode.ORL_DR5, Opcode.ORL_DR6, Opcode.ORL_DR7 };
    private static final Opcode[] ANL_IRN = new Opcode[] { Opcode.ANL_IR0, Opcode.ANL_IR1 };
    private static final Opcode[] ANL_DRN = new Opcode[] { Opcode.ANL_DR0, Opcode.ANL_DR1, Opcode.ANL_DR2, Opcode.ANL_DR3, Opcode.ANL_DR4, Opcode.ANL_DR5, Opcode.ANL_DR6, Opcode.ANL_DR7 };
    private static final Opcode[] XRL_IRN = new Opcode[] { Opcode.XRL_IR0, Opcode.XRL_IR1 };
    private static final Opcode[] XRL_DRN = new Opcode[] { Opcode.XRL_DR0, Opcode.XRL_DR1, Opcode.XRL_DR2, Opcode.XRL_DR3, Opcode.XRL_DR4, Opcode.XRL_DR5, Opcode.XRL_DR6, Opcode.XRL_DR7 };
    private static final Opcode[] SUBB_IRN = new Opcode[] { Opcode.SUBB_IR0, Opcode.SUBB_IR1 };
    private static final Opcode[] SUBB_DRN = new Opcode[] { Opcode.SUBB_DR0, Opcode.SUBB_DR1, Opcode.SUBB_DR2, Opcode.SUBB_DR3, Opcode.SUBB_DR4, Opcode.SUBB_DR5, Opcode.SUBB_DR6, Opcode.SUBB_DR7 };
    private static final Opcode[] XCH_IRN = new Opcode[] { Opcode.XCH_IR0, Opcode.XCH_IR1 };
    private static final Opcode[] XCH_DRN = new Opcode[] { Opcode.XCH_DR0, Opcode.XCH_DR1, Opcode.XCH_DR2, Opcode.XCH_DR3, Opcode.XCH_DR4, Opcode.XCH_DR5, Opcode.XCH_DR6, Opcode.XCH_DR7 };
    private static final Opcode[] XCHD_IRN = new Opcode[] { Opcode.XCHD_IR0, Opcode.XCHD_IR1 };
    private static final Opcode[] MOV_IRN_IMM = new Opcode[] { Opcode.MOV_IR0_IMM, Opcode.MOV_IR1_IMM };
    private static final Opcode[] MOV_DRN_IMM = new Opcode[] { Opcode.MOV_DR0_IMM, Opcode.MOV_DR1_IMM, Opcode.MOV_DR2_IMM, Opcode.MOV_DR3_IMM, Opcode.MOV_DR4_IMM, Opcode.MOV_DR5_IMM, Opcode.MOV_DR6_IMM, Opcode.MOV_DR7_IMM };
    private static final Opcode[] MOV_MEM_IRN = new Opcode[] { Opcode.MOV_MEM_IR0, Opcode.MOV_MEM_IR1 };
    private static final Opcode[] MOV_MEM_DRN = new Opcode[] { Opcode.MOV_MEM_DR0, Opcode.MOV_MEM_DR1, Opcode.MOV_MEM_DR2, Opcode.MOV_MEM_DR3, Opcode.MOV_MEM_DR4, Opcode.MOV_MEM_DR5, Opcode.MOV_MEM_DR6, Opcode.MOV_MEM_DR7 };
    private static final Opcode[] MOV_ACC_IRN = new Opcode[] { Opcode.MOV_ACC_IR0, Opcode.MOV_ACC_IR1 };
    private static final Opcode[] MOV_ACC_DRN = new Opcode[] { Opcode.MOV_ACC_DR0, Opcode.MOV_ACC_DR1, Opcode.MOV_ACC_DR2, Opcode.MOV_ACC_DR3, Opcode.MOV_ACC_DR4, Opcode.MOV_ACC_DR5, Opcode.MOV_ACC_DR6, Opcode.MOV_ACC_DR7 };
    private static final Opcode[] MOV_IRN_ACC = new Opcode[] { Opcode.MOV_IR0_ACC, Opcode.MOV_IR1_ACC };
    private static final Opcode[] MOV_DRN_ACC = new Opcode[] { Opcode.MOV_DR0_ACC, Opcode.MOV_DR1_ACC, Opcode.MOV_DR2_ACC, Opcode.MOV_DR3_ACC, Opcode.MOV_DR4_ACC, Opcode.MOV_DR5_ACC, Opcode.MOV_DR6_ACC, Opcode.MOV_DR7_ACC };
    private static final Opcode[] MOVX_ACC_IRN = new Opcode[] { Opcode.MOVX_ACC_IR0, Opcode.MOVX_ACC_IR1 };
    private static final Opcode[] MOVX_IRN_ACC = new Opcode[] { Opcode.MOVX_IR0_ACC, Opcode.MOVX_IR1_ACC };

    @Test
    void testNop()
    {
        test(new int[] { Opcode.NOP.toByte() }, 0, ram -> {}, ram -> {}, 1);
    }

    @Test
    void testSjmp_Forward()
    {
        test(new int[] { Opcode.SJMP.toByte(), 2 }, 0, ram -> {}, ram -> {}, 4);
    }

    @Test
    void testSjmp_Forward_Rollover()
    {
        test(new int[] { Opcode.SJMP.toByte(), 2 }, 65533, ram -> {}, ram -> {}, 1);
    }

    @Test
    void testSjmp_Backward()
    {
        test(new int[] { Opcode.SJMP.toByte(), -2 }, 0, ram -> {}, ram -> {}, 0);
    }

    @Test
    void testSjmp_Backward_Rollover()
    {
        test(new int[] { Opcode.SJMP.toByte(), -4 }, 0, ram -> {}, ram -> {}, 65534);
    }

    @Test
    void testJmp()
    {
        test(new int[] { Opcode.JMP.toByte() }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 2);
            ram.setSfr(Constants.ADDRESS_DATA_POINTER_LOWER, 3);
        }, ram -> {}, 5);
    }

    @Test
    void testAjmp()
    {
        for (int i = 0; i < 8; i++)
        {
            test(new int[] { AJMP[i].toByte(), 2 }, 0, ram -> {}, ram -> {}, (i << 8) | 2);
        }
    }

    @Test
    void testAcall()
    {
        for (int i = 0; i < 8; i++)
        {
            test(new int[] { ACALL[i].toByte(), 4 }, 2, ram -> {}, ram -> {
                ram.setSfr(Constants.ADDRESS_STACK_POINTER, Constants.INITIAL_STACK_POINTER + 2);
                ram.setRam(Constants.INITIAL_STACK_POINTER + 1, 4);
                ram.setRam(Constants.INITIAL_STACK_POINTER + 2, 0);
            }, (i << 8) | 4);
        }
    }

    @Test
    void testLjmp()
    {
        test(new int[] { Opcode.LJMP.toByte(), 2, 2 }, 0, ram -> {}, ram -> {}, 0x0202);
    }

    @Test
    void testLcall()
    {
        test(new int[] { Opcode.LCALL.toByte(), 2, 2 }, 2, ram -> {}, ram -> {
            ram.setSfr(Constants.ADDRESS_STACK_POINTER, Constants.INITIAL_STACK_POINTER + 2);
            ram.setRam(Constants.INITIAL_STACK_POINTER + 1, 5);
            ram.setRam(Constants.INITIAL_STACK_POINTER + 2, 0);
        }, 0x0202);
    }

    @Test
    void testRet()
    {
        test(new int[] { Opcode.RET.toByte() }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_STACK_POINTER, Constants.INITIAL_STACK_POINTER + 2);
            ram.setRam(Constants.INITIAL_STACK_POINTER + 1, 2);
            ram.setRam(Constants.INITIAL_STACK_POINTER + 2, 2);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_STACK_POINTER, Constants.INITIAL_STACK_POINTER);
        }, 0x0202);
    }

    @Test
    void testReti()
    {
        test(new int[] { Opcode.RETI.toByte() }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_STACK_POINTER, Constants.INITIAL_STACK_POINTER + 2);
            ram.setRam(Constants.INITIAL_STACK_POINTER + 1, 2);
            ram.setRam(Constants.INITIAL_STACK_POINTER + 2, 2);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_STACK_POINTER, Constants.INITIAL_STACK_POINTER);
        }, 0x0202);
    }

    @Test
    void testJbc_BitSet_Forward()
    {
        test(new int[] { Opcode.JBC.toByte(), 0x8C, 4 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_TCON, 0x1A);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_TCON, 0x0A);
        }, 7);
    }

    @Test
    void testJbc_BitSet_Backward()
    {
        test(new int[] { Opcode.JBC.toByte(), 0x8C, -3 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_TCON, 0x1A);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_TCON, 0x0A);
        }, 0);
    }

    @Test
    void testJbc_BitClear()
    {
        test(new int[] { Opcode.JBC.toByte(), 0x8C, 4 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_TCON, 0x0A);
        }, ram -> {}, 3);
    }

    @Test
    void testJb_BitSet_Forward()
    {
        test(new int[] { Opcode.JB.toByte(), 0x8C, 4 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_TCON, 0x1A);
        }, ram -> {}, 7);
    }

    @Test
    void testJb_BitSet_Backward()
    {
        test(new int[] { Opcode.JB.toByte(), 0x8C, -3 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_TCON, 0x1A);
        }, ram -> {}, 0);
    }

    @Test
    void testJb_BitClear()
    {
        test(new int[] { Opcode.JB.toByte(), 0x8C, 4 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_TCON, 0x0A);
        }, ram -> {}, 3);
    }

    @Test
    void testJnb_BitSet()
    {
        test(new int[] { Opcode.JNB.toByte(), 0x8C, 4 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_TCON, 0x1A);
        }, ram -> {}, 3);
    }

    @Test
    void testJnb_BitClear_Forward()
    {
        test(new int[] { Opcode.JNB.toByte(), 0x8C, 4 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_TCON, 0x0A);
        }, ram -> {}, 7);
    }

    @Test
    void testJnb_BitClear_Backward()
    {
        test(new int[] { Opcode.JNB.toByte(), 0x8C, -3 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_TCON, 0x0A);
        }, ram -> {}, 0);
    }

    @Test
    void testJc_BitSet_Forward()
    {
        test(new int[] { Opcode.JC.toByte(), 4 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, ram -> {}, 6);
    }

    @Test
    void testJc_BitSet_Backward()
    {
        test(new int[] { Opcode.JC.toByte(), -2 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, ram -> {}, 0);
    }

    @Test
    void testJc_BitClear()
    {
        test(new int[] { Opcode.JC.toByte(), 4 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0);
        }, ram -> {}, 2);
    }

    @Test
    void testJnc_BitSet()
    {
        test(new int[] { Opcode.JNC.toByte(), 4 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, ram -> {}, 2);
    }

    @Test
    void testJnc_BitClear_Forward()
    {
        test(new int[] { Opcode.JNC.toByte(), 4 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0);
        }, ram -> {}, 6);
    }

    @Test
    void testJnc_BitClear_Backward()
    {
        test(new int[] { Opcode.JNC.toByte(), -2 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0);
        }, ram -> {}, 0);
    }

    @Test
    void testJz_AccZero_Forward()
    {
        test(new int[] { Opcode.JZ.toByte(), 4 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0);
        }, ram -> {}, 6);
    }

    @Test
    void testJz_AccZero_Backward()
    {
        test(new int[] { Opcode.JZ.toByte(), -2 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0);
        }, ram -> {}, 0);
    }

    @Test
    void testJz_AccNonZero()
    {
        test(new int[] { Opcode.JZ.toByte(), 4 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00000001);
        }, ram -> {}, 2);
    }

    @Test
    void testJnz_AccZero()
    {
        test(new int[] { Opcode.JNZ.toByte(), 4 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0);
        }, ram -> {}, 2);
    }

    @Test
    void testJnz_AccNonZero_Forward()
    {
        test(new int[] { Opcode.JNZ.toByte(), 4 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00000001);
        }, ram -> {}, 6);
    }

    @Test
    void testJnz_AccNonZero_Backward()
    {
        test(new int[] { Opcode.JNZ.toByte(), -2 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00000001);
        }, ram -> {}, 0);
    }

    @Test
    void testCjneAccImm_Lower_Forward()
    {
        test(new int[] { Opcode.CJNE_ACC_IMM.toByte(), 4, 8 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 2);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, 11);
    }

    @Test
    void testCjneAccImm_Lower_Backward()
    {
        test(new int[] { Opcode.CJNE_ACC_IMM.toByte(), 4, -3 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 2);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, 0);
    }

    @Test
    void testCjneAccImm_Higher_Forward()
    {
        test(new int[] { Opcode.CJNE_ACC_IMM.toByte(), 4, 8 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 6);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0);
        }, 11);
    }

    @Test
    void testCjneAccImm_Higher_Backward()
    {
        test(new int[] { Opcode.CJNE_ACC_IMM.toByte(), 4, -3 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 6);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0);
        }, 0);
    }

    @Test
    void testCjneAccImm_Equal()
    {
        test(new int[] { Opcode.CJNE_ACC_IMM.toByte(), 4, 8 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 4);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0);
        }, 3);
    }

    @Test
    void testCjneAccMem_Lower_Forward()
    {
        test(new int[] { Opcode.CJNE_ACC_MEM.toByte(), 0, 8 }, 0, ram -> {
            ram.setRam(0, 4);
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 2);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, 11);
    }

    @Test
    void testCjneAccMem_Lower_Backward()
    {
        test(new int[] { Opcode.CJNE_ACC_MEM.toByte(), 0, -3 }, 0, ram -> {
            ram.setRam(0, 4);
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 2);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, 0);
    }

    @Test
    void testCjneAccMem_Higher_Forward()
    {
        test(new int[] { Opcode.CJNE_ACC_MEM.toByte(), 0, 8 }, 0, ram -> {
            ram.setRam(0, 4);
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 6);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0);
        }, 11);
    }

    @Test
    void testCjneAccMem_Higher_Backward()
    {
        test(new int[] { Opcode.CJNE_ACC_MEM.toByte(), 0, -3 }, 0, ram -> {
            ram.setRam(0, 4);
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 6);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0);
        }, 0);
    }

    @Test
    void testCjneAccMem_Equal()
    {
        test(new int[] { Opcode.CJNE_ACC_MEM.toByte(), 0, 8 }, 0, ram -> {
            ram.setRam(0, 4);
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 4);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0);
        }, 3);
    }

    @Test
    void testCjneIrnImm_Lower_Forward()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { CJNE_IRN_IMM[i].toByte(), 4, 8 }, 0, ram -> {
                ram.setRam(3, 2);
                ram.setRam(reg, 3);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
            }, 11);
        }
    }

    @Test
    void testCjneIrnImm_Lower_Backward()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { CJNE_IRN_IMM[i].toByte(), 4, -3 }, 0, ram -> {
                ram.setRam(3, 2);
                ram.setRam(reg, 3);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
            }, 0);
        }
    }

    @Test
    void testCjneIrnImm_Higher_Forward()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { CJNE_IRN_IMM[i].toByte(), 4, 8 }, 0, ram -> {
                ram.setRam(3, 6);
                ram.setRam(reg, 3);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0);
            }, 11);
        }
    }

    @Test
    void testCjneIrnImm_Higher_Backward()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { CJNE_IRN_IMM[i].toByte(), 4, -3 }, 0, ram -> {
                ram.setRam(3, 6);
                ram.setRam(reg, 3);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0);
            }, 0);
        }
    }

    @Test
    void testCjneIrnImm_Equal()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { CJNE_IRN_IMM[i].toByte(), 4, 8 }, 0, ram -> {
                ram.setRam(3, 4);
                ram.setRam(reg, 3);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0);
            }, 3);
        }
    }

    @Test
    void testCjneDrnImm_Lower_Forward()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { CJNE_DRN_IMM[i].toByte(), 4, 8 }, 0, ram -> {
                ram.setRam(reg, 2);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
            }, 11);
        }
    }

    @Test
    void testCjneDrnImm_Lower_Backward()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { CJNE_DRN_IMM[i].toByte(), 4, -3 }, 0, ram -> {
                ram.setRam(reg, 2);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
            }, 0);
        }
    }

    @Test
    void testCjneDrnImm_Higher_Forward()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { CJNE_DRN_IMM[i].toByte(), 4, 8 }, 0, ram -> {
                ram.setRam(reg, 6);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0);
            }, 11);
        }
    }

    @Test
    void testCjneDrnImm_Higher_Backward()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { CJNE_DRN_IMM[i].toByte(), 4, -3 }, 0, ram -> {
                ram.setRam(reg, 6);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0);
            }, 0);
        }
    }

    @Test
    void testCjneDrnImm_Equal()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { CJNE_DRN_IMM[i].toByte(), 4, 8 }, 0, ram -> {
                ram.setRam(reg, 4);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0);
            }, 3);
        }
    }

    @Test
    void testDjnzMem_NonZero_Forward()
    {
        test(new int[] { Opcode.DJNZ_MEM.toByte(), 10, 8 }, 0, ram -> {
            ram.setRam(10, 2);
        }, ram -> {
            ram.setRam(10, 1);
        }, 11);
    }

    @Test
    void testDjnzMem_NonZero_Backward()
    {
        test(new int[] { Opcode.DJNZ_MEM.toByte(), 10, -3 }, 0, ram -> {
            ram.setRam(10, 2);
        }, ram -> {
            ram.setRam(10, 1);
        }, 0);
    }

    @Test
    void testDjnzMem_Zero()
    {
        test(new int[] { Opcode.DJNZ_MEM.toByte(), 10, 8 }, 0, ram -> {
            ram.setRam(10, 1);
        }, ram -> {
            ram.setRam(10, 0);
        }, 3);
    }

    @Test
    void testDjnzDrn_NonZero_Forward()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { DJNZ_DRN[i].toByte(), 8 }, 0, ram -> {
                ram.setRam(reg, 2);
            }, ram -> {
                ram.setRam(reg, 1);
            }, 10);
        }
    }

    @Test
    void testDjnzDrn_NonZero_Backward()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { DJNZ_DRN[i].toByte(), -2 }, 0, ram -> {
                ram.setRam(reg, 2);
            }, ram -> {
                ram.setRam(reg, 1);
            }, 0);
        }
    }

    @Test
    void testDjnzDrn_Zero()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { DJNZ_DRN[i].toByte(), 8 }, 0, ram -> {
                ram.setRam(reg, 1);
            }, ram -> {
                ram.setRam(reg, 0);
            }, 2);
        }
    }

    @Test
    void testPush()
    {
        test(new int[] { Opcode.PUSH.toByte(), 2 }, 0, ram -> {
            ram.setRam(2, 10);
        }, ram -> {
            ram.setRam(8, 10);
            ram.setSfr(Constants.ADDRESS_STACK_POINTER, 8);
        }, 2);
    }

    @Test
    void testPop()
    {
        test(new int[] { Opcode.POP.toByte(), 2 }, 0, ram -> {
            ram.setRam(8, 10);
            ram.setSfr(Constants.ADDRESS_STACK_POINTER, 8);
        }, ram -> {
            ram.setRam(2, 10);
            ram.setSfr(Constants.ADDRESS_STACK_POINTER, 7);
        }, 2);
    }

    @Test
    void testRr_NoRollover()
    {
        test(new int[] { Opcode.RR.toByte() }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00000010);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00000001);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
        }, 1);
    }

    @Test
    void testRr_Rollover()
    {
        test(new int[] { Opcode.RR.toByte() }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00000001);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b10000000);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
        }, 1);
    }

    @Test
    void testRrc_CarryZeroToZero()
    {
        test(new int[] { Opcode.RRC.toByte() }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00000010);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00000001);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
        }, 1);
    }

    @Test
    void testRrc_CarryZeroToOne()
    {
        test(new int[] { Opcode.RRC.toByte() }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00000001);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, 1);
    }

    @Test
    void testRrc_CarryOneToOne()
    {
        test(new int[] { Opcode.RRC.toByte() }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00000001);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b10000000);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000001);
        }, 1);
    }

    @Test
    void testRrc_CarryOneToZero()
    {
        test(new int[] { Opcode.RRC.toByte() }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b10000000);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
        }, 1);
    }

    @Test
    void testRl_NoRollover()
    {
        test(new int[] { Opcode.RL.toByte() }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b01000000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b10000000);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
        }, 1);
    }

    @Test
    void testRl_Rollover()
    {
        test(new int[] { Opcode.RL.toByte() }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b10000000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00000001);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
        }, 1);
    }

    @Test
    void testRlc_CarryZeroToZero()
    {
        test(new int[] { Opcode.RLC.toByte() }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b01000000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b10000000);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
        }, 1);
    }

    @Test
    void testRlc_CarryZeroToOne()
    {
        test(new int[] { Opcode.RLC.toByte() }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b10000000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, 1);
    }

    @Test
    void testRlc_CarryOneToOne()
    {
        test(new int[] { Opcode.RLC.toByte() }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b10000000);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00000001);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000001);
        }, 1);
    }

    @Test
    void testRlc_CarryOneToZero()
    {
        test(new int[] { Opcode.RLC.toByte() }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00000001);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
        }, 1);
    }

    @Test
    void testIncAcc()
    {
        test(new int[] { Opcode.INC_ACC.toByte() }, 0, ram -> {}, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 1);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
        }, 1);
    }

    @Test
    void testIncMem()
    {
        test(new int[] { Opcode.INC_MEM.toByte(), 10 }, 0, ram -> {}, ram -> {
            ram.setRam(10, 1);
        }, 2);
    }

    @Test
    void testIncIrn()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { INC_IRN[i].toByte() }, 0, ram -> {
                ram.setRam(reg, 10);
            }, ram -> {
                ram.setRam(10, 1);
            }, 1);
        }
    }

    @Test
    void testIncDrn()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { INC_DRN[i].toByte() }, 0, ram -> {}, ram -> {
                ram.setRam(reg, 1);
            }, 1);
        }
    }

    @Test
    void testIncDptr_LsbOnly()
    {
        test(new int[] { Opcode.INC_DPTR.toByte() }, 0, ram -> {}, ram -> {
            ram.setSfr(Constants.ADDRESS_DATA_POINTER_LOWER, 1);
        }, 1);
    }

    @Test
    void testIncDptr_Both()
    {
        test(new int[] { Opcode.INC_DPTR.toByte() }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_DATA_POINTER_LOWER, 255);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_DATA_POINTER_LOWER, 0);
            ram.setSfr(Constants.ADDRESS_DATA_POINTER_UPPER, 1);
        }, 1);
    }

    @Test
    void testDecAcc()
    {
        test(new int[] { Opcode.DEC_ACC.toByte() }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 1);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0);
        }, 1);
    }

    @Test
    void testDecMem()
    {
        test(new int[] { Opcode.DEC_MEM.toByte(), 10 }, 0, ram -> {
            ram.setRam(10, 1);
        }, ram -> {
            ram.setRam(10, 0);
        }, 2);
    }

    @Test
    void testDecIrn()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { DEC_IRN[i].toByte() }, 0, ram -> {
                ram.setRam(10, 1);
                ram.setRam(reg, 10);
            }, ram -> {
                ram.setRam(10, 0);
            }, 1);
        }
    }

    @Test
    void testDecDrn()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { DEC_DRN[i].toByte() }, 0, ram -> {
                ram.setRam(reg, 1);
            }, ram -> {
                ram.setRam(reg, 0);
            }, 1);
        }
    }

    @Test
    void testAddImm_NoAuxCarry_NoCarry_NoOverflow_LowNibble()
    {
        test(new int[] { Opcode.ADD_IMM.toByte(), 3 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 2);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 5);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
        }, 2);
    }

    @Test
    void testAddImm_NoAuxCarry_NoCarry_NoOverflow_HighNibble()
    {
        test(new int[] { Opcode.ADD_IMM.toByte(), 0x30 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x20);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x50);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
        }, 2);
    }

    @Test
    void testAddImm_AuxCarry_NoCarry_NoOverflow()
    {
        test(new int[] { Opcode.ADD_IMM.toByte(), 7 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 9);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 16);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b01000001);
        }, 2);
    }

    @Test
    void testAddImm_NoAuxCarry_Carry_NoOverflow()
    {
        test(new int[] { Opcode.ADD_IMM.toByte(), 0x90 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x70);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, 2);
    }

    @Test
    void testAddImm_NoAuxCarry_NoCarry_Overflow()
    {
        test(new int[] { Opcode.ADD_IMM.toByte(), 64 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 64);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 128);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000101);
        }, 2);
    }

    @Test
    void testAddMem_NoAuxCarry_NoCarry_NoOverflow_LowNibble()
    {
        test(new int[] { Opcode.ADD_MEM.toByte(), 10 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 2);
            ram.setRam(10, 3);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 5);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
        }, 2);
    }

    @Test
    void testAddMem_NoAuxCarry_NoCarry_NoOverflow_HighNibble()
    {
        test(new int[] { Opcode.ADD_MEM.toByte(), 10 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x20);
            ram.setRam(10, 0x30);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x50);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
        }, 2);
    }

    @Test
    void testAddMem_AuxCarry_NoCarry_NoOverflow()
    {
        test(new int[] { Opcode.ADD_MEM.toByte(), 10 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 9);
            ram.setRam(10, 7);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 16);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b01000001);
        }, 2);
    }

    @Test
    void testAddMem_NoAuxCarry_Carry_NoOverflow()
    {
        test(new int[] { Opcode.ADD_MEM.toByte(), 10 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x70);
            ram.setRam(10, 0x90);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, 2);
    }

    @Test
    void testAddMem_NoAuxCarry_NoCarry_Overflow()
    {
        test(new int[] { Opcode.ADD_MEM.toByte(), 10 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 64);
            ram.setRam(10, 64);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 128);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000101);
        }, 2);
    }

    @Test
    void testAddIrn_NoAuxCarry_NoCarry_NoOverflow_LowNibble()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { ADD_IRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 2);
                ram.setRam(reg, 10);
                ram.setRam(10, 3);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 5);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
            }, 1);
        }
    }

    @Test
    void testAddIrn_NoAuxCarry_NoCarry_NoOverflow_HighNibble()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { ADD_IRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x20);
                ram.setRam(reg, 10);
                ram.setRam(10, 0x30);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x50);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
            }, 1);
        }
    }

    @Test
    void testAddIrn_AuxCarry_NoCarry_NoOverflow()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { ADD_IRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 9);
                ram.setRam(reg, 10);
                ram.setRam(10, 7);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 16);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b01000001);
            }, 1);
        }
    }

    @Test
    void testAddIrn_NoAuxCarry_Carry_NoOverflow()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { ADD_IRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x70);
                ram.setRam(reg, 10);
                ram.setRam(10, 0x90);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
            }, 1);
        }
    }

    @Test
    void testAddIrn_NoAuxCarry_NoCarry_Overflow()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { ADD_IRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 64);
                ram.setRam(reg, 10);
                ram.setRam(10, 64);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 128);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000101);
            }, 1);
        }
    }

    @Test
    void testAddDrn_NoAuxCarry_NoCarry_NoOverflow_LowNibble()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { ADD_DRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 2);
                ram.setRam(reg, 3);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 5);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
            }, 1);
        }
    }

    @Test
    void testAddDrn_NoAuxCarry_NoCarry_NoOverflow_HighNibble()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { ADD_DRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x20);
                ram.setRam(reg, 0x30);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x50);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
            }, 1);
        }
    }

    @Test
    void testAddDrn_AuxCarry_NoCarry_NoOverflow()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { ADD_DRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 9);
                ram.setRam(reg, 7);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 16);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b01000001);
            }, 1);
        }
    }

    @Test
    void testAddDrn_NoAuxCarry_Carry_NoOverflow()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { ADD_DRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x70);
                ram.setRam(reg, 0x90);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
            }, 1);
        }
    }

    @Test
    void testAddDrn_NoAuxCarry_NoCarry_Overflow()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { ADD_DRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 64);
                ram.setRam(reg, 64);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 128);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000101);
            }, 1);
        }
    }

    @Test
    void testAddcImm_NoAuxCarry_NoCarry_NoOverflow_NoIncomingCarry_LowNibble()
    {
        test(new int[] { Opcode.ADDC_IMM.toByte(), 3 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 2);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 5);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
        }, 2);
    }

    @Test
    void testAddcImm_NoAuxCarry_NoCarry_NoOverflow_IncomingCarry_LowNibble()
    {
        test(new int[] { Opcode.ADDC_IMM.toByte(), 3 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 2);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 6);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
        }, 2);
    }

    @Test
    void testAddcImm_NoAuxCarry_NoCarry_NoOverflow_NoIncomingCarry_HighNibble()
    {
        test(new int[] { Opcode.ADDC_IMM.toByte(), 0x30 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x20);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x50);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
        }, 2);
    }

    @Test
    void testAddcImm_NoAuxCarry_NoCarry_NoOverflow_IncomingCarry_HighNibble()
    {
        test(new int[] { Opcode.ADDC_IMM.toByte(), 0x30 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x20);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x51);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
        }, 2);
    }

    @Test
    void testAddcImm_AuxCarry_NoCarry_NoOverflow_NoIncomingCarry()
    {
        test(new int[] { Opcode.ADDC_IMM.toByte(), 7 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 9);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 16);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b01000001);
        }, 2);
    }

    @Test
    void testAddcImm_AuxCarry_NoCarry_NoOverflow_IncomingCarry()
    {
        test(new int[] { Opcode.ADDC_IMM.toByte(), 7 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 9);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 17);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b01000000);
        }, 2);
    }

    @Test
    void testAddcImm_NoAuxCarry_Carry_NoOverflow_NoIncomingCarry()
    {
        test(new int[] { Opcode.ADDC_IMM.toByte(), 0x90 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x70);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, 2);
    }

    @Test
    void testAddcImm_NoAuxCarry_Carry_NoOverflow_IncomingCarry()
    {
        test(new int[] { Opcode.ADDC_IMM.toByte(), 0x90 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x70);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 1);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000001);
        }, 2);
    }

    @Test
    void testAddcImm_NoAuxCarry_NoCarry_Overflow_NoIncomingCarry()
    {
        test(new int[] { Opcode.ADDC_IMM.toByte(), 64 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 64);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 128);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000101);
        }, 2);
    }

    @Test
    void testAddcImm_NoAuxCarry_NoCarry_Overflow_IncomingCarry()
    {
        test(new int[] { Opcode.ADDC_IMM.toByte(), 64 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 64);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 129);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000100);
        }, 2);
    }

    @Test
    void testAddcMem_NoAuxCarry_NoCarry_NoOverflow_NoIncomingCarry_LowNibble()
    {
        test(new int[] { Opcode.ADDC_MEM.toByte(), 10 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 2);
            ram.setRam(10, 3);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 5);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
        }, 2);
    }

    @Test
    void testAddcMem_NoAuxCarry_NoCarry_NoOverflow_IncomingCarry_LowNibble()
    {
        test(new int[] { Opcode.ADDC_MEM.toByte(), 10 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 2);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
            ram.setRam(10, 3);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 6);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
        }, 2);
    }

    @Test
    void testAddcMem_NoAuxCarry_NoCarry_NoOverflow_NoIncomingCarry_HighNibble()
    {
        test(new int[] { Opcode.ADDC_MEM.toByte(), 10 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x20);
            ram.setRam(10, 0x30);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x50);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
        }, 2);
    }

    @Test
    void testAddcMem_NoAuxCarry_NoCarry_NoOverflow_IncomingCarry_HighNibble()
    {
        test(new int[] { Opcode.ADDC_MEM.toByte(), 10 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x20);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
            ram.setRam(10, 0x30);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x51);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
        }, 2);
    }

    @Test
    void testAddcMem_AuxCarry_NoCarry_NoOverflow_NoIncomingCarry()
    {
        test(new int[] { Opcode.ADDC_MEM.toByte(), 10 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 9);
            ram.setRam(10, 7);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 16);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b01000001);
        }, 2);
    }

    @Test
    void testAddcMem_AuxCarry_NoCarry_NoOverflow_IncomingCarry()
    {
        test(new int[] { Opcode.ADDC_MEM.toByte(), 10 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 9);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
            ram.setRam(10, 7);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 17);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b01000000);
        }, 2);
    }

    @Test
    void testAddcMem_NoAuxCarry_Carry_NoOverflow_NoIncomingCarry()
    {
        test(new int[] { Opcode.ADDC_MEM.toByte(), 10 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x70);
            ram.setRam(10, 0x90);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, 2);
    }

    @Test
    void testAddcMem_NoAuxCarry_Carry_NoOverflow_IncomingCarry()
    {
        test(new int[] { Opcode.ADDC_MEM.toByte(), 10 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x70);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
            ram.setRam(10, 0x90);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 1);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000001);
        }, 2);
    }

    @Test
    void testAddcMem_NoAuxCarry_NoCarry_Overflow_NoIncomingCarry()
    {
        test(new int[] { Opcode.ADDC_MEM.toByte(), 10 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 64);
            ram.setRam(10, 64);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 128);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000101);
        }, 2);
    }

    @Test
    void testAddcMem_NoAuxCarry_NoCarry_Overflow_IncomingCarry()
    {
        test(new int[] { Opcode.ADDC_MEM.toByte(), 10 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 64);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
            ram.setRam(10, 64);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 129);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000100);
        }, 2);
    }

    @Test
    void testAddcIrn_NoAuxCarry_NoCarry_NoOverflow_NoIncomingCarry_LowNibble()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { ADDC_IRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 2);
                ram.setRam(reg, 10);
                ram.setRam(10, 3);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 5);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
            }, 1);
        }
    }

    @Test
    void testAddcIrn_NoAuxCarry_NoCarry_NoOverflow_IncomingCarry_LowNibble()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { ADDC_IRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 2);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
                ram.setRam(reg, 10);
                ram.setRam(10, 3);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 6);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
            }, 1);
        }
    }

    @Test
    void testAddcIrn_NoAuxCarry_NoCarry_NoOverflow_NoIncomingCarry_HighNibble()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { ADDC_IRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x20);
                ram.setRam(reg, 10);
                ram.setRam(10, 0x30);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x50);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
            }, 1);
        }
    }

    @Test
    void testAddcIrn_NoAuxCarry_NoCarry_NoOverflow_IncomingCarry_HighNibble()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { ADDC_IRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x20);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
                ram.setRam(reg, 10);
                ram.setRam(10, 0x30);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x51);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
            }, 1);
        }
    }

    @Test
    void testAddcIrn_AuxCarry_NoCarry_NoOverflow_NoIncomingCarry()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { ADDC_IRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 9);
                ram.setRam(reg, 10);
                ram.setRam(10, 7);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 16);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b01000001);
            }, 1);
        }
    }

    @Test
    void testAddcIrn_AuxCarry_NoCarry_NoOverflow_IncomingCarry()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { ADDC_IRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 9);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
                ram.setRam(reg, 10);
                ram.setRam(10, 7);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 17);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b01000000);
            }, 1);
        }
    }

    @Test
    void testAddcIrn_NoAuxCarry_Carry_NoOverflow_NoIncomingCarry()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { ADDC_IRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x70);
                ram.setRam(reg, 10);
                ram.setRam(10, 0x90);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
            }, 1);
        }
    }

    @Test
    void testAddcIrn_NoAuxCarry_Carry_NoOverflow_IncomingCarry()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { ADDC_IRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x70);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
                ram.setRam(reg, 10);
                ram.setRam(10, 0x90);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 1);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000001);
            }, 1);
        }
    }

    @Test
    void testAddcIrn_NoAuxCarry_NoCarry_Overflow_NoIncomingCarry()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { ADDC_IRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 64);
                ram.setRam(reg, 10);
                ram.setRam(10, 64);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 128);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000101);
            }, 1);
        }
    }

    @Test
    void testAddcIrn_NoAuxCarry_NoCarry_Overflow_IncomingCarry()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { ADDC_IRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 64);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
                ram.setRam(reg, 10);
                ram.setRam(10, 64);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 129);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000100);
            }, 1);
        }
    }

    @Test
    void testAddcDrn_NoAuxCarry_NoCarry_NoOverflow_NoIncomingCarry_LowNibble()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { ADDC_DRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 2);
                ram.setRam(reg, 3);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 5);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
            }, 1);
        }
    }

    @Test
    void testAddcDrn_NoAuxCarry_NoCarry_NoOverflow_IncomingCarry_LowNibble()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { ADDC_DRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 2);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
                ram.setRam(reg, 3);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 6);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
            }, 1);
        }
    }

    @Test
    void testAddcDrn_NoAuxCarry_NoCarry_NoOverflow_NoIncomingCarry_HighNibble()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { ADDC_DRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x20);
                ram.setRam(reg, 0x30);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x50);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
            }, 1);
        }
    }

    @Test
    void testAddcDrn_NoAuxCarry_NoCarry_NoOverflow_IncomingCarry_HighNibble()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { ADDC_DRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x20);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
                ram.setRam(reg, 0x30);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x51);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
            }, 1);
        }
    }

    @Test
    void testAddcDrn_AuxCarry_NoCarry_NoOverflow_NoIncomingCarry()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { ADDC_DRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 9);
                ram.setRam(reg, 7);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 16);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b01000001);
            }, 1);
        }
    }

    @Test
    void testAddcDrn_AuxCarry_NoCarry_NoOverflow_IncomingCarry()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { ADDC_DRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 9);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
                ram.setRam(reg, 7);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 17);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b01000000);
            }, 1);
        }
    }

    @Test
    void testAddcDrn_NoAuxCarry_Carry_NoOverflow_NoIncomingCarry()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { ADDC_DRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x70);
                ram.setRam(reg, 0x90);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
            }, 1);
        }
    }

    @Test
    void testAddcDrn_NoAuxCarry_Carry_NoOverflow_IncomingCarry()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { ADDC_DRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x70);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
                ram.setRam(reg, 0x90);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 1);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000001);
            }, 1);
        }
    }

    @Test
    void testAddcDrn_NoAuxCarry_NoCarry_Overflow_NoIncomingCarry()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { ADDC_DRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 64);
                ram.setRam(reg, 64);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 128);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000101);
            }, 1);
        }
    }

    @Test
    void testAddcDrn_NoAuxCarry_NoCarry_Overflow_IncomingCarry()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { ADDC_DRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 64);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
                ram.setRam(reg, 64);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 129);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000100);
            }, 1);
        }
    }

    @Test
    void testOrlMemAcc()
    {
        test(new int[] { Opcode.ORL_MEM_ACC.toByte(), 10 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00001010);
            ram.setRam(10, 0b00001100);
        }, ram -> {
            ram.setRam(10, 0b00001110);
        }, 2);
    }

    @Test
    void testOrlMemImm()
    {
        test(new int[] { Opcode.ORL_MEM_IMM.toByte(), 10, 0b00001010 }, 0, ram -> {
            ram.setRam(10, 0b00001100);
        }, ram -> {
            ram.setRam(10, 0b00001110);
        }, 3);
    }

    @Test
    void testOrlAccImm()
    {
        test(new int[] { Opcode.ORL_IMM.toByte(), 0b00001010 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00001100);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00001110);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
        }, 2);
    }

    @Test
    void testOrlAccMem()
    {
        test(new int[] { Opcode.ORL_MEM.toByte(), 10 }, 0, ram -> {
            ram.setRam(10, 0b00001010);
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00001100);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00001110);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
        }, 2);
    }

    @Test
    void testOrlAccIrn()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { ORL_IRN[i].toByte() }, 0, ram -> {
                ram.setRam(reg, 10);
                ram.setRam(10, 0b00001010);
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00001100);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00001110);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
            }, 1);
        }
    }

    @Test
    void testOrlAccDrn()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { ORL_DRN[i].toByte() }, 0, ram -> {
                ram.setRam(reg, 0b00001010);
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00001100);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00001110);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
            }, 1);
        }
    }

    @Test
    void testOrlCBit_Neither()
    {
        test(new int[] { Opcode.ORL_C_BIT.toByte(), 0x04 /* Byte 20h, Bit 4 */ }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
            ram.setRam(0x20, 0b00000000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
        }, 2);
    }

    @Test
    void testOrlCBit_Carry()
    {
        test(new int[] { Opcode.ORL_C_BIT.toByte(), 0x04 /* Byte 20h, Bit 4 */ }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
            ram.setRam(0x20, 0b00000000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, 2);
    }

    @Test
    void testOrlCBit_MemBit()
    {
        test(new int[] { Opcode.ORL_C_BIT.toByte(), 0x04 /* Byte 20h, Bit 4 */ }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
            ram.setRam(0x20, 0b00010000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, 2);
    }

    @Test
    void testOrlCBit_Both()
    {
        test(new int[] { Opcode.ORL_C_BIT.toByte(), 0x04 /* Byte 20h, Bit 4 */ }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
            ram.setRam(0x20, 0b00010000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, 2);
    }

    @Test
    void testOrlCNbit_Neither()
    {
        test(new int[] { Opcode.ORL_C_NBIT.toByte(), 0x04 /* Byte 20h, Bit 4 */ }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
            ram.setRam(0x20, 0b00000000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, 2);
    }

    @Test
    void testOrlCNbit_Carry()
    {
        test(new int[] { Opcode.ORL_C_NBIT.toByte(), 0x04 /* Byte 20h, Bit 4 */ }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
            ram.setRam(0x20, 0b00000000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, 2);
    }

    @Test
    void testOrlCNbit_MemBit()
    {
        test(new int[] { Opcode.ORL_C_NBIT.toByte(), 0x04 /* Byte 20h, Bit 4 */ }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
            ram.setRam(0x20, 0b00010000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
        }, 2);
    }

    @Test
    void testOrlCNbit_Both()
    {
        test(new int[] { Opcode.ORL_C_NBIT.toByte(), 0x04 /* Byte 20h, Bit 4 */ }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
            ram.setRam(0x20, 0b00010000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, 2);
    }

    @Test
    void testAnlMemAcc()
    {
        test(new int[] { Opcode.ANL_MEM_ACC.toByte(), 10 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00001010);
            ram.setRam(10, 0b00001100);
        }, ram -> {
            ram.setRam(10, 0b00001000);
        }, 2);
    }

    @Test
    void testAnlMemImm()
    {
        test(new int[] { Opcode.ANL_MEM_IMM.toByte(), 10, 0b00001010 }, 0, ram -> {
            ram.setRam(10, 0b00001100);
        }, ram -> {
            ram.setRam(10, 0b00001000);
        }, 3);
    }

    @Test
    void testAnlAccImm()
    {
        test(new int[] { Opcode.ANL_IMM.toByte(), 0b00001010 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00001100);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00001000);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
        }, 2);
    }

    @Test
    void testAnlAccMem()
    {
        test(new int[] { Opcode.ANL_MEM.toByte(), 10 }, 0, ram -> {
            ram.setRam(10, 0b00001010);
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00001100);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00001000);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
        }, 2);
    }

    @Test
    void testAnlAccIrn()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { ANL_IRN[i].toByte() }, 0, ram -> {
                ram.setRam(reg, 10);
                ram.setRam(10, 0b00001010);
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00001100);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00001000);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
            }, 1);
        }
    }

    @Test
    void testAnlAccDrn()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { ANL_DRN[i].toByte() }, 0, ram -> {
                ram.setRam(reg, 0b00001010);
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00001100);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00001000);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
            }, 1);
        }
    }

    @Test
    void testAnlCBit_Neither()
    {
        test(new int[] { Opcode.ANL_C_BIT.toByte(), 0x04 /* Byte 20h, Bit 4 */ }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
            ram.setRam(0x20, 0b00000000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
        }, 2);
    }

    @Test
    void testAnlCBit_Carry()
    {
        test(new int[] { Opcode.ANL_C_BIT.toByte(), 0x04 /* Byte 20h, Bit 4 */ }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
            ram.setRam(0x20, 0b00000000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
        }, 2);
    }

    @Test
    void testAnlCBit_MemBit()
    {
        test(new int[] { Opcode.ANL_C_BIT.toByte(), 0x04 /* Byte 20h, Bit 4 */ }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
            ram.setRam(0x20, 0b00010000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
        }, 2);
    }

    @Test
    void testAnlCBit_Both()
    {
        test(new int[] { Opcode.ANL_C_BIT.toByte(), 0x04 /* Byte 20h, Bit 4 */ }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
            ram.setRam(0x20, 0b00010000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, 2);
    }

    @Test
    void testAnlCNbit_Neither()
    {
        test(new int[] { Opcode.ANL_C_NBIT.toByte(), 0x04 /* Byte 20h, Bit 4 */ }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
            ram.setRam(0x20, 0b00000000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
        }, 2);
    }

    @Test
    void testAnlCNbit_Carry()
    {
        test(new int[] { Opcode.ANL_C_NBIT.toByte(), 0x04 /* Byte 20h, Bit 4 */ }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
            ram.setRam(0x20, 0b00000000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, 2);
    }

    @Test
    void testAnlCNbit_MemBit()
    {
        test(new int[] { Opcode.ANL_C_NBIT.toByte(), 0x04 /* Byte 20h, Bit 4 */ }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
            ram.setRam(0x20, 0b00010000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
        }, 2);
    }

    @Test
    void testAnlCNbit_Both()
    {
        test(new int[] { Opcode.ANL_C_NBIT.toByte(), 0x04 /* Byte 20h, Bit 4 */ }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
            ram.setRam(0x20, 0b00010000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
        }, 2);
    }

    @Test
    void testXrlMemAcc()
    {
        test(new int[] { Opcode.XRL_MEM_ACC.toByte(), 10 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00001010);
            ram.setRam(10, 0b00001100);
        }, ram -> {
            ram.setRam(10, 0b00000110);
        }, 2);
    }

    @Test
    void testXrlMemImm()
    {
        test(new int[] { Opcode.XRL_MEM_IMM.toByte(), 10, 0b00001010 }, 0, ram -> {
            ram.setRam(10, 0b00001100);
        }, ram -> {
            ram.setRam(10, 0b00000110);
        }, 3);
    }

    @Test
    void testXrlAccImm()
    {
        test(new int[] { Opcode.XRL_IMM.toByte(), 0b00001010 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00001100);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00000110);
        }, 2);
    }

    @Test
    void testXrlAccMem()
    {
        test(new int[] { Opcode.XRL_MEM.toByte(), 10 }, 0, ram -> {
            ram.setRam(10, 0b00001010);
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00001100);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00000110);
        }, 2);
    }

    @Test
    void testXrlAccIrn()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { XRL_IRN[i].toByte() }, 0, ram -> {
                ram.setRam(reg, 10);
                ram.setRam(10, 0b00001010);
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00001100);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00000110);
            }, 1);
        }
    }

    @Test
    void testXrlAccDrn()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { XRL_DRN[i].toByte() }, 0, ram -> {
                ram.setRam(reg, 0b00001010);
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00001100);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00000110);
            }, 1);
        }
    }

    @Test
    void testSubbImm_NoAuxCarry_NoCarry_NoOverflow_NoIncomingCarry_LowNibble()
    {
        test(new int[] { Opcode.SUBB_IMM.toByte(), 2 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 3);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 1);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
        }, 2);
    }

    @Test
    void testSubbImm_NoAuxCarry_NoCarry_NoOverflow_IncomingCarry_LowNibble()
    {
        test(new int[] { Opcode.SUBB_IMM.toByte(), 2 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 3);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
        }, 2);
    }

    @Test
    void testSubbImm_NoAuxCarry_NoCarry_NoOverflow_NoIncomingCarry_HighNibble()
    {
        test(new int[] { Opcode.SUBB_IMM.toByte(), 0x20 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x30);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x10);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
        }, 2);
    }

    @Test
    void testSubbImm_NoAuxCarry_NoCarry_NoOverflow_IncomingCarry_HighNibble()
    {
        test(new int[] { Opcode.SUBB_IMM.toByte(), 0x20 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x31);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x10);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
        }, 2);
    }

    @Test
    void testSubbImm_AuxCarry_NoCarry_NoOverflow_NoIncomingCarry()
    {
        test(new int[] { Opcode.SUBB_IMM.toByte(), 7 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 16);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 9);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b01000000);
        }, 2);
    }

    @Test
    void testSubbImm_AuxCarry_NoCarry_NoOverflow_IncomingCarry()
    {
        test(new int[] { Opcode.SUBB_IMM.toByte(), 7 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 16);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 8);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b01000001);
        }, 2);
    }

    @Test
    void testSubbImm_NoAuxCarry_Carry_NoOverflow_NoIncomingCarry()
    {
        test(new int[] { Opcode.SUBB_IMM.toByte(), 0x70 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x90);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, 2);
    }

    @Test
    void testSubbImm_NoAuxCarry_Carry_NoOverflow_IncomingCarry()
    {
        test(new int[] { Opcode.SUBB_IMM.toByte(), 0x70 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 1);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x90);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, 2);
    }

    @Test
    void testSubbImm_NoAuxCarry_NoCarry_Overflow_NoIncomingCarry()
    {
        test(new int[] { Opcode.SUBB_IMM.toByte(), 64 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 128);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 64);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000101);
        }, 2);
    }

    @Test
    void testSubbImm_NoAuxCarry_NoCarry_Overflow_IncomingCarry()
    {
        test(new int[] { Opcode.SUBB_IMM.toByte(), 64 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 129);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 64);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000101);
        }, 2);
    }

    @Test
    void testSubbMem_NoAuxCarry_NoCarry_NoOverflow_NoIncomingCarry_LowNibble()
    {
        test(new int[] { Opcode.SUBB_MEM.toByte(), 10 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 3);
            ram.setRam(10, 2);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 1);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
        }, 2);
    }

    @Test
    void testSubbMem_NoAuxCarry_NoCarry_NoOverflow_IncomingCarry_LowNibble()
    {
        test(new int[] { Opcode.SUBB_MEM.toByte(), 10 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 3);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
            ram.setRam(10, 2);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
        }, 2);
    }

    @Test
    void testSubbMem_NoAuxCarry_NoCarry_NoOverflow_NoIncomingCarry_HighNibble()
    {
        test(new int[] { Opcode.SUBB_MEM.toByte(), 10 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x30);
            ram.setRam(10, 0x20);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x10);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
        }, 2);
    }

    @Test
    void testSubbMem_NoAuxCarry_NoCarry_NoOverflow_IncomingCarry_HighNibble()
    {
        test(new int[] { Opcode.SUBB_MEM.toByte(), 10 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x31);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
            ram.setRam(10, 0x20);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x10);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
        }, 2);
    }

    @Test
    void testSubbMem_AuxCarry_NoCarry_NoOverflow_NoIncomingCarry()
    {
        test(new int[] { Opcode.SUBB_MEM.toByte(), 10 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 16);
            ram.setRam(10, 7);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 9);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b01000000);
        }, 2);
    }

    @Test
    void testSubbMem_AuxCarry_NoCarry_NoOverflow_IncomingCarry()
    {
        test(new int[] { Opcode.SUBB_MEM.toByte(), 10 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 16);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
            ram.setRam(10, 7);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 8);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b01000001);
        }, 2);
    }

    @Test
    void testSubbMem_NoAuxCarry_Carry_NoOverflow_NoIncomingCarry()
    {
        test(new int[] { Opcode.SUBB_MEM.toByte(), 10 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0);
            ram.setRam(10, 0x70);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x90);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, 2);
    }

    @Test
    void testSubbMem_NoAuxCarry_Carry_NoOverflow_IncomingCarry()
    {
        test(new int[] { Opcode.SUBB_MEM.toByte(), 10 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 1);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
            ram.setRam(10, 0x70);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x90);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, 2);
    }

    @Test
    void testSubbMem_NoAuxCarry_NoCarry_Overflow_NoIncomingCarry()
    {
        test(new int[] { Opcode.SUBB_MEM.toByte(), 10 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 128);
            ram.setRam(10, 64);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 64);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000101);
        }, 2);
    }

    @Test
    void testSubbMem_NoAuxCarry_NoCarry_Overflow_IncomingCarry()
    {
        test(new int[] { Opcode.SUBB_MEM.toByte(), 10 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 129);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
            ram.setRam(10, 64);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 64);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000101);
        }, 2);
    }

    @Test
    void testSubbIrn_NoAuxCarry_NoCarry_NoOverflow_NoIncomingCarry_LowNibble()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { SUBB_IRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 3);
                ram.setRam(reg, 10);
                ram.setRam(10, 2);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 1);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
            }, 1);
        }
    }

    @Test
    void testSubbIrn_NoAuxCarry_NoCarry_NoOverflow_IncomingCarry_LowNibble()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { SUBB_IRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 3);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
                ram.setRam(reg, 10);
                ram.setRam(10, 2);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
            }, 1);
        }
    }

    @Test
    void testSubbIrn_NoAuxCarry_NoCarry_NoOverflow_NoIncomingCarry_HighNibble()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { SUBB_IRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x30);
                ram.setRam(reg, 10);
                ram.setRam(10, 0x20);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x10);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
            }, 1);
        }
    }

    @Test
    void testSubbIrn_NoAuxCarry_NoCarry_NoOverflow_IncomingCarry_HighNibble()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { SUBB_IRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x31);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
                ram.setRam(reg, 10);
                ram.setRam(10, 0x20);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x10);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
            }, 1);
        }
    }

    @Test
    void testSubbIrn_AuxCarry_NoCarry_NoOverflow_NoIncomingCarry()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { SUBB_IRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 16);
                ram.setRam(reg, 10);
                ram.setRam(10, 7);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 9);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b01000000);
            }, 1);
        }
    }

    @Test
    void testSubbIrn_AuxCarry_NoCarry_NoOverflow_IncomingCarry()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { SUBB_IRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 16);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
                ram.setRam(reg, 10);
                ram.setRam(10, 7);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 8);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b01000001);
            }, 1);
        }
    }

    @Test
    void testSubbIrn_NoAuxCarry_Carry_NoOverflow_NoIncomingCarry()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { SUBB_IRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0);
                ram.setRam(reg, 10);
                ram.setRam(10, 0x70);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x90);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
            }, 1);
        }
    }

    @Test
    void testSubbIrn_NoAuxCarry_Carry_NoOverflow_IncomingCarry()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { SUBB_IRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 1);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
                ram.setRam(reg, 10);
                ram.setRam(10, 0x70);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x90);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
            }, 1);
        }
    }

    @Test
    void testSubbIrn_NoAuxCarry_NoCarry_Overflow_NoIncomingCarry()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { SUBB_IRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 128);
                ram.setRam(reg, 10);
                ram.setRam(10, 64);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 64);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000101);
            }, 1);
        }
    }

    @Test
    void testSubbIrn_NoAuxCarry_NoCarry_Overflow_IncomingCarry()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { SUBB_IRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 129);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
                ram.setRam(reg, 10);
                ram.setRam(10, 64);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 64);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000101);
            }, 1);
        }
    }

    @Test
    void testSubbDrn_NoAuxCarry_NoCarry_NoOverflow_NoIncomingCarry_LowNibble()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { SUBB_DRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 3);
                ram.setRam(reg, 2);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 1);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
            }, 1);
        }
    }

    @Test
    void testSubbDrn_NoAuxCarry_NoCarry_NoOverflow_IncomingCarry_LowNibble()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { SUBB_DRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 3);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
                ram.setRam(reg, 2);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000000);
            }, 1);
        }
    }

    @Test
    void testSubbDrn_NoAuxCarry_NoCarry_NoOverflow_NoIncomingCarry_HighNibble()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { SUBB_DRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x30);
                ram.setRam(reg, 0x20);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x10);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
            }, 1);
        }
    }

    @Test
    void testSubbDrn_NoAuxCarry_NoCarry_NoOverflow_IncomingCarry_HighNibble()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { SUBB_DRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x31);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
                ram.setRam(reg, 0x20);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x10);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
            }, 1);
        }
    }

    @Test
    void testSubbDrn_AuxCarry_NoCarry_NoOverflow_NoIncomingCarry()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { SUBB_DRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 16);
                ram.setRam(reg, 7);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 9);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b01000000);
            }, 1);
        }
    }

    @Test
    void testSubbDrn_AuxCarry_NoCarry_NoOverflow_IncomingCarry()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { SUBB_DRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 16);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
                ram.setRam(reg, 7);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 8);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b01000001);
            }, 1);
        }
    }

    @Test
    void testSubbDrn_NoAuxCarry_Carry_NoOverflow_NoIncomingCarry()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { SUBB_DRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0);
                ram.setRam(reg, 0x70);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x90);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
            }, 1);
        }
    }

    @Test
    void testSubbDrn_NoAuxCarry_Carry_NoOverflow_IncomingCarry()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { SUBB_DRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 1);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
                ram.setRam(reg, 0x70);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x90);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
            }, 1);
        }
    }

    @Test
    void testSubbDrn_NoAuxCarry_NoCarry_Overflow_NoIncomingCarry()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { SUBB_DRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 128);
                ram.setRam(reg, 64);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 64);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000101);
            }, 1);
        }
    }

    @Test
    void testSubbDrn_NoAuxCarry_NoCarry_Overflow_IncomingCarry()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { SUBB_DRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 129);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
                ram.setRam(reg, 64);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 64);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000101);
            }, 1);
        }
    }

    @Test
    void testMul_NoOverflow()
    {
        test(new int[] { Opcode.MUL_AB.toByte() }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 10);
            ram.setSfr(Constants.ADDRESS_REGISTER_B, 12);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 120);
            ram.setSfr(Constants.ADDRESS_REGISTER_B, 0);
        }, 1);
    }

    @Test
    void testMul_Overflow()
    {
        test(new int[] { Opcode.MUL_AB.toByte() }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 25);
            ram.setSfr(Constants.ADDRESS_REGISTER_B, 12);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 44);
            ram.setSfr(Constants.ADDRESS_REGISTER_B, 1);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000101);
        }, 1);
    }

    @Test
    void testDiv_NoDivByZero()
    {
        test(new int[] { Opcode.DIV_AB.toByte() }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 10);
            ram.setSfr(Constants.ADDRESS_REGISTER_B, 3);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 3);
            ram.setSfr(Constants.ADDRESS_REGISTER_B, 1);
        }, 1);
    }

    @Test
    void testDiv_DivByZero()
    {
        test(new int[] { Opcode.DIV_AB.toByte() }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 10);
            ram.setSfr(Constants.ADDRESS_REGISTER_B, 0);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 10);
            ram.setSfr(Constants.ADDRESS_REGISTER_B, 0);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000100);
        }, 1);
    }

    @Test
    void testSetbBit()
    {
        test(new int[] { Opcode.SETB_BIT.toByte(), Constants.BIT_ADDRESS_FLAG0 }, 0, ram -> {}, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00100000);
        }, 2);
    }

    @Test
    void testSetbC()
    {
        test(new int[] { Opcode.SETB_C.toByte() }, 0, ram -> {}, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, 1);
    }

    @Test
    void testCplAcc()
    {
        test(new int[] { Opcode.CPL_ACC.toByte() }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b11010101);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001); // mov A,#11010101b would normally set this
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0b00101010);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
        }, 1);
    }

    @Test
    void testCplBit_ZeroToOne()
    {
        test(new int[] { Opcode.CPL_BIT.toByte(), Constants.BIT_ADDRESS_FLAG0 }, 0, ram -> {}, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00100000);
        }, 2);
    }

    @Test
    void testCplBit_OneToZero()
    {
        test(new int[] { Opcode.CPL_BIT.toByte(), Constants.BIT_ADDRESS_FLAG0 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00100000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0);
        }, 2);
    }

    @Test
    void testCplC_ZeroToOne()
    {
        test(new int[] { Opcode.CPL_C.toByte() }, 0, ram -> {}, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, 1);
    }

    @Test
    void testCplC_OneToZero()
    {
        test(new int[] { Opcode.CPL_C.toByte() }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0);
        }, 1);
    }

    @Test
    void testClrAcc()
    {
        test(new int[] { Opcode.CLR_ACC.toByte() }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 20);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0);
        }, 1);
    }

    @Test
    void testClrBit()
    {
        test(new int[] { Opcode.CLR_BIT.toByte(), Constants.BIT_ADDRESS_FLAG0 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00100000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0);
        }, 2);
    }

    @Test
    void testClrC()
    {
        test(new int[] { Opcode.CLR_C.toByte() }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0);
        }, 1);
    }

    @Test
    void testSwap()
    {
        test(new int[] { Opcode.SWAP.toByte() }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0xAB);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0xBA);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
        }, 1);
    }

    @Test
    void testXchMem()
    {
        test(new int[] { Opcode.XCH_MEM.toByte(), 10 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 10);
            ram.setRam(10, 20);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 20);
            ram.setRam(10, 10);
        }, 2);
    }

    @Test
    void testXchIrn()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { XCH_IRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 10);
                ram.setRam(10, 20);
                ram.setRam(reg, 10);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 20);
                ram.setRam(10, 10);
            }, 1);
        }
    }

    @Test
    void testXchDrn()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { XCH_DRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 10);
                ram.setRam(reg, 20);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 20);
                ram.setRam(reg, 10);
            }, 1);
        }
    }

    @Test
    void testXchdIrn()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { XCHD_IRN[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0xAB);
                ram.setRam(10, 0xBA);
                ram.setRam(reg, 10);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0xAA);
                ram.setRam(10, 0xBB);
            }, 1);
        }
    }

    @Test
    void testDa_NoAdjust()
    {
        test(new int[] { Opcode.DA.toByte() }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x25);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x25);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
        }, 1);
    }

    @Test
    void testDa_LowAdjust()
    {
        test(new int[] { Opcode.DA.toByte() }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x2B);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x31);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
        }, 1);
    }

    @Test
    void testDa_HighAdjust()
    {
        test(new int[] { Opcode.DA.toByte() }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0xB5);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x15);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000001);
        }, 1);
    }

    @Test
    void testDa_BothAdjust()
    {
        test(new int[] { Opcode.DA.toByte() }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x9B);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x01);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000001);
        }, 1);
    }

    @Test
    void testDa_IncomingAuxCarry()
    {
        test(new int[] { Opcode.DA.toByte() }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x23);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b01000000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x29);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b01000001);
        }, 1);
    }

    @Test
    void testDa_IncomingCarry()
    {
        test(new int[] { Opcode.DA.toByte() }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x25);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 0x85);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000001);
        }, 1);
    }

    @Test
    void testMovAccImm()
    {
        test(new int[] { Opcode.MOV_ACC_IMM.toByte(), 20 }, 0, ram -> {}, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 20);
        }, 2);
    }

    @Test
    void testMovMemImm()
    {
        test(new int[] { Opcode.MOV_MEM_IMM.toByte(), 10, 20 }, 0, ram -> {}, ram -> {
            ram.setRam(10, 20);
        }, 3);
    }

    @Test
    void testMovIrnImm()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { MOV_IRN_IMM[i].toByte(), 20 }, 0, ram -> {
                ram.setRam(reg, 10);
            }, ram -> {
                ram.setRam(10, 20);
            }, 2);
        }
    }

    @Test
    void testMovDrnImm()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { MOV_DRN_IMM[i].toByte(), 20 }, 0, ram -> {}, ram -> {
                ram.setRam(reg, 20);
            }, 2);
        }
    }

    @Test
    void testMovMemMem()
    {
        test(new int[] { Opcode.MOV_MEM_MEM.toByte(), 10, 20 }, 0, ram -> {
            ram.setRam(20, 30);
        }, ram -> {
            ram.setRam(10, 30);
        }, 3);
    }

    @Test
    void testMovMemIrn()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { MOV_MEM_IRN[i].toByte(), 10 }, 0, ram -> {
                ram.setRam(reg, 20);
                ram.setRam(20, 30);
            }, ram -> {
                ram.setRam(10, 30);
            }, 2);
        }
    }

    @Test
    void testMovMemDrn()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { MOV_MEM_DRN[i].toByte(), 10 }, 0, ram -> {
                ram.setRam(reg, 30);
            }, ram -> {
                ram.setRam(10, 30);
            }, 2);
        }
    }

    @Test
    void testMovAccMem()
    {
        test(new int[] { Opcode.MOV_ACC_MEM.toByte(), 20 }, 0, ram -> {
            ram.setRam(20, 30);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 30);
        }, 2);
    }

    @Test
    void testMovAccIrn()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { MOV_ACC_IRN[i].toByte() }, 0, ram -> {
                ram.setRam(20, 30);
                ram.setRam(reg, 20);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 30);
            }, 1);
        }
    }

    @Test
    void testMovAccDrn()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { MOV_ACC_DRN[i].toByte() }, 0, ram -> {
                ram.setRam(reg, 30);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 30);
            }, 1);
        }
    }

    @Test
    void testMovMemAcc()
    {
        test(new int[] { Opcode.MOV_MEM_ACC.toByte(), 20 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 30);
        }, ram -> {
            ram.setRam(20, 30);
        }, 2);
    }

    @Test
    void testMovIrnAcc()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { MOV_IRN_ACC[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 30);
                ram.setRam(reg, 20);
            }, ram -> {
                ram.setRam(20, 30);
            }, 1);
        }
    }

    @Test
    void testMovDrnAcc()
    {
        for (int i = 0; i < 8; i++)
        {
            int reg = i;
            test(new int[] { MOV_DRN_ACC[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 30);
            }, ram -> {
                ram.setRam(reg, 30);
            }, 1);
        }
    }

    @Test
    void testMovDptr()
    {
        test(new int[] { Opcode.MOV_DPTR.toByte(), 0x12, 0x34 }, 0, ram -> {}, ram -> {
            ram.setSfr(Constants.ADDRESS_DATA_POINTER_UPPER, 0x12);
            ram.setSfr(Constants.ADDRESS_DATA_POINTER_LOWER, 0x34);
        }, 3);
    }

    @Test
    void testMovBitC()
    {
        test(new int[] { Opcode.MOV_BIT_C.toByte(), 0x07 /* Byte 20h, Bit 7 */ }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, ram -> {
            ram.setRam(0x20, 0b10000000);
        }, 2);
    }

    @Test
    void testMovCBit()
    {
        test(new int[] { Opcode.MOV_C_BIT.toByte(), 0x07 /* Byte 20h, Bit 7 */ }, 0, ram -> {
            ram.setRam(0x20, 0b10000000);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b10000000);
        }, 2);
    }

    @Test
    void testMovcAccIapc()
    {
        test(new int[] { Opcode.MOVC_ACC_IAPC.toByte(), Opcode.NOP.toByte(), Opcode.NOP.toByte(), 10 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 2);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 10);
        }, 1);
    }

    @Test
    void testMovcAccIadptr()
    {
        test(new int[] { Opcode.MOVC_ACC_IADPTR.toByte(), Opcode.NOP.toByte(), Opcode.NOP.toByte(), 10 }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_DATA_POINTER_LOWER, 1);
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 2);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 10);
        }, 1);
    }

    @Test
    void testMovxAccIdptr()
    {
        test(new int[] { Opcode.MOVX_ACC_IDPTR.toByte() }, 0, ram -> {
            ram.setExt(10, 127);
            ram.setSfr(Constants.ADDRESS_DATA_POINTER_LOWER, 10);
        }, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 127);
            ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
        }, 1);
    }

    @Test
    void testMovxAccIrn()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { MOVX_ACC_IRN[i].toByte() }, 0, ram -> {
                ram.setExt(10, 127);
                ram.setRam(reg, 10);
            }, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 127);
                ram.setSfr(Constants.ADDRESS_STATUS_WORD, 0b00000001);
            }, 1);
        }
    }

    @Test
    void testMovxIdptrAcc()
    {
        test(new int[] { Opcode.MOVX_IDPTR_ACC.toByte() }, 0, ram -> {
            ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 127);
            ram.setSfr(Constants.ADDRESS_DATA_POINTER_LOWER, 10);
        }, ram -> {
            ram.setExt(10, 127);
        }, 1);
    }

    @Test
    void testMovxIrnAcc()
    {
        for (int i = 0; i < 2; i++)
        {
            int reg = i;
            test(new int[] { MOVX_IRN_ACC[i].toByte() }, 0, ram -> {
                ram.setSfr(Constants.ADDRESS_ACCUMULATOR, 127);
                ram.setRam(reg, 10);
            }, ram -> {
                ram.setExt(10, 127);
            }, 1);
        }
    }

    private static void test(
            int[] code,
            int initialPc,
            RamModifier setupModifier,
            RamModifier expectedModifier,
            int expectedPC
    )
    {
        Interpreter interpreter = new Interpreter();
        // Build ROM, padded by NOP until initial PC value
        byte[] romBytes = new byte[initialPc + code.length];
        for (int i = 0; i < code.length; i++)
        {
            romBytes[initialPc + i] = (byte) (code[i] & 0xFF);
        }
        interpreter.loadCode(new Code("test", romBytes, Int2ObjectMaps.emptyMap()));
        interpreter.setProgramCounter(initialPc);

        // Set up data needed for test
        setupModifier.modify(new RamAdapter(interpreter.getRam(), interpreter.getSfr(), interpreter.getExtRam()));

        // Set up expected ram layout
        byte[] ramExpected = interpreter.getRam().clone();
        byte[] sfrExpected = interpreter.getSfr().clone();
        byte[] extExpected = interpreter.getExtRam().clone();
        // Handle interrupt inputs being low immediately setting respective flags
        sfrExpected[Constants.ADDRESS_TCON - Constants.SFR_START] |= 0x0A;
        expectedModifier.modify(new RamAdapter(ramExpected, sfrExpected, extExpected));

        interpreter.run();
        byte[] ramActual = interpreter.getRam();
        byte[] sfrActual = interpreter.getSfr();
        byte[] extActual = interpreter.getExtRam();

        Assertions.assertEquals(expectedPC, interpreter.getProgramCounter(), "ProgramCounter does not match expected");
        Assertions.assertArrayEquals(ramExpected, ramActual, "RAM does not match expected");
        Assertions.assertArrayEquals(sfrExpected, sfrActual, "SFR does not match expected");
        Assertions.assertArrayEquals(extExpected, extActual, "External RAM does not match expected");
    }

    @FunctionalInterface
    private interface RamModifier
    {
        void modify(RamAdapter ram);
    }

    private record RamAdapter(byte[] ram, byte[] sfr, byte[] ext)
    {
        public void setRam(int address, int value)
        {
            ram[address] = (byte) (value & 0xFF);
        }

        public void setSfr(int address, int value)
        {
            sfr[address - Constants.SFR_START] = (byte) (value & 0xFF);
        }

        public void setExt(int address, int value)
        {
            ext[address] = (byte) (value & 0xFF);
        }
    }
}
