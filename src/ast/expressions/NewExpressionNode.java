package ast.expressions;

import ast.TypeNode;
import ast.Visitor;
import tools.Position;

import java.util.ArrayList;

public class NewExpressionNode extends ExpressionNode{
    public ArrayList<ExpressionNode> sizes = new ArrayList<>();

    public NewExpressionNode(Position pos,TypeNode Type) {
        super(pos,Type,false);

    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
