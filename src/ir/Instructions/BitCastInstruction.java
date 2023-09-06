package ir.Instructions;

import ir.BasicBlock;
import ir.IRVisitor;
import ir.Value;
import ir.type.BasicType;

public class BitCastInstruction extends Basic {
    public BitCastInstruction(String Name, BasicType Type, Value value, BasicBlock Father) {
        super(Type, Name, Father);
        addOp(value);
    }

    public String toString() {
        return String.format("%s = bitcast %s to %s",name(), this.Op.get(0).nameWithType(), this.type);
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
