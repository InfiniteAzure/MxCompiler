package ir.constant;

import ir.type.IntType;
import ir.type.PointerType;

public class NullConst extends Constant {
    public NullConst() {
        super(new PointerType(new IntType(32, false)),null);
    }

    public String name() {
        return "nullptr";
    }
}
