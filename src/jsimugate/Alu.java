package jsimugate;


import java.util.function.IntBinaryOperator;

/**
 * Implementation of a simple arithmetic & logic unit with the operations in the AluOp enum
 */
public class Alu extends Adder {
    static int msb = 0x80;

    /**
     * Operations supported by the ALU
     * Organization: Unary bit , Arithmetic Bit , Down Bit , Carry Bit
     */
    enum AluOp {
        CLR((a, b) -> 0), //////////// 0 0 0 0
        AND((a, b) -> a & b), //////// 0 0 0 1
        XOR((a, b) -> a ^ b), //////// 0 0 1 0
        OR((a, b) -> a | b), ///////// 0 0 1 1

        ADD((a, b) -> a + b), ///////// 0 1 0 0
        ADC((a, b) -> a + b + 1), ///// 0 1 0 1
        SUB((a, b) -> a - b), ///////// 0 1 1 0
        SBB((a, b) -> a - b - 1), ///// 0 1 1 1

        SHL((a, b) -> b << 1), //////// 0 0 0 0
        RLC((a, b) -> (b << 1) | 1), // 0 0 0 1
        SHR((a, b) -> b >> 1), //////// 0 0 1 0
        NOT((a, b) -> ~b), //////////// 0 0 1 1

        INC((a, b) -> b + 1), ///////// 0 1 0 0
        NEG((a, b) -> -b), //////////// 0 1 0 1
        DEC((a, b) -> b - 1), ///////// 0 1 1 0
        XFER((a, b) -> b), //////////// 1 1 1 1
        ;

        private final IntBinaryOperator fn;

        AluOp(IntBinaryOperator f) {
            this.fn = f;
        }

        int perform(int a, int b) {
            return fn.applyAsInt(a, b);
        }
    }


    PinGroup fnIn = new PinGroup();
    Signal old_c = Signal._0;
    int fn;

    /**
     * ALU construction is like an adder with extra function pins.
     * The operation is also different according to the function selected by these pins.
     */
    public Alu() {
        label = "ALU";
        fnIn.addPinHorizontally(cIn.translate(-25, 0)); // Carry: and, or, adc, sbb, rlc, not, neg, xfer
        addPin(fnIn.addPinHorizontally(new Pin(-5, 170).down(30))); // Alternate: xor, or, sub, sbb, shr, not, dec,xfer
        addPin(fnIn.addPinHorizontally(new Pin(-15, 180).down(30))); // Math: add, adc, sub, sbb; inc, neg, dec, xfer
        addPin(fnIn.addPinHorizontally(new Pin(-25, 190).down(30))); // Unary: shl, rlc, shr, not, inc, neg, dec, xfer
    }

    /**
     * Perform the operation selected by the fn bins
     * on the input pin groups a and b,
     * The out pins and the carry pin receive the output.
     */
    public void operate() {
        getAB();
        fn = fnIn.getValue();
        AluOp op = AluOp.values()[fn];
        result = op.perform(a, b);
        int cy = result & 0x100;
        cOut.setOutValue(old_c = Signal.fromBit(cy));
        label = op.toString();
        putResult();
    }


}
