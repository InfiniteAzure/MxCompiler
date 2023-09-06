package ir.Instructions;

import ir.BasicBlock;
import ir.Function;
import ir.IRVisitor;
import ir.Value;
import ir.type.VoidType;

import java.util.ArrayList;

public class CallInstruction extends Basic {
    public CallInstruction(String Name, Function function, BasicBlock Father, Value... args) {
        super(function.type().Return, Name, Father);
        addOp(function);
        for (var i : args) {
            addOp(i);
        }
    }

    public CallInstruction(String Name, Function function, BasicBlock Father, ArrayList<Value> args) {
        super(function.type().Return, Name, Father);
        addOp(function);
        for (var i : args) {
            addOp(i);
        }
    }

    public String toString() {
        Function func = (Function) this.Op.get(0);
        String s = "";
        if (!(this.type instanceof VoidType)) {
            s += this.name + " = ";
        }
        s += String.format("call %s %s(",func.type().Return, func.name());
        for (int i = 1; i < this.Op.size(); ++i) {
            if (i > 1)
                s += ", ";
            s += this.Op.get(i).nameWithType();
        }
        s += ")";
        return s;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

}
