package optimize;

import ir.BasicBlock;

import java.util.ArrayList;
import java.util.HashSet;

public class DominateTree {
    boolean isReverse;

    ArrayList<DTNode> dfnOrder = new ArrayList<>();
    public int dfn;

    public DominateTree(boolean reversed) {
        this.isReverse = reversed;
    }

    public BasicBlock entry(ir.Function func) {
        if (isReverse) {
            return func.exit;
        } else {
            return func.entry;
        }
    }

    public void runOnFunc(ir.Function func) {
        computeIDom(func);
        getChildren(func);
        computeDF(entry(func).node);
    }

    public ArrayList<BasicBlock> predecessor(DTNode node) {
        if (!isReverse) {
            return node.block.prev;
        } else {
            return node.block.next;
        }
    }

    public ArrayList<BasicBlock> successor(DTNode node) {
        if (isReverse) {
            return node.block.prev;
        } else {
            return node.block.next;
        }
    }

    public void getChildren(ir.Function func) {
        for (var block : func.blocks) {
            var node = block.node;
            if (node.idom != null)
                node.idom.children.add(node);
        }
    }

    public void dfs(DTNode father, DTNode n) {
        if (n.dfn > 0)
            return;
        n.dfn = dfn;
        dfnOrder.add(n);
        n.father = father;
        dfn++;
        for (var succ : successor(n)) {
            dfs(n, succ.node);
        }
    }

    public void computeDF(DTNode node) {
        var S = new HashSet<DTNode>();
        for (var nxtBlock : successor(node)) {
            var nxt = nxtBlock.node;
            if (nxt.idom != node)
                S.add(nxt);
        }
        for (var c : node.children) {
            computeDF(c);
            for (var w : c.domFrontier) {
                if (node == w || !node.isDominatorOf(w))
                    S.add(w);
            }
        }
        node.domFrontier.clear();
        node.domFrontier.addAll(S);
    }

    void computeIDom(ir.Function func) {
        dfn = 0;
        dfnOrder.clear();
        for (var b : func.blocks) {
            b.node.clear();
        }
        dfs(null, entry(func).node);
        for (int i = dfn - 1; i >= 1; --i) {
            var n = dfnOrder.get(i);
            var p = n.father;
            var s = p;
            for (var v : predecessor(n)) {
                if (v.node.dfn < 0)
                    continue;
                DTNode ss;
                if (v.node.dfn <= n.dfn)
                    ss = v.node;
                else
                    ss = ancestorWithLowestSemi(v.node).semi;
                if (ss.dfn < s.dfn)
                    s = ss;
            }
            n.semi = s;
            s.bucket.add(n);
            addEdge(p, n);
            for (var v : p.bucket) {
                var y = ancestorWithLowestSemi(v);
                if (y.semi == v.semi)
                    v.idom = p;
                else
                    v.samedom = y;
            }
            p.bucket.clear();
        }
        for (int i = 1; i <= dfn - 1; ++i) {
            var n = dfnOrder.get(i);
            if (n.samedom != null)
                n.idom = n.samedom.idom;
        }
    }

    public DTNode ancestorWithLowestSemi(DTNode v) {
        var a = v.ancestor;
        if (a.ancestor != null) {
            var b = ancestorWithLowestSemi(a);
            v.ancestor = a.ancestor;
            if (b.semi.dfn < v.best.semi.dfn)
                v.best = b;
        }
        return v.best;
    }

    void addEdge(DTNode p, DTNode n) {
        n.ancestor = p;
        n.best = n;
    }
}

