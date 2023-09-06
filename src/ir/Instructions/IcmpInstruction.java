package ir.Instructions;

import ir.BasicBlock;
import ir.IRVisitor;
import ir.Value;
import ir.type.IntType;

public class IcmpInstruction extends Basic{
    public String op;

    public IcmpInstruction(String Op, Value val1, Value val2, String name, BasicBlock Father) {
        super(new IntType(1,false), name, Father);
        this.op = Op;
        this.addOp(val1);
        this.addOp(val2);
    }

    public String toString() {
        return String.format("%s = icmp %s %s, %s",name(), op, Op.get(0).nameWithType(), Op.get(1).name());
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

}
