package optimize;

import ir.BasicBlock;

import java.util.ArrayList;

public class DTNode {

    public BasicBlock block;

    public int dfn;
    public DTNode father;
    public DTNode semi, samedom, idom;

    public ArrayList<DTNode> bucket = new ArrayList<>();
    public DTNode ancestor, best;
    public ArrayList<DTNode> children = new ArrayList<>();
    public ArrayList<DTNode> domFrontier = new ArrayList<>();

    public DTNode(BasicBlock origin) {
        this.block = origin;
    }

    public void clear() {
        dfn = -1;
        father = semi = samedom = idom = ancestor = best = null;
        bucket.clear();
        children.clear();
        domFrontier.clear();
    }

    public boolean isDominatorOf(DTNode o) {
        while (o != null) {
            if (o.idom == this)
                return true;
            o = o.idom;
        }
        return false;
    }

    public String toString() {
        return block.toString();
    }

}
