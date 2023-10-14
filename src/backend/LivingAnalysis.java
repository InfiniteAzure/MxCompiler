package backend;

import asm.Block;
import asm.Pass;
import asm.asmModule;
import asm.operand.SimpleReg;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

public class LivingAnalysis implements Pass {
    // only func pass is used
    // not quite understand what hushu says, but it works!
    HashMap<Block, LANode> nodeMap = new HashMap<>();

    HashMap<Block, HashSet<SimpleReg>> blockUsesMap = new HashMap<>(), blockDefsMap = new HashMap<>();
    PriorityQueue<LANode> queue = new PriorityQueue<>();

    @Override
    public void runOnFunc(asm.Function func) {
        // clear old data
        for (var block : func.blocks) {
            block.liveIn.clear();
            block.liveOut.clear();
        }
        func.blocks.forEach(this::combineBlock);

        dfn = 1;
        dfs(func.exit);

        queue.offer(nodeMap.get(func.exit));
        while (!queue.isEmpty()) {
            var node = queue.poll();
            while (!queue.isEmpty() && queue.peek().equals(node)) {
                queue.poll();
            }
            var block = node.block;

            var newLiveOut = new HashSet<SimpleReg>();
            block.nexts.forEach(suc -> newLiveOut.addAll(suc.liveIn));

            var newLiveIn = new HashSet<SimpleReg>(newLiveOut);
            newLiveIn.removeAll(blockDefsMap.get(block));
            newLiveIn.addAll(blockUsesMap.get(block));

            if (!newLiveIn.equals(block.liveIn) || !newLiveOut.equals(block.liveOut)) {
                block.liveIn.addAll(newLiveIn);
                block.liveOut.addAll(newLiveOut);
                block.prevs.forEach(pre -> queue.offer(nodeMap.get(pre)));
            }
        }
    }

    private int dfn;

    private void dfs(Block block) {
        nodeMap.put(block, new LANode(dfn, block));
        dfn++;
        for (var b : block.prevs) {
            if (nodeMap.containsKey(b))
                continue;
            dfs(b);
        }
    }

    public void combineBlock(asm.Block block) {
        var blockUses = new HashSet<SimpleReg>();
        var blockDefs = new HashSet<SimpleReg>();
        for (var i : block.instructions) {
            i.uses().forEach(use -> {
                if (!blockDefs.contains(use))
                    blockUses.add(use);
            });
            blockDefs.addAll(i.defs());
        }
        blockUsesMap.put(block, blockUses);
        blockDefsMap.put(block, blockDefs);
    }

    public void runOnBlock(Block block) {}

    public void runOnModule(asmModule module) {}
}
