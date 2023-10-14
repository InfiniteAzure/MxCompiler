package optimize;

import ir.BasicBlock;
import ir.IRBuilder;
import ir.Instructions.BrInstruction;

import java.util.ArrayList;

public class LICM {
    IRBuilder builder;

    public LICM(IRBuilder irBuilder) {
        this.builder = irBuilder;
    }

    public void runOnModule(ir.Module module) {
        module.functions.forEach(this::runOnFunc);
    }

    public void runOnFunc(ir.Function func) {
        new CFG().runOnFunc(func);
        new LoopAnalysis().runOnFunc(func);
        func.rootLoops.forEach(this::runOnLoop);
    }

    void createPreHeader(Loop loop) {
        var preHeader = new BasicBlock(
                builder.rename("preheader"), loop.header.father);
        loop.preHeader = preHeader;

        // to avoid ConcurrentModificationException
        var headerPrevs = new ArrayList<BasicBlock>(loop.header.prev);
        for (var pre : headerPrevs) {
            if (!loop.tails.contains(pre)) {
                preHeader.prev.add(pre);
                loop.header.prev.remove(pre);

                pre.redirectNext(loop.header, preHeader);
                loop.header.redirectPrev(pre, preHeader);
            }
        }

        new BrInstruction(loop.header, preHeader);
        preHeader.next.add(loop.header);

        if (loop.header.father.entry == loop.header) {
            loop.header.father.entry = preHeader;
        }

        var outerloop = loop.outerLoop;
        while (outerloop != null) {
            outerloop.blocks.add(loop.preHeader);
            outerloop = outerloop.outerLoop;
        }
    }

    public void runOnLoop(Loop loop) {
        loop.innerLoops.forEach(this::runOnLoop);

        createPreHeader(loop);
        boolean changed = true;
        while (changed) {
            changed = false;
            for (var block : loop.blocks) {
                var iter = block.instructions.listIterator();
                while (iter.hasNext()) {
                    var inst = iter.next();
                    if (loop.isInvariant(inst)) {
                        changed = true;
                        iter.remove();
                        loop.preHeader.addBeforeTerminator(inst);
                    }
                }
            }
        }
    }
}
