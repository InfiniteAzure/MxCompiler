package ast.statement;

import ast.Visitor;
import scope.Scope;
import tools.Position;

import java.util.ArrayList;

public class BlockNode extends StatementNode{
    public ArrayList<StatementNode> statements = new ArrayList<>();

    public Scope scope;

    public BlockNode(Position pos) {
        super(pos);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
