package backend;

import asm.Block;

import java.util.ArrayList;
import java.util.HashSet;

public class BlockReorder {
    public void runOnModule(asm.asmModule module) {
        module.funcs.forEach(this::runOnFunc);
    }

    public void runOnFunc(asm.Function func) {
        visited.clear();
        reordered = new ArrayList<>();
        dfs(func.entry);
        func.blocks = reordered;
    }

    HashSet<Block> visited = new HashSet<>();
    ArrayList<asm.Block> reordered;

    void dfs(asm.Block block) {
        if (visited.contains(block)) {
            return;
        }
        visited.add(block);
        reordered.add(block);
        for (var succ : block.nexts) {
            dfs(succ);
        }
    }
}
