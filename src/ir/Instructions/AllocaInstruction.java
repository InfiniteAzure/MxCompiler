package ir.Instructions;

import ir.BasicBlock;
import ir.IRVisitor;
import ir.type.BasicType;
import ir.type.PointerType;

public class AllocaInstruction extends Basic {
    BasicType element;

    public AllocaInstruction(BasicType Type, String Name, BasicBlock Father) {
        super(new PointerType(Type), Name, Father);
        this.element = Type;
    }

    public String toString() {
        return String.format("%s = alloca %s, align %d",name(),element.toString(), element.size());
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
