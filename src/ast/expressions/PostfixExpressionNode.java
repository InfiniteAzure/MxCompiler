package ast.expressions;

import ast.Visitor;
import tools.Position;

public class PostfixExpressionNode extends ExpressionNode{
    public ExpressionNode expr;
    public String op;

    public PostfixExpressionNode(Position pos,ExpressionNode Expr,String Op){
        super(pos,Expr.type,false);
        this.expr = Expr;
        this.op = Op;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
