package scope;

import ast.FunctionNode;
import ir.Function;
import ir.Value;
import ir.constant.GlobalConst;
import ir.type.StructType;

import java.util.HashMap;

public class GlobalScope extends Scope{
    public HashMap<String, ClassScope> classScopes = new HashMap<>();
    public HashMap<String, FunctionNode> functions = new HashMap<>();
    public HashMap<String, StructType> classTypes = new HashMap<>();
    public HashMap<String, Function> funcs = new HashMap<>();
    public HashMap<String, GlobalConst> globalVariables = new HashMap<>();
    public GlobalScope() {
        super(null);
    }

    public void addFunction(FunctionNode func) {
        if (functions.containsKey(func.name)) {
            throw new Error("redefinition of a function called " + func.name);
        }
        if (classScopes.containsKey(func.name)) {
            throw new Error("function name has already been used by a class");
        }
        functions.put(func.name, func);
    }

    public void addFunc(String Name,Function func) {
        this.funcs.put(Name,func);
    }

    public Function getFunc(String Name) {
        return funcs.get(Name);
    }

    public Value getVariable(String name, boolean recursive) {
        return globalVariables.get(name);
    }

    public FunctionNode getFunction(String name) {
        return this.functions.get(name);
    }

    public ClassScope getClassScope(String name) {
        return this.classScopes.get(name);
    }

    public void addClassScope(ClassScope Class) {
        if (classScopes.containsKey(Class.name)) {
            throw new Error("redefinition of a class called " + Class.name);
        }
        if (functions.containsKey(Class.name)) {
            throw new Error("class name has already been used by a function");
        }
        classScopes.put(Class.name, Class);
    }

    public void addClassType(String name, StructType Class) {
        this.classTypes.put(name, Class);
    }

    public StructType getClassType(String Name) {
        return this.classTypes.get(Name);
    }


}
