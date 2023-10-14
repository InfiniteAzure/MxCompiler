package ir.Instructions;

import ir.BasicBlock;
import ir.IRVisitor;
import ir.Value;
import ir.type.BasicType;

public class PhiInstruction extends Basic{
    public PhiInstruction(BasicType type, String Name, BasicBlock Father) {
        super(type, Name, null);
        ParentInit(Father);
    }

    public void ParentInit(BasicBlock Father) {
        if (this.father != Father) {
            this.father = Father;
            if (Father != null) {
                Father.phiInstructions.add(this);
            }
        }
    }
    public void addBr(Value val, BasicBlock block) {
        addOp(val);
        addOp(block);
    }

    public void removeBr(BasicBlock removedBlock) {
        for (int i = 1; i < Op.size(); i += 2) {
            if (this.Op.get(i) == removedBlock) {
                Op.remove(i);
                Op.remove(i - 1);
                return;
            }
        }
    }

    public String toString() {
        var ret = String.format("%s = phi %s ",name, type);
        for (int i = 0; i < Op.size(); i += 2) {
            ret += String.format("[%s, %s]",this.Op.get(i).name(), this.Op.get(i + 1).name());
            if (i < Op.size() - 2)
                ret += ", ";
        }
        return ret;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

}
