package ast;

import ast.statement.BlockNode;
import scope.FunctionScope;
import tools.Position;

public class FunctionNode extends ASTnode{

    public String name;
    public TypeNode returnType;
    public ParameterListNode parameterlist;
    public BlockNode block;
    public FunctionScope func;

    public FunctionNode(Position pos, String Name, TypeNode Return, BlockNode Block,ParameterListNode Parameterlist) {
        super(pos);
        this.name = Name;
        this.returnType = Return;
        this.parameterlist = Parameterlist;
        this.block = Block;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}