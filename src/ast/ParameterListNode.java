package ast;

import java.util.ArrayList;

import tools.Position;

public class ParameterListNode extends ASTnode{
    public ArrayList<ParameterNode> parameters = new ArrayList<>();

    public ParameterListNode(Position pos) {
        super(pos);
    }

    public void add(ParameterNode parameter) {
        this.parameters.add(parameter);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}