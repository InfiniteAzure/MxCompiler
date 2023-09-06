package ir.type;

public class ArrayType extends BasicType{
    public BasicType elements;
    public int size;

    public ArrayType(BasicType Elements,int Size) {
        this.elements = Elements;
        this.size = Size;
    }

    public int size() {
        return elements.size() * size;
    }

    public String toString() {
        return String.format("[%d x %s]",size, elements.toString());
    }
}
