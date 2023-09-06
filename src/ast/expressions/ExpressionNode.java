package ast.expressions;

import ast.ASTnode;
import ast.FunctionNode;
import ast.TypeNode;
import ir.Value;
import tools.Position;

public abstract class ExpressionNode extends ASTnode {
    public TypeNode type;
    public boolean left;
    public boolean function;
    public FunctionNode func;

    public Value val, ptr;

    ExpressionNode(Position pos, TypeNode Type, boolean Left) {
        super(pos);
        this.left = Left;
        this.type = Type;
    }
}