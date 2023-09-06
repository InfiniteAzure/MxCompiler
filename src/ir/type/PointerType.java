package ir.type;

public class PointerType extends BasicType{
    public BasicType element;

    public PointerType(BasicType base) {
        this.element = base;
    }

    public int size() {
        return 4;
    }

    public String toString() {
        return this.element.toString() + "*";
    }
}
