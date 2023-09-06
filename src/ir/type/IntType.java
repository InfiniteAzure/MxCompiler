package ir.type;

public class IntType extends BasicType {
    public int memorySize;
    public boolean isBool;

    public IntType(int Size, boolean IsBool) {
        this.memorySize = Size;
        this.isBool = IsBool;
    }

    public int size() {
        if (this.memorySize == 1) {
            return 1;
        }
        return this.memorySize / 8;
    }

    public String toString() {
        return String.format("i%d",this.memorySize);
    }
}
