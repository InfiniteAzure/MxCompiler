package optimize;

import ir.BasicBlock;
import ir.Instructions.*;
import ir.Value;
import ir.constant.Constant;

import java.util.HashMap;
import java.util.HashSet;

public class Loop {
    public BasicBlock preHeader;
    public BasicBlock header;
    public HashSet<BasicBlock> tails = new HashSet<>();
    public HashSet<BasicBlock> blocks = new HashSet<>();

    public Loop outerLoop;
    public HashSet<Loop> innerLoops = new HashSet<>();

    public Loop(BasicBlock header) {
        this.header = header;
    }

    HashMap<Basic, Boolean> calcRecord = new HashMap<>();

    public boolean isInvariant(Value value) {
        if (value instanceof Constant)
            return true;
        if (value instanceof Basic inst)
            return isInvariant(inst);
        return false;
    }

    public boolean isInvariant(Basic inst) {
        var res = calcRecord.get(inst);
        if (res != null) {
            return res;
        }

        if (!blocks.contains(inst.father)) {
            res = true;
        } else if (inst instanceof ReturnInstruction || inst instanceof BrInstruction ||
                inst instanceof LoadInstruction || inst instanceof StoreInstruction ||
                inst instanceof CallInstruction) {
            return false;
        } else {
            res = true;
            for (var operand : inst.Op) {
                if (!this.isInvariant(operand)) {
                    res = false;
                    break;
                }
            }
        }
        calcRecord.put(inst, res);
        return res;
    }
}