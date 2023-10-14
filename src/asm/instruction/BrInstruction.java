package asm.instruction;

import asm.Block;
import asm.operand.SimpleReg;

import java.util.HashSet;

public class BrInstruction extends BasicInstruction{
    public String op;
    public SimpleReg rs1, rs2;
    public Block dest;

    public BrInstruction(String op, SimpleReg rs1, SimpleReg rs2, Block dest, Block parent) {
        super(parent);
        this.op = op;
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.dest = dest;
    }

    public String toString() {
        return "%s %s, %s, %s".formatted(op, rs1, rs2, dest.label);
    }

    public void accept(asm.asmVisitor visitor) {
        visitor.visit(this);
    }

    public HashSet<SimpleReg> uses() {
        var ret = new HashSet<SimpleReg>();
        ret.add(rs1);
        ret.add(rs2);
        return ret;
    }

    public HashSet<SimpleReg> defs() {
        return new HashSet<SimpleReg>();
    }

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
    }
}
