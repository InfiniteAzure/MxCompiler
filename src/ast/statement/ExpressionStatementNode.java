package ast.statement;

import ast.Visitor;
import ast.expressions.ExpressionNode;
import tools.Position;

public class ExpressionStatementNode extends StatementNode{
    public ExpressionNode expr;

    public ExpressionStatementNode(Position pos,ExpressionNode Expr) {
        super(pos);
        this.expr = Expr;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
