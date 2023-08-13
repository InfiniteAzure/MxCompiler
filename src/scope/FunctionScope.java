package scope;

import ast.TypeNode;

public class FunctionScope extends Scope{
    public TypeNode returnType;
    public boolean checkReturn = false;

    public FunctionScope(TypeNode ReturnType,Scope Father) {
        super(Father);
        this.returnType = ReturnType;
    }

    public TypeNode getReturnType() {
        return this.returnType;
    }
}
