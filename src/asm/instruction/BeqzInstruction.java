package asm.instruction;

import asm.Block;
import asm.asmVisitor;
import asm.operand.SimpleReg;

import java.util.HashSet;

public class BeqzInstruction extends BasicInstruction{
    public SimpleReg rs;
    public Block dest;

    public BeqzInstruction(SimpleReg Rs, Block destination, Block Father) {
        super(Father);
        this.rs = Rs;
        this.dest = destination;
    }

    public String toString() {
        return String.format("beqz %s, %s",rs, dest.label);
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
        return new HashSet<SimpleReg>();
    }

    public void replaceUse(SimpleReg oldReg, SimpleReg newReg) {
        if (rs == oldReg) {
            rs = newReg;
        }
    }

    public void replaceDef(SimpleReg oldReg, SimpleReg newReg) {
    }
}
