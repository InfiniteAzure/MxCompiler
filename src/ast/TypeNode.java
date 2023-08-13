package ast;

import Parser.MxParser.TypeContext;
import tools.Position;

public class TypeNode extends ASTnode {
    public String name;
    public boolean Array;
    public boolean Class;
    public int arraySize;

    public TypeNode(Position pos, boolean ClassCheck, String Name) {
        super(pos);
        name = Name;
        Array = false;
        Class = ClassCheck;
        arraySize = 0;
    }

    public TypeNode(TypeContext ctx) {
        this(new Position(ctx), ctx.Identifier() != null || ctx.getText().equals("string"), ctx.getText());
    }

    public TypeNode(TypeNode n) {
        super(n.pos);
        name = n.name;
        Array = n.Array;
        Class = n.Class;
        arraySize = n.arraySize;
    }

    public TypeNode(Position pos, boolean ClassCheck, String Name, int size) {
        super(pos);
        name = Name;
        Array = true;
        Class = ClassCheck;
        arraySize = size;
    }

    public boolean check(TypeNode comp) {
        if (!this.name.equals(comp.name) || this.Array != comp.Array || this.arraySize != comp.arraySize) {
            return false;
        }
        return true;
    }

    public boolean assignmentCheck(TypeNode comp) {
        if (this.name.equals("null")) {
            if (comp.Class || comp.Array) {
                return true;
            } else {
                return false;
            }
        }
        return this.check(comp);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }


}
