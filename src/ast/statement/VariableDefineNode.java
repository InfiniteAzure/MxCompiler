package ast.statement;

import ast.TypeNode;
import ast.Visitor;
import tools.Position;

import java.util.ArrayList;

public class VariableDefineNode extends StatementNode {
    public TypeNode type;
    public ArrayList<SingleVariableDefineNode> defines = new ArrayList<>();

    public VariableDefineNode(Position pos, TypeNode Type) {
        super(pos);
        this.type = Type;
    }

    public void addVariable (SingleVariableDefineNode add) {
        defines.add(add);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
