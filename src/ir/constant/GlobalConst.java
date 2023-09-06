package ir.constant;

import ir.Value;
import ir.type.BasicType;
import ir.type.PointerType;

public class GlobalConst extends Constant{
    public Value init;

    public GlobalConst(BasicType Type, String Name) {
        super(new PointerType(Type),Name);
    }

    public String toString() {
        var elementType = ((PointerType) this.type).element;
        String initial;
        if (this.init == null) {
            initial = "noinitializer";
        } else {
            initial = this.init.name();
        }
        return String.format("%s = global %s %s, align %d",this.name(), elementType, initial, this.type.size());
    }
}
