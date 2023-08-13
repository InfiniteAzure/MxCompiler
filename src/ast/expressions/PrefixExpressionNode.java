package ast.expressions;

import ast.Visitor;
import tools.Position;

public class PrefixExpressionNode extends ExpressionNode{
    public ExpressionNode expr;
    public String op;

    public PrefixExpressionNode(Position pos, ExpressionNode Expr, String Op){
        super(pos,Expr.type,true);
        this.expr = Expr;
        this.op = Op;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }


}
