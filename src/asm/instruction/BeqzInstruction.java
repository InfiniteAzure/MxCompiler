package asm.instruction;

import asm.Block;
import asm.asmVisitor;
import asm.operand.SimpleReg;

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
}
