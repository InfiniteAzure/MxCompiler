package ast.statement;

import ast.Visitor;
import tools.Position;

public class BreakNode extends StatementNode{
    public BreakNode(Position pos) {
        super(pos);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
