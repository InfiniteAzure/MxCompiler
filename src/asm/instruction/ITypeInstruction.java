package asm.instruction;

import asm.Block;
import asm.asmVisitor;
import asm.operand.Imm;
import asm.operand.SimpleReg;

import java.util.HashSet;

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

    public HashSet<SimpleReg> uses() {
        var ret = new HashSet<SimpleReg>();
        ret.add(rs);
        return ret;
    }

    public HashSet<SimpleReg> defs() {
        var ret = new HashSet<SimpleReg>();
        ret.add(rd);
        return ret;
    }

    public void replaceUse(SimpleReg oldReg, SimpleReg newReg) {
        if (rs == oldReg) {
            rs = newReg;
        }

    }

    public void replaceDef(SimpleReg oldReg, SimpleReg newReg) {
        if (rd == oldReg) {
            rd = newReg;
        }
    }
}
