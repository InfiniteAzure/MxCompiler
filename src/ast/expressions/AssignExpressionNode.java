package ast.expressions;

import ast.Visitor;
import tools.Position;

public class AssignExpressionNode extends ExpressionNode {
    public ExpressionNode lhs, rhs;

    public AssignExpressionNode(Position pos, ExpressionNode Lhs,ExpressionNode Rhs) {
        super(pos,Lhs.type, false);
        this.lhs = Lhs;
        this.rhs = Rhs;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}