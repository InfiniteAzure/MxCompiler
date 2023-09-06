package ir.Instructions;

import ir.BasicBlock;
import ir.IRVisitor;
import ir.Value;
import ir.type.BasicType;
import ir.type.PointerType;
import tools.TextUtils;

public class GetElementPtrInstruction extends Basic {
    public GetElementPtrInstruction(String Name, BasicType Return, Value ptr, BasicBlock Father, Value... index) {
        super(Return, Name, Father);
        this.addOp(ptr);
        for (var i : index)
            this.addOp(i);
    }


    public String toString() {
        return String.format("%s = getelementptr inbounds %s, %s",
                name(), ((PointerType) this.Op.get(0).type).element,
                TextUtils.join(Op, x -> x.nameWithType()));
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
