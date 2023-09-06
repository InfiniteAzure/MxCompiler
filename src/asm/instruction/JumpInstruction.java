package asm.instruction;

import asm.Block;
import asm.asmVisitor;

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
}
