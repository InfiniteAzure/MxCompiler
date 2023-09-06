package ir.constant;

import ir.type.IntType;

public class IntConst extends Constant{
    public int itself;

    public IntConst(int Value,int size) {
        super(new IntType(size,false),null);
        this.itself = Value;
    }

    public String name() {
        return String.format("%d",this.itself);
    }
}
