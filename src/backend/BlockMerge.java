package backend;

import asm.Block;

import java.util.HashMap;

public class BlockMerge {

    HashMap<Block, Block> blockMoveMap = new HashMap<>();
    public void runOnModule(asm.asmModule module) {
        module.funcs.forEach(this::runOnFunc);
    }

    public void runOnFunc(asm.Function func) {
        blockMoveMap.clear();
        for (var pred : func.blocks) {
            pred = getAlias(pred);
            if (pred.nexts.size() != 1)
                continue;
            var succ = pred.nexts.get(0);
            if (succ.prevs.size() != 1)
                continue;
            pred.instructions.remove(pred.instructions.size() - 1);
            pred.instructions.addAll(succ.instructions);
            pred.nexts.clear();
            pred.nexts.addAll(succ.nexts);
            blockMoveMap.put(succ, pred);
        }
        func.blocks.removeIf(block -> blockMoveMap.containsKey(block));
    }

    asm.Block getAlias(asm.Block block) {
        var a = blockMoveMap.get(block);
        if (a == null)
            return block;
        a = getAlias(a);
        blockMoveMap.put(block, a);
        return a;
    }
}
