package scope;

import ast.TypeNode;
import ast.statement.SingleVariableDefineNode;

import java.util.HashMap;

public class Scope {
    public Scope father;

    public HashMap<String, SingleVariableDefineNode> variableDefinitions = new HashMap<>();


    public Scope(Scope f) {
        this.father = f;
    }

    public void DefineVariable(SingleVariableDefineNode def) {
        if (variableDefinitions.containsKey(def.name)) {
            throw new Error("variable redefinition occured at row:" + def.pos.row + " column:" + def.pos.column);
        }
        variableDefinitions.put(def.name,def);
        return;
    }

    public SingleVariableDefineNode getVariableDefine(String name, boolean recursive) {
        var i = this.variableDefinitions.get(name);
        if (i != null)
            return i;
        if (recursive && this.father != null)
            return this.father.getVariableDefine(name, true);
        return null;
    }

    public boolean insideLoop() {
        if (this instanceof LoopScope)
            return true;
        if (!(this instanceof FunctionScope) && !(this instanceof ClassScope) && this.father != null)
            return this.father.insideLoop();
        return false;
    }

    public LoopScope getLoopScope() {
        if (this instanceof LoopScope)
            return (LoopScope) this;
        if (!(this instanceof FunctionScope) && !(this instanceof ClassScope) && this.father != null)
            return this.father.getLoopScope();
        return null;
    }

    public ClassScope getClassScope() {
        if (this instanceof ClassScope)
            return (ClassScope) this;
        if (this.father != null)
            return this.father.getClassScope();
        return null;
    }

    public FunctionScope getFunctionScope() {
        if (this instanceof FunctionScope)
            return (FunctionScope) this;
        if (this.father != null)
            return this.father.getFunctionScope();
        return null;
    }

    public TypeNode getReturnType() {
        return this.father.getReturnType();
    }

}