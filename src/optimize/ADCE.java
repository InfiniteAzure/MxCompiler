package optimize;

import ir.BasicBlock;
import ir.Instructions.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class ADCE {
    public ADCE() {}

    public void runOnModule(ir.Module module) {
        module.functions.forEach(this::runOnFunc);
    }

    Queue<Basic> workList = new LinkedList<>();

    HashSet<Basic> liveInst = new HashSet<>();
    HashSet<BasicBlock> liveBlock = new HashSet<>();

    public void runOnFunc(ir.Function func) {
        new CFG().runOnFunc(func);
        new DominateTree(true).runOnFunc(func);
        markLive(func);

        for (var block : func.blocks) {
            var iter1 = block.phiInstructions.iterator();
            while (iter1.hasNext()) {
                var inst = iter1.next();
                if (liveInst.contains(inst))
                    continue;
                iter1.remove();
            }
            var iter2 = block.instructions.listIterator();
            while (iter2.hasNext()) {
                var inst = iter2.next();
                if (liveInst.contains(inst))
                    continue;
                iter2.remove();
                if (inst.isTerminator()) {
                    var dest = getLivePDom(block);
                    iter2.add(new BrInstruction(dest, null));
                }
            }
        }
    }

    public void markLive(ir.Function func) {
        for (var block : func.blocks) {
            for (var inst : block.instructions)
                if (isAlwaysLive(inst)) {
                    markInstLive(inst);
                }
        }

        while (!workList.isEmpty()) {
            var inst = workList.poll();
            markBlockLive(inst.father);

            for (var operand : inst.Op) {
                if (operand instanceof Basic i) {
                    markInstLive(i);
                } else if (operand instanceof BasicBlock b) {
                    markTerminatorLive(b);
                }
            }
        }
    }

    public boolean isAlwaysLive(Basic inst) {
        return inst instanceof ReturnInstruction || inst instanceof StoreInstruction || inst instanceof CallInstruction;
    }

    public void markInstLive(Basic inst) {
        if (liveInst.contains(inst))
            return;
        liveInst.add(inst);
        workList.offer(inst);
    }

    public void markTerminatorLive(BasicBlock block) {
        markInstLive(block.instructions.getLast());
    }

    public void markBlockLive(BasicBlock block) {
        if (liveBlock.contains(block)) {
            return;
        }
        liveBlock.add(block);
        for (var dependence : block.node.domFrontier) {
            markTerminatorLive(dependence.block);
        }
    }

    public BasicBlock getLivePDom(BasicBlock block) {
        var dom = block.node.idom;
        while (!liveBlock.contains(dom.block)) {
            dom = dom.idom;
        }
        return dom.block;
    }


}
