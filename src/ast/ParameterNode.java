package ast;

import tools.Position;

public class ParameterNode extends ASTnode{
    public TypeNode type;
    public String name;

    public ParameterNode(Position pos,TypeNode Type,String Name) {
        super(pos);
        this.name = Name;
        this.type = Type;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}