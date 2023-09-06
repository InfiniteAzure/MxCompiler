package ir;

import ir.type.BasicType;

import java.util.ArrayList;

public class User extends Value{
    public ArrayList<Value>Op = new ArrayList<>();
    public User(BasicType type, String name) {
        super(type,name);
    }

    public void addOp(Value value) {
        //value.users.add(this);
        Op.add(value);
    }

}
