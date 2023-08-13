package ast.expressions;

import ast.TypeNode;
import ast.Visitor;
import ast.statement.SingleVariableDefineNode;
import tools.Position;

public class SingleExpressionNode extends ExpressionNode{
    public String context;
    public SingleVariableDefineNode variable;

    public SingleExpressionNode(Position pos, String cont, TypeNode Type, boolean isLeft) {
        super(pos,Type,isLeft);
        this.context = cont;
    }

    public String substitute() {
        return context.substring(1, context.length() - 1)
                .replace("\\\"", "\"")
                .replace("\\n", "\n")
                .replace("\\t", "\t")
                .replace("\\\\", "\\");
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
