package ir.Instructions;

import ir.BasicBlock;
import ir.IRVisitor;
import ir.Value;
import ir.type.BasicType;

public class TruncInstruction extends Basic {
    public TruncInstruction(Value val, BasicType Type, String name, BasicBlock Father) {
        super(Type, name, Father);
        this.addOp(val);
    }

    public String toString() {
        var val = this.Op.get(0);
        return this.name() + " = trunc " + val.nameWithType() + " to " + this.type;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
