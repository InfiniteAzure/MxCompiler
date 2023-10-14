package asm;

import asm.instruction.*;

public interface asmVisitor {
    public void visit(BeqzInstruction inst);
    public void visit(CallInstruction inst);
    public void visit(ITypeInstruction inst);
    public void visit(JumpInstruction inst);
    public void visit(LiInstruction inst);
    public void visit(LuiInstruction inst);
    public void visit(LoadInstruction inst);
    public void visit(MvInstruction inst);
    public void visit(RetInstruction inst);
    public void visit(RTypeInstruction inst);
    public void visit(StoreInstruction inst);
    public void visit(BrInstruction inst);
}
