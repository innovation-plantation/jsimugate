package jsimugate;


import java.util.function.IntBinaryOperator;

/**
 * Implementation of a simple arithmetic & logic unit with the operations in the AluOp enum
 */
public class Alu extends Adder {
    static int msb = 0x80;

    /**
     * Operations supported by the ALU
     * Carry: true if this operation affects the carry
     */
    enum AluOp {
        CLR(true, (a, b) -> 0), ///////////// 0 0 0 0
        AND(false, (a, b) -> a & b), //////// 0 0 0 1
        XOR(false, (a, b) -> a ^ b), //////// 0 0 1 0
        OR(false, (a, b) -> a | b), ///////// 0 0 1 1,

        ADD(true, (a, b) -> a + b), ///////// 0 1 0 0
        ADC(true, (a, b) -> a + b + 1), ///// 0 1 0 1
        SUB(true, (a, b) -> a - b), ///////// 0 1 1 0
        SBB(true, (a, b) -> a - b - 1), ///// 0 1 1 1

        SHL(true, (a, b) -> a << 1), //////// 0 0 0 0
        RLC(true, (a, b) -> (a << 1) | 1), // 0 0 0 1
        SHR(true, (a, b) -> a >> 1), //////// 0 0 1 0
        NOT(false, (a, b) -> ~a), /////////// 0 0 1 1

        INC(true, (a, b) -> a + 1), ///////// 0 1 0 0
        NEG(true, (a, b) -> -a), //////////// 0 1 0 1
        DEC(true, (a, b) -> a - 1), ///////// 0 1 1 0
        XFER(false, (a, b) -> a), /////////// 1 1 1
        ;

        private final IntBinaryOperator fn;
        private final boolean carry;

        AluOp(boolean carry, IntBinaryOperator f) {
            this.carry = carry;
            this.fn = f;
        }

        int perform(int a, int b) {
            return fn.applyAsInt(a, b);
        }
    }


    Pin fnIn[];
    Signal old_c = Signal._0;
    int fn;

    /**
     * ALU construction is like an adder with extra function pins.
     * The operation is also different according to the function selected by these pins.
     */
    public Alu() {
        label = "ALU";
        fnIn = new Pin[]{
                cIn, // Carry: and, or, adc, sbb, rlc, not, neg, xfer
                addPin(new Pin(10, 170).down(30)), // Alternate: xor, or, sub, sbb, shr, not, dec,xfer
                addPin(new Pin(-10, 180).down(30)), // Math: add, adc, sub, sbb; inc, neg, dec, xfer
                addPin(new Pin(-30, 190).down(30)), // Unary: shl, rlc, shr, not, inc, neg, dec, xfer
        };
    }

    /**
     * Perform the operation selected by the fn bins
     * on the input pin groups a and b,
     * The out pins and the carry pin receive the output.
     */
    public void operate() {
        getAB();
        fn = Pin.pack(fnIn);
        AluOp op = AluOp.values()[fn];
        result = op.perform(a, b);
        int cy = result & 0x100;
        if (op.carry) cOut.setOutValue(old_c = Signal.fromBit(cy));
        else cOut.setOutValue(old_c);
        label = op.toString();
        putResult();
    }


}
