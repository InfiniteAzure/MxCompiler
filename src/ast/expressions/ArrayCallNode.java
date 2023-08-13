package ast.expressions;

import ast.Visitor;
import tools.Position;

public class ArrayCallNode extends ExpressionNode {
    public ExpressionNode array, index;

    public ArrayCallNode(ExpressionNode Array, ExpressionNode Index, Position pos) {
        super(pos, null,true);
        this.array = Array;
        this.index = Index;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}