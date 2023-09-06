package asm.operand;

public class Relocation extends Imm{
    public enum RelocationType {
        hi, lo
    };

    public RelocationType type;
    public GlobalObject object;

    public Relocation(GlobalObject obj, RelocationType type) {
        super(0);
        this.object = obj;
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("%%%s(%s)",type, object);
    }
}
