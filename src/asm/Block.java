package asm;

import asm.instruction.BasicInstruction;

import java.util.ArrayList;

public class Block extends Operand {
    public String label;
    public ArrayList<BasicInstruction> instructions = new ArrayList<>();

    public Block(String Label) {
        this.label = Label;
    }

}
