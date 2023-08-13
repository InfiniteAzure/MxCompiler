package ast;

import tools.Position;

public abstract class ASTnode {
    public Position pos;

    public ASTnode(Position pos) {
        this.pos = pos;
    }
    public abstract void accept(Visitor visitor);
}