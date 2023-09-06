package ir.Instructions;

import ir.BasicBlock;
import ir.IRVisitor;
import ir.Value;

public class BinaryInstruction extends Basic {
    public String op;

    public BinaryInstruction(String Op, Value left, Value right, String Name, BasicBlock Father) {
        super(left.type, Name, Father);
        this.op = Op;
        this.addOp(left);
        this.addOp(right);
    }

    public String toString() {
        return String.format("%s = %s %s %s, %s",name(),op,type,this.Op.get(0).name(),this.Op.get(1).name());
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
