package ast.statement;

import ast.Visitor;
import ast.expressions.ExpressionNode;
import tools.Position;

public class ReturnNode extends StatementNode{
    public ExpressionNode returnVal;

    public ReturnNode(Position pos,ExpressionNode Return) {
        super(pos);
        this.returnVal = Return;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
