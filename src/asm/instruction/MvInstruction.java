package asm.instruction;

import asm.Block;
import asm.asmVisitor;
import asm.operand.SimpleReg;

import java.util.HashSet;

public class MvInstruction extends BasicInstruction {
    public SimpleReg rd, rs;

    public MvInstruction(SimpleReg Rd, SimpleReg Rs, Block Father) {
        super(Father);
        this.rd = Rd;
        this.rs = Rs;
    }

    public String toString() {
        return String.format("mv %s, %s", rd, rs);
    }

    public void accept(asmVisitor visitor) {
        visitor.visit(this);
    }

    public HashSet<SimpleReg> uses() {
        var ret = new HashSet<SimpleReg>();
        ret.add(rs);
        return ret;
    }

    @Override
    public HashSet<SimpleReg> defs() {
        var ret = new HashSet<SimpleReg>();
        ret.add(rd);
        return ret;
    }

    @Override
    public void replaceUse(SimpleReg oldReg, SimpleReg newReg) {
        if (rs == oldReg)
            rs = newReg;
    }

    @Override
    public void replaceDef(SimpleReg oldReg, SimpleReg newReg) {
        if (rd == oldReg)
            rd = newReg;
    }
}
