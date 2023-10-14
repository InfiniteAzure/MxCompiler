package backend;

import asm.instruction.MvInstruction;
import asm.operand.SimpleReg;

import java.util.HashSet;
import java.util.LinkedHashSet;

public class IGNode implements Comparable<IGNode>{
    public HashSet<SimpleReg> adjList = new LinkedHashSet<>();
    public HashSet<MvInstruction> moveList = new LinkedHashSet<>();
    public int degree;
    // do not simplify pre colored node!(why?)
    public double frequency;
    public SimpleReg origin;

    public IGNode(SimpleReg origin) {
        this.origin = origin;
    }

    public void init(boolean precolored) {
        this.adjList.clear();
        this.moveList.clear();
        this.frequency = 0;
        this.degree = precolored ? Integer.MAX_VALUE : 0;
    }

    public int compareTo(IGNode o) {
        return Integer.compare(degree, o.degree);
    }
}

