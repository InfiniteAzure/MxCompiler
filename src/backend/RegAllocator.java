package backend;

import asm.*;
import asm.instruction.*;
import asm.operand.PhysicalReg;
import asm.operand.SimpleReg;
import asm.operand.Stack;
import asm.operand.VirtualReg;

import java.util.ArrayList;

public class RegAllocator implements asmVisitor, Pass {
    asmModule module;
    Function tempFunc;
    Block tempBlock;

    public PhysicalReg sp = PhysicalReg.regMap.get("sp");
    public PhysicalReg t0 = PhysicalReg.regMap.get("t0");
    public PhysicalReg t1 = PhysicalReg.regMap.get("t1");

    public PhysicalReg regAllocRead(SimpleReg src, PhysicalReg reg) {
        if (src instanceof VirtualReg v) {
            tempFunc.spilledReg = Math.max(tempFunc.spilledReg, v.index + 1);
            var offset = new Stack(v.index, Stack.StackType.spill);
            new asm.instruction.LoadInstruction(4, reg, sp, offset, tempBlock);
            return reg;
        }
        return (PhysicalReg) src;
    }

    public PhysicalReg regAllocWrite(SimpleReg dest, PhysicalReg reg) {
        if (dest instanceof VirtualReg v) {
            tempFunc.spilledReg = Math.max(tempFunc.spilledReg, v.index + 1);
            var offset = new Stack(v.index, Stack.StackType.spill);
            new asm.instruction.StoreInstruction(4, reg, sp, offset, tempBlock);
            return reg;
        }
        return (PhysicalReg) dest;
    }

    public void runOnModule(asmModule module) {
        module.funcs.forEach(this::runOnFunc);
    }

    public void runOnFunc(Function func) {
        tempFunc = func;
        func.blocks.forEach(this::runOnBlock);
    }

    public void runOnBlock(Block block) {
        tempBlock = block;
        var oldInsts = block.instructions;
        block.instructions = new ArrayList<>();
        oldInsts.forEach(x -> x.accept(this));
    }

    public void visit(BeqzInstruction inst) {
        inst.rs = regAllocRead(inst.rs, t0);
        tempBlock.instructions.add(inst);
    }

    public void visit(CallInstruction inst) {
        tempBlock.instructions.add(inst);
    }

    public void visit(ITypeInstruction inst) {
        inst.rs = regAllocRead(inst.rs, t0);
        tempBlock.instructions.add(inst);
        inst.rd = regAllocWrite(inst.rd, t0);
    }

    public void visit(JumpInstruction inst) {
        tempBlock.instructions.add(inst);
    }

    public void visit(LiInstruction inst) {
        tempBlock.instructions.add(inst);
        inst.rd = regAllocWrite(inst.rd, t0);
    }

    public void visit(LuiInstruction inst) {
        tempBlock.instructions.add(inst);
        inst.rd = regAllocWrite(inst.rd, t0);
    }

    public void visit(LoadInstruction inst) {
        inst.rs = regAllocRead(inst.rs, t0);
        tempBlock.instructions.add(inst);
        inst.rd = regAllocWrite(inst.rd, t0);
    }

    public void visit(MvInstruction inst) {
        inst.rs1 = regAllocRead(inst.rs1, t0);
        tempBlock.instructions.add(inst);
        inst.rd = regAllocWrite(inst.rd, t0);
    }

    public void visit(RetInstruction inst) {
        tempBlock.instructions.add(inst);
    }

    public void visit(RTypeInstruction inst) {
        inst.rs1 = regAllocRead(inst.rs1, t0);
        inst.rs2 = regAllocRead(inst.rs2, t1);
        tempBlock.instructions.add(inst);
        inst.rd = regAllocWrite(inst.rd, t0);
    }

    public void visit(StoreInstruction inst) {
        inst.rs1 = regAllocRead(inst.rs1, t0);
        inst.rs2 = regAllocRead(inst.rs2, t1);
        tempBlock.instructions.add(inst);
    }

}
