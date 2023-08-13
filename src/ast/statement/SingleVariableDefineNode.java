package ast.statement;

import ast.TypeNode;
import ast.Visitor;
import ast.expressions.ExpressionNode;
import tools.Position;

public class SingleVariableDefineNode extends StatementNode{
    public TypeNode type;
    public String name;
    public ExpressionNode right;

    public SingleVariableDefineNode(Position pos, TypeNode Type,String Name,ExpressionNode Expr) {
        super(pos);
        this.type = Type;
        this.name = Name;
        this.right = Expr;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
