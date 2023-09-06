package asm.instruction;

import asm.Block;
import asm.asmVisitor;
import asm.operand.Imm;
import asm.operand.SimpleReg;

public class ITypeInstruction extends BasicInstruction {
    public String op;
    public SimpleReg rd, rs;
    public Imm imm;

    public ITypeInstruction(String Op, SimpleReg Rd, SimpleReg Rs, Imm Imm, Block Father) {
        super(Father);
        this.op = Op;
        this.rd = Rd;
        this.rs = Rs;
        this.imm = Imm;
    }

    public String toString() {
        if (this.imm == null) {
            return String.format("%s %s, %s", op, rd, rs);
        } else {
            return String.format("%s %s, %s, %s", op, rd, rs, imm);
        }
    }

    public void accept(asmVisitor visitor) {
        visitor.visit(this);
    }
}
