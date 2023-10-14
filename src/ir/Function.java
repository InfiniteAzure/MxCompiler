package ir;

import ir.constant.Constant;
import ir.type.FunctionType;
import optimize.Loop;

import java.util.ArrayList;
import java.util.HashSet;

public class Function extends Constant {
    public ArrayList<BasicBlock> blocks = new ArrayList<>();
    public BasicBlock entry, exit;
    public Value returnValue;
    public boolean member;

    public HashSet<Loop> rootLoops = new HashSet<>();

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
