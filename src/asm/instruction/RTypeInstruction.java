package asm.instruction;

import asm.Block;
import asm.asmVisitor;
import asm.operand.SimpleReg;

import java.util.HashSet;

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

    public HashSet<SimpleReg> uses() {
        var ret = new HashSet<SimpleReg>();
        ret.add(rs1);
        ret.add(rs2);
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
        if (rs1 == oldReg) {
            rs1 = newReg;
        }
        if (rs2 == oldReg) {
            rs2 = newReg;
        }

    }

    @Override
    public void replaceDef(SimpleReg oldReg, SimpleReg newReg) {
        if (rd == oldReg) {
            rd = newReg;
        }
    }
}
