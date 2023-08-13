package ast.expressions;

import ast.Visitor;
import tools.Position;

import java.util.ArrayList;

public class FunctionCallNode extends ExpressionNode {
    public ExpressionNode function;
    public ArrayList<ExpressionNode> args = new ArrayList<>();

    public FunctionCallNode(ExpressionNode function, Position pos) {
        super(pos, null, false);
        this.function = function;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}