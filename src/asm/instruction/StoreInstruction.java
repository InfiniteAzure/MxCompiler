package asm.instruction;

import asm.Block;
import asm.asmVisitor;
import asm.operand.Imm;
import asm.operand.SimpleReg;

public class StoreInstruction extends BasicInstruction {
    public SimpleReg rs1, rs2;
    public Imm offset;
    public int size;

    public StoreInstruction(int Size, SimpleReg Val, SimpleReg Addr, Imm Offset, Block Father) {
        super(Father);
        this.rs1 = Addr;
        this.rs2 = Val;
        this.offset = Offset;
        this.size = Size;
    }

    public String toString() {
        if (size == 1) {
            return String.format("sb %s, %s(%s)", rs2, offset, rs1);
        } else {
            return String.format("sw %s, %s(%s)", rs2, offset, rs1);
        }
    }

    public void accept(asmVisitor visitor) {
        visitor.visit(this);
    }
}
