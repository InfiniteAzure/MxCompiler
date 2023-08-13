package ast;

import ast.statement.BlockNode;
import scope.FunctionScope;
import tools.Position;

public class ConstructorNode extends ASTnode{
    public String name;
    public BlockNode block;
    public FunctionScope func;

    public ConstructorNode(Position pos,String Name,BlockNode Block) {
        super(pos);
        this.name = Name;
        this.block = Block;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}