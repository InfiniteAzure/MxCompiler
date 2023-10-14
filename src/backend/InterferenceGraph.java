package backend;

import asm.operand.PhysicalReg;
import asm.operand.SimpleReg;

import java.util.HashSet;
import java.util.LinkedHashSet;

public class InterferenceGraph {
    public HashSet<IGEdge> adjSet = new LinkedHashSet<>();

    public void addEdge(SimpleReg u, SimpleReg v) {
        if (u == v) {
            return;
        }
        var edge1 = new IGEdge(u, v);
        if (!adjSet.contains(edge1)) {
            var edge2 = new IGEdge(v, u);
            adjSet.add(edge1);
            adjSet.add(edge2);
            if (!(u instanceof PhysicalReg)) {
                u.node.adjList.add(v);
                u.node.degree++;
            }
            if (!(v instanceof PhysicalReg)) {
                v.node.adjList.add(u);
                v.node.degree++;
            }
        }
    }

    public void addEdgeSpilled(SimpleReg u, SimpleReg v) {
        if (u == v)
            return;
        var edge1 = new IGEdge(u, v);
        if (!adjSet.contains(edge1)) {
            var edge2 = new IGEdge(v, u);
            adjSet.add(edge1);
            adjSet.add(edge2);
            u.nodeSpilled.adjList.add(v);
            u.nodeSpilled.degree++;
            v.nodeSpilled.adjList.add(u);
            v.nodeSpilled.degree++;
        }
    }

    public void init() {
        adjSet.clear();
    }
}
