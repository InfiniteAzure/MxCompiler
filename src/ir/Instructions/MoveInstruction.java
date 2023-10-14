package ir.Instructions;

import ir.BasicBlock;
import ir.IRVisitor;
import ir.Value;

public class MoveInstruction extends Basic {
    //not LLVM, only to eliminate phi(maybe not correct)
    //a genius idea from my 1024th!
    public MoveInstruction(Value dest, Value src, BasicBlock parent) {
        super(null, null, parent);
        addOp(dest);
        addOp(src);
    }

    public String toString() {
        return String.format("move %s %s", Op.get(0).name(), Op.get(1).name());
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
