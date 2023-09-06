package ir;

import ir.Instructions.*;

public interface IRVisitor {
    public void visit(ir.Module module);
    public void visit(Function function);
    public void visit(BasicBlock block);
    public void visit(AllocaInstruction inst);
    public void visit(BinaryInstruction inst);
    public void visit(BitCastInstruction inst);
    public void visit(BrInstruction inst);
    public void visit(CallInstruction inst);
    public void visit(GetElementPtrInstruction inst);
    public void visit(IcmpInstruction inst);
    public void visit(LoadInstruction inst);
    public void visit(ReturnInstruction inst);
    public void visit(StoreInstruction inst);
    public void visit(TruncInstruction inst);
    public void visit(ZextInstruction inst);
}
