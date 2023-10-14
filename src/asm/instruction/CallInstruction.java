package asm.instruction;

import asm.Block;
import asm.Function;
import asm.asmVisitor;
import asm.operand.PhysicalReg;
import asm.operand.SimpleReg;

import java.util.HashSet;

public class CallInstruction extends BasicInstruction{
    public Function func;

    public CallInstruction(Function Func, Block Father) {
        super(Father);
        this.func = Func;
    }

    public String toString() {
        return "call " + func.label;
    }

    public void accept(asmVisitor visitor) {
        visitor.visit(this);
    }

    public HashSet<SimpleReg> uses() {
        var ret = new HashSet<SimpleReg>();
        for (int i = 0; i < func.args.size() && i < 8; ++i) {
            ret.add(PhysicalReg.regA(i));
        }
        return ret;
    }

    public HashSet<SimpleReg> defs() {
        return new HashSet<>(PhysicalReg.Caller);
    }

    public void replaceUse(SimpleReg oldReg, SimpleReg newReg) {
    }

    public void replaceDef(SimpleReg oldReg, SimpleReg newReg) {
    }
}
