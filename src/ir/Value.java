package ir;

import ir.type.BasicType;

import java.util.ArrayList;

public class Value {
    public BasicType type;
    public String name;
    public ArrayList<User> users = new ArrayList<>();

    public asm.Operand asm = null;

    public Value(BasicType Type,String Name) {
        this.type = Type;
        this.name = Name;
    }

    public void replaceAllUse(Value v) {
        for (var user : users) {
            var op = user.Op;
            for (int i = 0; i < op.size(); ++i) {
                if (op.get(i) == this) {
                    op.set(i, v);
                }
            }
            v.users.add(user);
        }
        users.clear();
    }

    public String name() {
        return this.name;
    }

    public String nameWithType() {
        return this.type + " " + this.name();
    }

}
