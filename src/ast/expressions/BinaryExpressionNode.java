package ast.expressions;

import ast.Visitor;
import tools.Position;

public class BinaryExpressionNode extends ExpressionNode {
    public ExpressionNode lhs, rhs;
    public String op;

    public BinaryExpressionNode(ExpressionNode Lhs, ExpressionNode Rhs, String Op, Position pos) {
        super(pos, Lhs.type, false);
        this.lhs = Lhs;
        this.rhs = Rhs;
        this.op = Op;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}