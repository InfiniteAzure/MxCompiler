package scope;

import ast.ConstructorNode;
import ast.FunctionNode;
import ast.statement.SingleVariableDefineNode;
import ir.Function;
import tools.Position;

import java.util.HashMap;

public class ClassScope extends Scope {
    public String name;
    public Position pos;
    public ConstructorNode construct;
    public HashMap<String, FunctionNode> functions = new HashMap<>();

    public HashMap<String, Integer> memberVariableIndex = new HashMap<>();
    public HashMap<String, Function> funcs = new HashMap<>();

    public ClassScope(String className, GlobalScope parent, Position pos) {
        super(parent);
        this.name = className;
        this.pos = pos;
    }

    public void DefineVariable(SingleVariableDefineNode v) {
        super.DefineVariable(v);
        this.memberVariableIndex.put(v.name, memberVariableIndex.size());
    }

    public void addFunctionDefine(FunctionNode func) {
        if (functions.containsKey(func.name)) {
            throw new Error("redefinition of a function");
        }
        functions.put(func.name, func);
    }

    public int getVariableIndex(String name) {
        return memberVariableIndex.get(name);
    }

    public FunctionNode getFuncDef(String name) {
        return this.functions.get(name);
    }

    public void addFunc(String Name, Function func) {
        this.funcs.put(Name, func);
    }

    public Function getFunc(String funcName) {
        return this.funcs.get(funcName);
    }

    public void addConstructor(ConstructorNode constructor) {
        if (!constructor.name.equals(name))
            throw new Error("class constructor should have the same name as the class");
        if (this.construct != null) { // Mx* only allows one constructor in a class
            throw new Error("redefinition of a class constructor");
        }
        this.construct = constructor;
    }

}
