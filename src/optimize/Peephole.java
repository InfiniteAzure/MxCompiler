package optimize;

import ir.BasicBlock;
import ir.Instructions.*;
import ir.Value;
import ir.constant.IntConst;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Peephole {
    int Size = 10;

    public void runOnModule(ir.Module module) {
        module.functions.forEach(this::runOnFunc);
    }

    public void runOnFunc(ir.Function func) {
        func.blocks.forEach(this::runOnBlock);
        func.blocks.forEach(this::runOnBlock);
        func.blocks.forEach(this::runOnBlock);
    }

    public void runOnBlock(BasicBlock block) {
        var peephole = new LinkedList<Basic>();
        var removed = new HashSet<Basic>();
        for (var inst : block.instructions) {
            for (var prev : peephole) {
                if (isSame(prev, inst)) {
                    inst.replaceAllUse(prev);
                    removed.add(inst);
                }
                if (inst instanceof LoadInstruction load &&
                        prev instanceof LoadInstruction prevLoad &&
                        eq(load.Op.get(0), prevLoad.Op.get(0)) &&
                        noStoreAfter(prevLoad, peephole)) {
                    inst.replaceAllUse(prev);
                    removed.add(inst);
                }
            }
            if (!removed.contains(inst))
                peephole.add(inst);
            if (peephole.size() > Size) {
                peephole.pop();
            }
        }
        var iter = block.instructions.listIterator();
        while (iter.hasNext()) {
            var inst = iter.next();
            if (removed.contains(inst))
                iter.remove();
        }
    }

    boolean eq(Value a, Value b) {
        if (a == b)
            return true;
        if (a instanceof IntConst c1 && b instanceof IntConst c2) {
            return c1.itself == c2.itself;
        }
        return false;
    }

    boolean isSame(Basic prev, Basic next) {
        if (prev instanceof BinaryInstruction b1 && next instanceof BinaryInstruction b2) {
            if (b1.op.equals(b2.op) && eq(b1.Op.get(0), b2.Op.get(0)) && eq(b1.Op.get(1), b2.Op.get(1))) {
                return true;
            }
        }
        if (prev instanceof GetElementPtrInstruction g1 &&
                next instanceof GetElementPtrInstruction g2) {
            if (g1.Op.size() != g2.Op.size()) {
                return false;
            }

            for (int i = 0; i < g1.Op.size(); ++i) {
                if (!eq(g1.Op.get(i), g2.Op.get(i)))
                    return false;
            }
            return true;
        }
        return false;
    }

    boolean noStoreAfter(LoadInstruction load, List<Basic> peephole) {
        boolean inRange = false;
        for (var inst : peephole) {
            if (inRange && inst instanceof StoreInstruction store) {
                return false;
            }
            if (inst == load) {
                inRange = true;
            }
        }
        return true;
    }
}
