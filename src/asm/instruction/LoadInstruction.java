package asm.instruction;

import asm.Block;
import asm.asmVisitor;
import asm.operand.Imm;
import asm.operand.SimpleReg;

public class LoadInstruction extends BasicInstruction {
    public SimpleReg rd, rs;
    public Imm offset;
    public int size;

    public LoadInstruction(int Size, SimpleReg Rd, SimpleReg Rs, Imm Offset, Block Father) {
        super(Father);
        this.rd = Rd;
        this.rs = Rs;
        this.offset = Offset;
        this.size = Size;
    }

    public String toString() {
        if (size == 1) {
            return String.format("lb %s, %s(%s)", rd, offset, rs);
        } else {
            return String.format("lw %s, %s(%s)", rd, offset, rs);
        }

    }

    public void accept(asmVisitor visitor) {
        visitor.visit(this);
    }
}
