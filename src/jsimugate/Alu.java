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
        setShape(Artwork.adderShape());
        aIn = new Pin[]{
                addPin(new Pin(-80, -20).left(30)),
                addPin(new Pin(-80, -40).left(30)),
                addPin(new Pin(-80, -60).left(30)),
                addPin(new Pin(-80, -80).left(30)),
                addPin(new Pin(-80, -100).left(30)),
                addPin(new Pin(-80, -120).left(30)),
                addPin(new Pin(-80, -140).left(30)),
                addPin(new Pin(-80, -160).left(30)),
        };
        bIn = new Pin[]{
                addPin(new Pin(-80, 160).left(30)),
                addPin(new Pin(-80, 140).left(30)),
                addPin(new Pin(-80, 120).left(30)),
                addPin(new Pin(-80, 100).left(30)),
                addPin(new Pin(-80, 80).left(30)),
                addPin(new Pin(-80, 60).left(30)),
                addPin(new Pin(-80, 40).left(30)),
                addPin(new Pin(-80, 20).left(30)),
        };

        fnIn = new Pin[]{
                addPin(new Pin(-30, 190).down(30)),
                addPin(new Pin(-10, 180).down(30)),
                addPin(new Pin(10, 170).down(30)),
                addPin(new Pin(30, 160).down(30)),
        };
        out = new Pin[] {
                addPin(new Pin(80,80).right(30)),
                addPin(new Pin(80,60).right(30)),
                addPin(new Pin(80,40).right(30)),
                addPin(new Pin(80,20).right(30)),
                addPin(new Pin(80,0).right(30)),
                addPin(new Pin(80,-20).right(30)),
                addPin(new Pin(80,-40).right(30)),
                addPin(new Pin(80,-60).right(30)),
        };
        cOut = addPin(new Pin(80,-100).right(30));
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
