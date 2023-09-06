package asm.instruction;

import asm.Block;
import asm.asmVisitor;

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
}
