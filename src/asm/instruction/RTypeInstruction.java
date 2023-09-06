package asm.instruction;

import asm.Block;
import asm.asmVisitor;
import asm.operand.SimpleReg;

public class RTypeInstruction extends BasicInstruction {
    public String op;
    public SimpleReg rd, rs1, rs2;

    public RTypeInstruction(String Op, SimpleReg Rd, SimpleReg Rs1, SimpleReg Rs2, Block Father) {
        super(Father);
        this.rd = Rd;
        this.rs1 = Rs1;
        this.rs2 = Rs2;
        this.op = Op;
    }

    public String toString() {
        return String.format("%s %s, %s, %s", op, rd, rs1, rs2);
    }

    public void accept(asmVisitor visitor) {
        visitor.visit(this);
    }
}
