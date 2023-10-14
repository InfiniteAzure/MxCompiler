package backend;

import asm.instruction.ITypeInstruction;
import asm.instruction.JumpInstruction;
import asm.operand.Relocation;

public class DIE {
    public void runOnModule(asm.asmModule module) {
        module.funcs.forEach(this::runOnFunc);
    }

    public void runOnFunc(asm.Function func) {
        for (int i = 0; i < func.blocks.size(); ++i) {
            var block = func.blocks.get(i);
            var nxtBlock = i + 1 < func.blocks.size() ? func.blocks.get(i + 1) : null;
            var iter = block.instructions.listIterator();
            while (iter.hasNext()) {
                // my IDEA_J gives me a WRONG hint here
                var inst = iter.next();
                if (inst instanceof ITypeInstruction i_inst
                        && (i_inst.op.equals("addi") || i_inst.op.equals("subi"))
                        && i_inst.rd == i_inst.rs && !(i_inst.imm instanceof Relocation)
                        && i_inst.imm.value == 0) {
                    iter.remove();
                }
            }
            if (block.instructions.isEmpty())
                continue;
            var t = block.instructions.getLast();
            if (t instanceof JumpInstruction j && j.dest == nxtBlock)
                block.instructions.remove(block.instructions.size() - 1);
        }
    }
}
