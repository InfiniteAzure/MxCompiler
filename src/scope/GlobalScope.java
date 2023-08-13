package scope;

import ast.FunctionNode;

import java.util.HashMap;

public class GlobalScope extends Scope{
    public HashMap<String, ClassScope> classScopes = new HashMap<>();
    public HashMap<String, FunctionNode> functions = new HashMap<>();

    public GlobalScope() {
        super(null);
    }

    public void addFunction(FunctionNode func) {
        if (functions.containsKey(func.name)) {
            throw new Error("redifinition of a function called " + func.name);
        }
        if (classScopes.containsKey(func.name)) {
            throw new Error("function name has already been used by a class");
        }
        functions.put(func.name, func);
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







}
