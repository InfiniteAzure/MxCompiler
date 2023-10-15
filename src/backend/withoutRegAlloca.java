package backend;

import asm.*;
import asm.instruction.*;
import asm.operand.PhysicalReg;
import asm.operand.SimpleReg;
import asm.operand.Stack;
import asm.operand.VirtualReg;

import java.util.LinkedList;

public class withoutRegAlloca implements Pass, asmVisitor {
    asm.asmModule module;
    asm.Function curFunc;
    asm.Block curBlock;

    private final PhysicalReg sp = PhysicalReg.reg("sp");
    private final PhysicalReg t0 = PhysicalReg.reg("t0");
    private final PhysicalReg t1 = PhysicalReg.reg("t1");

    private PhysicalReg regAllocRead(SimpleReg src, PhysicalReg reg) {
        if (src instanceof VirtualReg v) {
            if (src.stackOffset == null) {
                src.stackOffset = new Stack(curFunc.spilledReg, Stack.StackType.spill);
                curFunc.spilledReg++;
            }
            // TODO long offset lui
            new asm.instruction.LoadInstruction(4, reg, sp, src.stackOffset, curBlock);
            return reg;
        }
        return (PhysicalReg) src;
    }

    private PhysicalReg regAllocWrite(SimpleReg dest, PhysicalReg reg) {
        if (dest instanceof VirtualReg v) {
            if (dest.stackOffset == null) {
                dest.stackOffset = new Stack(curFunc.spilledReg, Stack.StackType.spill);
                curFunc.spilledReg++;
            }
            // TODO long offset lui
            new asm.instruction.StoreInstruction(4, reg, sp, dest.stackOffset, curBlock);
            return reg;
        }
        return (PhysicalReg) dest;
    }

    public void runOnModule(asmModule module) {
        module.funcs.forEach(this::runOnFunc);
    }

    public void runOnFunc(Function func) {
        curFunc = func;
        func.blocks.forEach(this::runOnBlock);
    }

    public void runOnBlock(Block block) {
        curBlock = block;
        var oldInsts = block.instructions;
        block.instructions = new LinkedList<>();
        oldInsts.forEach(x -> x.accept(this));
    }

    public void visit(BeqzInstruction inst) {
        inst.rs = regAllocRead(inst.rs, t0);
        curBlock.instructions.add(inst);
    }

    @Override
    public void visit(BrInstruction inst) {
        inst.rs1 = regAllocRead(inst.rs1, t0);
        inst.rs2 = regAllocRead(inst.rs2, t1);
        curBlock.instructions.add(inst);
    }

    public void visit(CallInstruction inst) {
        curBlock.instructions.add(inst);
    }

    @Override
    public void visit(ITypeInstruction inst) {
        inst.rs = regAllocRead(inst.rs, t0);
        curBlock.instructions.add(inst);
        inst.rd = regAllocWrite(inst.rd, t0);
    }

    @Override
    public void visit(JumpInstruction inst) {
        curBlock.instructions.add(inst);
    }

    @Override
    public void visit(LiInstruction inst) {
        curBlock.instructions.add(inst);
        inst.rd = regAllocWrite(inst.rd, t0);
    }

    @Override
    public void visit(LuiInstruction inst) {
        curBlock.instructions.add(inst);
        inst.rd = regAllocWrite(inst.rd, t0);
    }

    @Override
    public void visit(LoadInstruction inst) {
        inst.rs = regAllocRead(inst.rs, t0);
        curBlock.instructions.add(inst);
        inst.rd = regAllocWrite(inst.rd, t0);
    }

    @Override
    public void visit(MvInstruction inst) {
        inst.rs = regAllocRead(inst.rs, t0);
        curBlock.instructions.add(inst);
        inst.rd = regAllocWrite(inst.rd, t0);
    }

    @Override
    public void visit(RetInstruction inst) {
        curBlock.instructions.add(inst);
    }

    @Override
    public void visit(RTypeInstruction inst) {
        inst.rs1 = regAllocRead(inst.rs1, t0);
        inst.rs2 = regAllocRead(inst.rs2, t1);
        curBlock.instructions.add(inst);
        inst.rd = regAllocWrite(inst.rd, t0);
    }

    @Override
    public void visit(StoreInstruction inst) {
        inst.rs1 = regAllocRead(inst.rs1, t0);
        inst.rs2 = regAllocRead(inst.rs2, t1);
        curBlock.instructions.add(inst);
    }
}
