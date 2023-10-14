package ir.type;

public class IntType extends BasicType {
    public int memorySize;
    public boolean isBool;

    public IntType(int Size, boolean IsBool) {
        this.memorySize = Size;
        this.isBool = IsBool;
    }

    public int size() {
        return (this.memorySize - 1) / 8 + 1;
    }

    public String toString() {
        return String.format("i%d",this.memorySize);
    }
}
