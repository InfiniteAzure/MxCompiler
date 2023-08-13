package ast.statement;

import ast.Visitor;
import ast.expressions.ExpressionNode;
import scope.LoopScope;
import tools.Position;

public class WhileNode extends StatementNode{
    public ExpressionNode condition;
    public StatementNode body;
    public LoopScope scope;

    public WhileNode(Position pos, ExpressionNode cond,StatementNode Body) {
        super(pos);
        this.condition = cond;
        this.body = Body;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
