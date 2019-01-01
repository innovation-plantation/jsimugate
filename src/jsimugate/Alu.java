package jsimugate;


import java.util.function.IntBinaryOperator;

public class Alu extends Part {
    static int msb = 0x80;

    enum AluOp8bit {
        XFER(false, (a, b) -> a),
        XOR(false, (a, b) -> a ^ b),
        AND(false, (a, b) -> a & b),
        OR(false, (a, b) -> a | b),
        NOT(false, (a, b) -> ~a),
        NEG(true, (a, b) -> -a),
        INC(true, (a, b) -> a + 1),
        DEC(true, (a, b) -> a - 1),
        ADD(true, (a, b) -> a + b),
        ADC(true, (a, b) -> a + b + 1),
        SUB(true, (a, b) -> a - b),
        SBB(true, (a, b) -> a - b - 1),
        SHL(true, (a, b) -> a << 1),
        RLC(true, (a, b) -> (a << 1) | 1),
        SHR(true, (a, b) -> a >> 1),
        CLR(true, (a, b) -> 0);

        private final IntBinaryOperator fn;
        private final boolean carry;

        AluOp8bit(boolean carry, IntBinaryOperator f) {
            this.carry = carry;
            this.fn = f;
        }

        int perform(int a, int b) {
            return fn.applyAsInt(a, b);
        }
    }

    Pin aIn[];
    Pin bIn[];
    Pin fnIn[];
    Pin out[];
    Pin cOut;
    Signal old_c=Signal._X;

    public Alu() {
        label="ALU";
        aIn = new Pin[]{
                addPin(new Pin(-70, -20).left(20)),
                addPin(new Pin(-70, -40).left(20)),
                addPin(new Pin(-70, -60).left(20)),
                addPin(new Pin(-70, -80).left(20)),
                addPin(new Pin(-70, -100).left(20)),
                addPin(new Pin(-70, -120).left(20)),
                addPin(new Pin(-70, -140).left(20)),
                addPin(new Pin(-70, -160).left(20)),
        };
        bIn = new Pin[]{
                addPin(new Pin(-70, 160).left(20)),
                addPin(new Pin(-70, 140).left(20)),
                addPin(new Pin(-70, 120).left(20)),
                addPin(new Pin(-70, 100).left(20)),
                addPin(new Pin(-70, 80).left(20)),
                addPin(new Pin(-70, 60).left(20)),
                addPin(new Pin(-70, 40).left(20)),
                addPin(new Pin(-70, 20).left(20)),
        };

        fnIn = new Pin[]{
                addPin(new Pin(-30, 180).down(20)),
                addPin(new Pin(-10, 170).down(20)),
                addPin(new Pin(10, 160).down(20)),
                addPin(new Pin(30, 150).down(20)),
        };
        out = new Pin[] {
                addPin(new Pin(70,80).right(20)),
                addPin(new Pin(70,60).right(20)),
                addPin(new Pin(70,40).right(20)),
                addPin(new Pin(70,20).right(20)),
                addPin(new Pin(70,0).right(20)),
                addPin(new Pin(70,-20).right(20)),
                addPin(new Pin(70,-40).right(20)),
                addPin(new Pin(70,-60).right(20)),
        };
        cOut = addPin(new Pin(70,-100).right(20));
    }

    public void operate() {
        int a = pack(aIn), b=pack(bIn), fn=pack(fnIn);
        AluOp8bit op = AluOp8bit.values()[fn];
        int result = op.perform(a,b);
        int cy=result&0x100;
        if (op.carry) cOut.setOutValue(old_c=Signal.fromBit(cy));
        else cOut.setOutValue(old_c);
        unpack(out,result);
        label = op.toString();
    }

    private int pack(Pin[] pins) {
        int result = 0;
        for (int n = 0; n < pins.length; n++) result += pins[n].getInValue().asBit() << n;
        return result;
    }
    void unpack(Pin[] pins,int src) {
        for (int n = 0; n < pins.length; n++) pins[n].setInValue(Signal.fromBit((src>>n)&1));
    }
}
