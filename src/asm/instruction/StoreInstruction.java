package asm.instruction;

import asm.Block;
import asm.asmVisitor;
import asm.operand.Imm;
import asm.operand.SimpleReg;

import java.util.HashSet;

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
    public HashSet<SimpleReg> uses() {
        var ret = new HashSet<SimpleReg>();
        ret.add(rs1);
        ret.add(rs2);
        return ret;
    }

    public HashSet<SimpleReg> defs() {
        return new HashSet<>();
    }

    public void replaceUse(SimpleReg oldReg, SimpleReg newReg) {
        if (rs1 == oldReg)
            rs1 = newReg;
        if (rs2 == oldReg)
            rs2 = newReg;
    }

    public void replaceDef(SimpleReg oldReg, SimpleReg newReg) {}
}
