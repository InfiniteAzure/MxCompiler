package asm.instruction;

import asm.Block;
import asm.asmVisitor;

public class CallInstruction extends BasicInstruction{
    public String funcName;

    public CallInstruction(String Name, Block Father) {
        super(Father);
        this.funcName = Name;
    }

    public String toString() {
        return "call " + funcName;
    }

    public void accept(asmVisitor visitor) {
        visitor.visit(this);
    }
}
