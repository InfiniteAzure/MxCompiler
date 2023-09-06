package asm.instruction;

import asm.Block;
import asm.asmVisitor;
import asm.operand.SimpleReg;

public class MvInstruction extends BasicInstruction {
    public SimpleReg rd, rs1;

    public MvInstruction(SimpleReg Rd, SimpleReg Rs1, Block Father) {
        super(Father);
        this.rd = Rd;
        this.rs1 = Rs1;
    }

    public String toString() {
        return String.format("mv %s, %s", rd, rs1);
    }

    public void accept(asmVisitor visitor) {
        visitor.visit(this);
    }
}
