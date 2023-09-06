package asm.operand;

public class Stack extends Imm{

    public enum StackType {
        getArg, alloca, spill, putArg, decSp, incSp
    };

    public StackType type;

    public Stack(int offset, StackType type) {
        super(offset);
        this.type = type;
    }
}
