package ir.type;

import java.util.ArrayList;

public class FunctionType extends BasicType{
    public BasicType Return;
    public ArrayList<BasicType> parameters = new ArrayList<>();

    public FunctionType(BasicType returnType) {
        this.Return = returnType;
    }

    public int size() {
        return 0;
    }

    public String toString() {
        return null;
    }
}
