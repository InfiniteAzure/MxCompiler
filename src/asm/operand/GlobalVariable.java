package asm.operand;

import asm.operand.GlobalObject;

public class GlobalVariable extends GlobalObject {
    public int initVal, size;

    public GlobalVariable(String Name, int init, int Size) {
        this.name = Name;
        this.initVal = init;
        this.size = Size;
    }
}
