package ir.Instructions;

import ir.BasicBlock;
import ir.IRVisitor;
import ir.Value;
import ir.type.VoidType;

public class BrInstruction extends Basic {
    public BrInstruction(BasicBlock destination, BasicBlock Father) {
        super(new VoidType(), "br", Father);
        addOp(destination);
    }

    public BrInstruction(Value condition, BasicBlock IfFirst, BasicBlock IfSecond, BasicBlock Father) {
        super(new VoidType(), "br", Father);
        addOp(condition);
        addOp(IfFirst);
        addOp(IfSecond);
    }

    public String toString() {
        if (this.Op.size() == 1) {
            return "br " + Op.get(0).nameWithType();
        }
        return String.format("br %s, %s, %s",this.Op.get(0).nameWithType(),
                this.Op.get(1).nameWithType(), this.Op.get(2).nameWithType());
    }

    public boolean isTerminator() {
        return true;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
