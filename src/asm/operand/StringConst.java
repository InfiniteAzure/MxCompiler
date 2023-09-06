package asm.operand;

public class StringConst extends GlobalObject {
    public String val;

    public StringConst(String Name, String value) {
        this.name = Name;
        this.val = value;
    }

    public String escaped() {
        return val.replace("\\", "\\\\")
                .replace("\n", "\\n")
                .replace("\t", "\\t")
                .replace("\"", "\\\"")
                .replace("\0", "");
    }

}