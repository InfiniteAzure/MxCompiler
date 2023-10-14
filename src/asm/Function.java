package asm;

import asm.operand.SimpleReg;

import java.util.ArrayList;

public class Function extends Operand {
    public String label;
    public ArrayList<Block> blocks = new ArrayList<>();
    public Block entry, exit;
    public ArrayList<SimpleReg> args = new ArrayList<>();
    public int allocaCnt = 0;
    public int spilledReg = 0;
    public int spilledArg = 0;
    public int totalStack = 0;

    public Function(String label) {
        this.label = label;
    }
}
