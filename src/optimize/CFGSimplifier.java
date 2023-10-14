package optimize;

import ir.BasicBlock;

import java.util.HashMap;
import java.util.HashSet;

public class CFGSimplifier {
    public void runOnModule(ir.Module module) {
        module.functions.forEach(this::runOnFunc);
    }

    public void runOnFunc(ir.Function func) {
        boolean change = true;
        while (change) {
            new CFG().runOnFunc(func);
            change = mergeBlock(func);
        }
        while (change) {
            new CFG().runOnFunc(func);
            change = removeDeadBlock(func);
        }
    }

    boolean removeDeadBlock(ir.Function func) {
        var deadBlocks = new HashSet<BasicBlock>();

        boolean changed = true;
        while (changed) {
            changed = false;
            for (var block : func.blocks) {
                if (block == func.entry)
                    continue;
                if (deadBlocks.contains(block))
                    continue;
                if (block.prev.isEmpty()) {
                    deadBlocks.add(block);
                    for (var suc : block.next) {
                        suc.prev.remove(block);
                    }
                    changed = true;
                }
            }
        }

        for (var block : deadBlocks) {
            func.blocks.remove(block);
            for (var suc : block.next) {
                suc.prev.remove(block);
                removePhiBranchIn(suc, block);
            }
            block.prev.clear();
            block.next.clear();
        }
        return deadBlocks.size() > 1;
    }

    public void removePhiBranchIn(BasicBlock block, BasicBlock removed) {
        var iter = block.phiInstructions.iterator();
        while (iter.hasNext()) {
            var phi = iter.next();
            phi.removeBr(removed);
            if (phi.Op.size() == 2) {
                iter.remove();
                phi.replaceAllUse(phi.Op.get(0));
            }
        }
    }

    boolean mergeBlock(ir.Function func) {
        blockMoveMap.clear();
        for (var pred : func.blocks) {
            pred = getAlias(pred);
            if (pred.next.size() != 1)
                continue;
            var succ = pred.next.get(0);
            if (succ.prev.size() != 1)
                continue;
            if (func.exit == succ)
                func.exit = pred;
            pred.instructions.remove(pred.instructions.size() - 1);
            assert succ.phiInstructions.isEmpty();
            for (var sucInst : succ.instructions) {
                sucInst.father = pred;
                pred.instructions.add(sucInst);
            }
            pred.next.clear();
            pred.next.addAll(succ.next);
            for (var suc : succ.next) {
                suc.redirectPrev(succ, pred);
            }
            blockMoveMap.put(succ, pred);
        }
        var iter = func.blocks.iterator();
        while (iter.hasNext()) {
            var block = iter.next();
            if (blockMoveMap.containsKey(block))
                iter.remove();
        }
        return blockMoveMap.size() != 0;
    }

    HashMap<BasicBlock, BasicBlock> blockMoveMap = new HashMap<>();

    BasicBlock getAlias(BasicBlock block) {
        var a = blockMoveMap.get(block);
        if (a == null)
            return block;
        a = getAlias(a);
        blockMoveMap.put(block, a);
        return a;
    }




}
