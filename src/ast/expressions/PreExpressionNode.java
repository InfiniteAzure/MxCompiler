package ast.expressions;

import ast.Visitor;
import tools.Position;

public class PreExpressionNode extends ExpressionNode{
        public ExpressionNode expr;
        public String op;

        public PreExpressionNode(Position pos, ExpressionNode expr, String op) {
            super(pos,expr.type,false);
            this.expr = expr;
            this.op = op;
        }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}

