package optimize;

import ir.BasicBlock;
import ir.Instructions.BrInstruction;

public class CFG {
    public void runOnModule(ir.Module module) {
        module.functions.forEach(this::runOnFunc);
    }

    public void runOnFunc(ir.Function func) {
        for (var block : func.blocks) {
            block.prev.clear();
            block.next.clear();
        }

        for (var block : func.blocks) {
            var terminator = block.instructions.get(block.instructions.size() - 1);
            if (terminator instanceof BrInstruction b) {
                if (b.Op.size() == 1) {
                    addEdge(block, (BasicBlock) b.Op.get(0));
                } else {
                    addEdge(block, (BasicBlock)b.Op.get(1));
                    addEdge(block, (BasicBlock)b.Op.get(2));
                }
            }
        }
    }

    private void addEdge(ir.BasicBlock prev, ir.BasicBlock next) {
        prev.next.add(next);
        next.prev.add(prev);
    }
}
