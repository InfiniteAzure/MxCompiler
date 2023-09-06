package ir;

import ir.constant.GlobalConst;
import ir.constant.StringConst;
import ir.type.StructType;

import java.util.ArrayList;

public class Module {
    public ArrayList<GlobalConst> globalVariables = new ArrayList<>();
    public ArrayList<StringConst> strings = new ArrayList<>();
    public ArrayList<Function> functions = new ArrayList<>();
    public ArrayList<StructType> classes = new ArrayList<>();

    public ArrayList<Function> functionsDeclarations = new ArrayList<>();

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
