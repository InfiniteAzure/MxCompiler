package optimize;

import ir.BasicBlock;
import ir.Instructions.MoveInstruction;

public class PhiElimination {
    // TODO: Is this right?
    public void runOnFunc(ir.Function func) {
        for (var block : func.blocks) {
            for (var phi : block.phiInstructions) {
                for (int i = 0; i < phi.Op.size(); i += 2) {
                    var val = phi.Op.get(i);
                    var fromBlock = (BasicBlock) phi.Op.get(i + 1);
                    var mv = new MoveInstruction(phi, val, null);
                    fromBlock.addBeforeTerminator(mv);
                }
            }
        }
    }

    public void runOnModule(ir.Module module) {
        module.functions.forEach(this::runOnFunc);
    }
}

