package asm;

import asm.instruction.BasicInstruction;
import asm.operand.SimpleReg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class Block extends Operand {
    public String label;
    public LinkedList<BasicInstruction> instructions = new LinkedList<>();

    public ArrayList<Block> prevs = new ArrayList<>(), nexts = new ArrayList<>();
    public HashSet<SimpleReg> liveIn = new HashSet<>(), liveOut = new HashSet<>();
    public int loopDepth;

    public Block(String label, int loopDepth) {
        this.label = label;
        this.loopDepth = loopDepth;
    }

    public Block(String Label) {
        this.label = Label;
    }

}
