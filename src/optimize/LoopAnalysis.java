package optimize;

import ir.BasicBlock;
import ir.Function;

import java.util.*;

public class LoopAnalysis {
    Function curFunc;

    public void runOnFunc(ir.Function func) {
        curFunc = func;
        new CFG().runOnFunc(func);
        new DominateTree(false).runOnFunc(func);
        func.rootLoops.clear();
        for (var block : func.blocks) {
            block.LoopDepth = 0;
        }

        findNaturalLoops();

        visited.clear();
        buildLoopNestTree(func.entry);
    }

    void findNaturalLoops() {
        for (var block : curFunc.blocks) {
            for (var suc : block.next) {
                if (suc.node.isDominatorOf(block.node)) {
                    buildNaturalLoop(suc, block);
                    break;
                }
            }
        }
    }

    HashMap<BasicBlock, Loop> headToLoop = new HashMap<>();

    void buildNaturalLoop(BasicBlock head, BasicBlock tail) {
        var loop = headToLoop.get(head);
        if (loop == null) {
            loop = new Loop(head);
            headToLoop.put(head, loop);
        }
        loop.tails.add(tail);
        loop.blocks.add(head);
        loop.blocks.add(tail);

        Queue<BasicBlock> queue = new LinkedList<>();
        queue.offer(tail);
        while (!queue.isEmpty()) {
            var block = queue.poll();
            for (var pre : block.prev) {
                if (loop.blocks.contains(pre))
                    continue;
                loop.blocks.add(pre);
                queue.offer(pre);
            }
        }
    }

    Stack<Loop> loopStack = new Stack<>();
    HashSet<BasicBlock> visited = new HashSet<>();

    Loop topLoop() {
        return loopStack.isEmpty() ? null : loopStack.peek();
    }

    void buildLoopNestTree(BasicBlock block) {
        if (visited.contains(block))
            return;
        visited.add(block);

        var outerLoop = topLoop();
        while (outerLoop != null && !outerLoop.blocks.contains(block)) {
            loopStack.pop();
            outerLoop = topLoop();
        }

        var loop = headToLoop.get(block);
        if (loop != null) {
            if (outerLoop != null) {
                loop.outerLoop = outerLoop;
                outerLoop.innerLoops.add(loop);
            } else {
                curFunc.rootLoops.add(loop);
            }
            loopStack.push(loop);
        }

        block.LoopDepth = loopStack.size();

        for (var suc : block.next) {
            buildLoopNestTree(suc);
        }
    }
}
