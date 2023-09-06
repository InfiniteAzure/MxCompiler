package asm.operand;

import asm.Operand;

public class Imm extends Operand {
    public int value;

    public Imm(int v) {
        this.value = v;
    }

    public String toString() {
        return Integer.toString(value);
    }
}
