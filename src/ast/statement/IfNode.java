package ast.statement;

import ast.Visitor;
import ast.expressions.ExpressionNode;
import scope.Scope;
import tools.Position;

public class IfNode extends StatementNode{
    public ExpressionNode condition;
    public StatementNode one,two;

    public Scope oneScope,twoScope;

    public IfNode(Position pos,ExpressionNode cond,StatementNode One,StatementNode Two) {
        super(pos);
        this.condition = cond;
        this.one = One;
        this.two = Two;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
