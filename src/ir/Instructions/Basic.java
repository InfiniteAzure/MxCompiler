package ir.Instructions;

import ir.BasicBlock;
import ir.IRVisitor;
import ir.User;
import ir.type.BasicType;

public abstract class Basic extends User {
    public BasicBlock father;

    public Basic(BasicType Type, String Name, BasicBlock Father) {
        super(Type, Name);
        this.ParentInit(Father);
    }

    public void ParentInit(BasicBlock Father) {
        if (this.father != Father) {
            this.father = Father;
            if (Father != null) {
                Father.addInstruction(this);
            }
        }
    }

    public abstract String toString();

    public boolean isTerminator() {
        return false;
    }

    public abstract void accept(IRVisitor visitor);
}
