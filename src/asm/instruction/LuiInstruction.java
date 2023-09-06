package asm.instruction;

import asm.Block;
import asm.asmVisitor;
import asm.operand.Imm;
import asm.operand.SimpleReg;

public class LuiInstruction extends BasicInstruction {
    public SimpleReg rd;
    public Imm imm;

    public LuiInstruction(SimpleReg Rd, Imm Imm, Block Father) {
        super(Father);
        this.rd = Rd;
        this.imm = Imm;
    }

    public String toString() {
        return String.format("lui %s, %s", rd, imm);
    }

    public void accept(asmVisitor visitor) {
        visitor.visit(this);
    }
}
