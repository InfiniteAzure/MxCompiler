package ast;

import tools.Position;

import java.util.ArrayList;

public class ProgramNode extends ASTnode {
    public ArrayList<ASTnode> definitions = new ArrayList<>();

    public ProgramNode(Position pos) {
        super(pos);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}