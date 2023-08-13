package ast.statement;

import ast.Visitor;
import ast.expressions.ExpressionNode;
import scope.LoopScope;
import tools.Position;

public class ForNode extends StatementNode{
    public VariableDefineNode initVariable;
    public ExpressionNode one, two, three;
    public StatementNode body;

    public LoopScope scope;

    public ForNode(VariableDefineNode initVar, ExpressionNode initExpr, ExpressionNode condition, ExpressionNode increase, StatementNode Body, Position pos) {
        super(pos);
        this.initVariable = initVar;
        this.one = initExpr;
        this.two = condition;
        this.three = increase;
        this.body = Body;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
