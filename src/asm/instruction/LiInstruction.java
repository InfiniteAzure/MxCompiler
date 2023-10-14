package asm.instruction;

import asm.Block;
import asm.asmVisitor;
import asm.operand.Imm;
import asm.operand.SimpleReg;

import java.util.HashSet;

public class LiInstruction extends BasicInstruction{
    public SimpleReg rd;
    public Imm imm;

    public LiInstruction(SimpleReg Rd, Imm Imm, Block Father) {
        super(Father);
        this.rd = Rd;
        this.imm = Imm;
    }

    public String toString() {
        return String.format("li %s, %s",rd, imm);
    }

    public void accept(asmVisitor visitor) {
        visitor.visit(this);
    }

    public HashSet<SimpleReg> uses() {
        return new HashSet<SimpleReg>();
    }

    public HashSet<SimpleReg> defs() {
        var ret = new HashSet<SimpleReg>();
        ret.add(rd);
        return ret;
    }

    public void replaceUse(SimpleReg oldReg, SimpleReg newReg) {
    }

    public void replaceDef(SimpleReg oldReg, SimpleReg newReg) {
        if (rd == oldReg)
            rd = newReg;
    }

}
