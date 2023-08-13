package ast.statement;

import ast.Visitor;
import tools.Position;

public class ContinueNode extends StatementNode{
    public ContinueNode(Position pos){
        super(pos);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
