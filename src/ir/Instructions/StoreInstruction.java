package ir.Instructions;

import ir.BasicBlock;
import ir.IRVisitor;
import ir.Value;
import ir.type.PointerType;

public class StoreInstruction extends Basic{
    public StoreInstruction(Value val, Value ptr, BasicBlock Father) {
        super(((PointerType) ptr.type).element, "store", Father);
        this.addOp(val);
        this.addOp(ptr);
    }

    public String toString() {
        var val = this.Op.get(0);
        var ptr = this.Op.get(1);
        return String.format("store %s %s, %s, align %d",this.type, val.name(), ptr.nameWithType(), val.type.size());
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
