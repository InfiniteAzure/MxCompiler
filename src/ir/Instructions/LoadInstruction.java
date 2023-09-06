package ir.Instructions;

import ir.BasicBlock;
import ir.IRVisitor;
import ir.Value;
import ir.type.PointerType;

public class LoadInstruction extends Basic{
    public LoadInstruction(String Name, Value ptr, BasicBlock Father) {
        super(((PointerType) ptr.type).element, Name, Father);
        this.addOp(ptr);
    }

    public String toString() {
        var ptr = this.Op.get(0);
        return String.format("%s = load %s, %s, align %d",this.name(), this.type, ptr.nameWithType(), ptr.type.size());
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
