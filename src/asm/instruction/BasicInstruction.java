package asm.instruction;

import asm.Block;
import asm.asmVisitor;

public abstract class BasicInstruction {
    public BasicInstruction(Block Father) {
        if (Father != null)
            Father.instructions.add(this);
    }
    public abstract String toString();

    public abstract void accept(asmVisitor visitor);
}
