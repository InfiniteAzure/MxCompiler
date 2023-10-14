package backend;

import asm.operand.SimpleReg;

public class IGEdge {
    public SimpleReg u, v;

    public IGEdge(SimpleReg u, SimpleReg v) {
        this.u = u;
        this.v = v;
    }

    public boolean equals(Object o) {
        return o instanceof IGEdge e && (e.u == u && e.v == v);
    }

    public int hashCode() {
        return u.hashCode() ^ v.hashCode();
    }

}
