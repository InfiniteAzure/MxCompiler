package ir;

import ir.constant.Constant;
import ir.type.FunctionType;

import java.util.ArrayList;

public class Function extends Constant {
    public ArrayList<BasicBlock> blocks = new ArrayList<>();
    public BasicBlock entry, exit;
    public Value returnValue;
    public boolean member;

    public Function(FunctionType Type, String Name, boolean Member) {
        super(Type, Name);
        this.member = Member;
    }

    public FunctionType type() {
        return (FunctionType) type;
    }

    public void addArgs(Value add) {
        this.addOp(add);
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
