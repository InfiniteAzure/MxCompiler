package asm.instruction;

import asm.Block;
import asm.asmVisitor;
import asm.operand.SimpleReg;

import java.util.HashSet;

public abstract class BasicInstruction {
    public BasicInstruction(Block Father) {
        if (Father != null)
            Father.instructions.add(this);
    }
    public abstract String toString();

    public abstract HashSet<SimpleReg> uses();

    public abstract HashSet<SimpleReg> defs();

    public abstract void replaceUse(SimpleReg oldReg, SimpleReg newReg);

    public abstract void replaceDef(SimpleReg oldReg, SimpleReg newReg);

    public abstract void accept(asmVisitor visitor);
}
