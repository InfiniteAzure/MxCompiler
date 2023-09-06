package ir;

import ir.Instructions.AllocaInstruction;
import ir.Instructions.Basic;
import ir.type.LabelType;

import java.util.LinkedList;

public class BasicBlock extends Value {
    public LinkedList<Basic> instructions = new LinkedList<>();
    public Function father;

    public boolean terminated = false;

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

    public String name() {
        return "%" + this.name;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    ;

}
