package backend;

import asm.Block;
import asm.Function;
import asm.Pass;
import asm.asmVisitor;
import asm.instruction.*;
import asm.operand.Imm;
import asm.operand.Stack;

public class StackAllocator implements asmVisitor, Pass {

    Function tempFunc;

    private Imm calcStackOff(Stack s) {
        int offset = switch (s.type) {
            case spill -> tempFunc.spilledArg + tempFunc.allocaCnt + s.value;
            case alloca -> tempFunc.spilledArg + s.value;
            case putArg -> s.value;
            case getArg -> tempFunc.totalStack + s.value;
            case decSp -> -tempFunc.totalStack;
            case incSp -> tempFunc.totalStack;
        };
        return new Imm(offset * 4);
    }

    public void runOnModule(asm.asmModule module) {
        module.funcs.forEach(this::runOnFunc);
    }

    public void runOnFunc(asm.Function func) {
        tempFunc = func;
        func.totalStack = func.spilledReg + func.allocaCnt + func.spilledArg;
        func.blocks.forEach(this::runOnBlock);
    }

    public void runOnBlock(Block block) {
        block.instructions.forEach(x -> x.accept(this));
    }

    public void visit(LoadInstruction inst) {
        if (inst.offset instanceof Stack s) {
            inst.offset = calcStackOff(s);
        }
    }

    public void visit(StoreInstruction inst) {
        if (inst.offset instanceof Stack s) {
            inst.offset = calcStackOff(s);
        }
    }

    public void visit(ITypeInstruction inst) {
        if (inst.imm instanceof Stack s) {
            inst.imm = calcStackOff(s);
        }
    }

    //only instructions above need to calculate offset
    public void visit(BeqzInstruction inst) {}
    public void visit(CallInstruction inst) {}
    public void visit(JumpInstruction inst) {}
    public void visit(LiInstruction inst) {}
    public void visit(LuiInstruction inst) {}
    public void visit(MvInstruction inst) {}
    public void visit(RetInstruction inst) {}
    public void visit(RTypeInstruction inst) {}
}
