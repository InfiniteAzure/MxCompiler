package ast;

import tools.Position;

import java.util.ArrayList;

public class ClassNode extends ASTnode {
    public String name;
    public ArrayList<ASTnode> definitions = new ArrayList<>();

    public ClassNode(Position pos,String Name) {
        super(pos);
        this.name = Name;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}