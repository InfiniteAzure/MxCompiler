package ir.constant;

import ir.type.ArrayType;
import ir.type.IntType;
import ir.type.PointerType;

public class StringConst extends Constant {
    public String itself;

    public StringConst(String Name, String Itself) {
        super(new PointerType(new ArrayType(new IntType(8, false), Itself.length() + 1)), Name);
        this.itself = Itself + '\0';
    }

    public String toString() {
        return String.format("%s = private unnamed_addr constant %s c\"%s\"",
                this.name(), ((PointerType) this.type).element, this.escaped());
    }

    public String escaped() {
        return this.itself.replace("\\", "\\5C")
                .replace("\0", "\\00")
                .replace("\n", "\\0A")
                .replace("\t", "\\09")
                .replace("\"", "\\22");
    }


}
