package ir;

import ir.Instructions.AllocaInstruction;
import ir.Instructions.Basic;
import ir.Instructions.BrInstruction;
import ir.Instructions.PhiInstruction;
import ir.type.LabelType;
import optimize.DTNode;

import java.util.ArrayList;
import java.util.LinkedList;

public class BasicBlock extends Value {
    public LinkedList<Basic> instructions = new LinkedList<>();
    public LinkedList<PhiInstruction> phiInstructions = new LinkedList<>();
    public Function father;

    public ArrayList<BasicBlock> prev = new ArrayList<>(), next = new ArrayList<>();

    public DTNode node = new DTNode(this);

    public boolean terminated = false;

    public int LoopDepth;

    public BasicBlock(String Name, Function Father) {
        super(new LabelType(), Name);
        this.father = Father;
        if (Father != null) {
            Father.blocks.add(this);
        }
    }

    public void addInstruction(Basic Instruction) {
        if (Instruction instanceof AllocaInstruction i) {
            this.addAllocaInstruction(i);
        } else {
            if (!this.terminated) {
                this.instructions.add(Instruction);
                if (Instruction.isTerminator()) {
                    this.terminated = true;
                }
            }
        }
    }

    public void addBeforeTerminator(Basic Instruction) {
        instructions.add(instructions.size() - 1, Instruction);
        Instruction.father = this;
    }

    public void addAllocaInstruction(AllocaInstruction allocaInstruction) {
        for (int i = 0; i < instructions.size(); ++i) {
            var inst = instructions.get(i);
            if (!(inst instanceof AllocaInstruction)) {
                instructions.add(i, allocaInstruction);
                return;
            }
        }
        instructions.add(allocaInstruction);
    }

    public void redirectPrev(BasicBlock oldPrev, BasicBlock newPrev) {
        this.prev.remove(oldPrev);
        this.prev.add(newPrev);
        for (var phi : this.phiInstructions) {
            phi.replaceOp(oldPrev, newPrev);
        }
    }

    public void redirectNext(BasicBlock oldNext, BasicBlock newNext) {
        this.next.remove(oldNext);
        this.next.add(newNext);
        for (var inst : this.instructions) {
            if (inst instanceof BrInstruction br) {
                br.replaceOp(oldNext, newNext);
            }
        }
    }

    public String name() {
        return "%" + this.name;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    public String toString() {
        return name();
    }


}
