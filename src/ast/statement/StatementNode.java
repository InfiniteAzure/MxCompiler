package ast.statement;

import ast.ASTnode;
import tools.Position;

public abstract class StatementNode extends ASTnode {
    public StatementNode(Position pos) {
        super(pos);
    }

}
