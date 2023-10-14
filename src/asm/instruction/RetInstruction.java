package asm.instruction;

import asm.Block;
import asm.asmVisitor;
import asm.operand.PhysicalReg;
import asm.operand.SimpleReg;

import java.util.HashSet;

public class RetInstruction extends BasicInstruction{
    public RetInstruction(Block Father) {
        super(Father);
    }

    public String toString() {
        return "ret";
    }

    public void accept(asmVisitor visitor) {
        visitor.visit(this);
    }

    public HashSet<SimpleReg> uses() {
        var ret = new HashSet<SimpleReg>();
        ret.add(PhysicalReg.reg("ra"));
        return ret;
    }

    public HashSet<SimpleReg> defs() {
        return new HashSet<>();
    }

    public void replaceUse(SimpleReg oldReg, SimpleReg newReg) {
    }

    public void replaceDef(SimpleReg oldReg, SimpleReg newReg) {
    }
}
