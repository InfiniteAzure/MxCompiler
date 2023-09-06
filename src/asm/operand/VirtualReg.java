package asm.operand;

public class VirtualReg extends SimpleReg {
    public static int VirtualRegCount = 0;
    public int index, size;

    public VirtualReg(int size) {
        this.index = VirtualRegCount;
        this.size = size;
        VirtualRegCount++;
    }
}

