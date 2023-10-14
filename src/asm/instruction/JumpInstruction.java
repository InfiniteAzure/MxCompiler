package asm.instruction;

import asm.Block;
import asm.asmVisitor;
import asm.operand.SimpleReg;

import java.util.HashSet;

public class JumpInstruction extends BasicInstruction{
    public Block dest;

    public JumpInstruction(Block destination, Block Father) {
        super(Father);
        this.dest = destination;
    }

    public String toString() {
        return "j " + dest.label;
    }

    public void accept(asmVisitor visitor) {
        visitor.visit(this);
    }

    public HashSet<SimpleReg> uses() {
        return new HashSet<>();
    }

    public HashSet<SimpleReg> defs() {
        return new HashSet<>();
    }

    public void replaceUse(SimpleReg oldReg, SimpleReg newReg) {}

    public void replaceDef(SimpleReg oldReg, SimpleReg newReg) {}
}
