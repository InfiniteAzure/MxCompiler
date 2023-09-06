package ir.Instructions;

import ir.BasicBlock;
import ir.IRVisitor;
import ir.Value;
import ir.type.BasicType;

public class ZextInstruction extends Basic {
    public ZextInstruction(Value val, BasicType Type, String name, BasicBlock Father) {
        super(Type, name, Father);
        this.addOp(val);
    }

    @Override
    public String toString() {
        var val = this.Op.get(0);
        return this.name + " = zext " + val.nameWithType() + " to " + this.type;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

}
