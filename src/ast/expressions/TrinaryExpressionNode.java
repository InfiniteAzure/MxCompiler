package ast.expressions;

import ast.Visitor;
import tools.Position;

public class TrinaryExpressionNode extends ExpressionNode{
    public ExpressionNode condition;
    public ExpressionNode first;
    public ExpressionNode second;

    public TrinaryExpressionNode(Position pos,ExpressionNode Condition,ExpressionNode First,ExpressionNode Second) {
        super(pos,First.type,false);
        this.condition = Condition;
        this.first = First;
        this.second = Second;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
