package ir.Instructions;

import ir.BasicBlock;
import ir.IRVisitor;
import ir.Value;
import ir.type.VoidType;

public class ReturnInstruction extends Basic{
    public ReturnInstruction(BasicBlock Father) {
        super(new VoidType(), "ret", Father);
    }

    public ReturnInstruction(Value val, BasicBlock Father) {
        super(val.type, "ret", Father);
        this.addOp(val);
    }
    public String toString() {
        if (this.type instanceof VoidType) {
            return "ret void";
        }
        return "ret " + this.Op.get(0).nameWithType();
    }

    public boolean isTerminator() {
        return true;
    }
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

}
