package backend;

import asm.Function;
import asm.instruction.LoadInstruction;
import asm.instruction.MvInstruction;
import asm.instruction.StoreInstruction;
import asm.operand.PhysicalReg;
import asm.operand.SimpleReg;
import asm.operand.VirtualReg;

import java.util.*;

public class RegAllocator {
    //greatest part in hushu, and it totally fucked me up
    //still not quite understand, but it finally worked
    //lots of notes to remind me what I am doing now
    //I even worked out one with freeze, but maybe wrong
    //usages of the hashset is shown on their name
    //warning: every node should be in one, and ONLY one of the Hashsets below
    public final HashSet<SimpleReg> precolored = new LinkedHashSet<>(PhysicalReg.regMap.values());

    public final HashSet<SimpleReg> initial = new LinkedHashSet<>();

    public final HashSet<SimpleReg> simplifyWorklist = new LinkedHashSet<>();

    public final HashSet<SimpleReg> freezeWorklist = new LinkedHashSet<>();

    public final HashSet<SimpleReg> spillWorklist = new LinkedHashSet<>();

    public final HashSet<SimpleReg> spilledNodes = new LinkedHashSet<>();

    public final HashSet<SimpleReg> coalescedNodes = new LinkedHashSet<>();

    public final HashMap<SimpleReg, SimpleReg> alias = new HashMap<>();

    public final HashSet<SimpleReg> coloredNodes = new LinkedHashSet<>();

    public final java.util.Stack<SimpleReg> selectedStack = new Stack<>();

    //these below are for the moveInstructions. rule same as above

    public final HashSet<MvInstruction> coalescedMoves = new LinkedHashSet<>();

    public final HashSet<MvInstruction> constrainedMoves = new LinkedHashSet<>();

    public final HashSet<MvInstruction> frozenMoves = new LinkedHashSet<>();

    public final HashSet<MvInstruction> worklistMoves = new LinkedHashSet<>();

    public final HashSet<MvInstruction> activeMoves = new LinkedHashSet<>();

    public final static int K = PhysicalReg.assignable.size();
    public Function tempFunc;
    public InterferenceGraph G = new InterferenceGraph();

    public InterferenceGraph GSpilled = new InterferenceGraph();

    public final HashSet<SimpleReg> introduced = new HashSet<>();

    public void runOnModule(asm.asmModule module) {
        for (var func : module.funcs) {
            runOnFunc(func);
        }
    }

    public void runOnFunc(Function func) {
        introduced.clear();
        tempFunc = func;
        graphColoring();
        removeUselessMv();
    }

    public void init() {
        initial.clear();
        simplifyWorklist.clear();
        freezeWorklist.clear();
        spillWorklist.clear();
        spilledNodes.clear();
        coalescedNodes.clear();
        alias.clear();
        coloredNodes.clear();
        selectedStack.clear();

        coalescedMoves.clear();
        constrainedMoves.clear();
        frozenMoves.clear();
        worklistMoves.clear();
        activeMoves.clear();

        G.init();

        // add all virtual registers to initial
        for (var block : tempFunc.blocks) {
            for (var inst : block.instructions) {
                for (var reg : inst.uses())
                    if (reg instanceof VirtualReg)
                        initial.add(reg);
                for (var reg : inst.defs())
                    if (reg instanceof VirtualReg)
                        initial.add(reg);
            }
        }

        for (var reg : initial) {
            reg.color = null;
            reg.node.init(false);
        }
        for (var reg : precolored) {
            reg.color = (PhysicalReg) reg;
            reg.node.init(true);
        }

        // frequency of a register = sum (uses + defs) * 10^loopDepth
        for (var block : tempFunc.blocks) {
            double weight = Math.pow(10, block.loopDepth);
            for (var inst : block.instructions) {
                for (var reg : inst.defs())
                    reg.node.frequency += weight;
                for (var reg : inst.uses())
                    reg.node.frequency += weight;
            }
        }
    }

    void initSpilled() {
        GSpilled.init();
        for (var node : spilledNodes) {
            node.nodeSpilled.init(false);
        }
    }

    public void build() {
        for (var block : tempFunc.blocks) {
            var lives = new HashSet<>(block.liveOut);

            // iterate in reverse order
            for (int i = block.instructions.size() - 1; i >= 0; i--) {
                var inst = block.instructions.get(i);
                var uses = inst.uses();
                var defs = inst.defs();
                if (inst instanceof MvInstruction mv) {
                    //kills the move between regs with the same value.
                    lives.removeAll(uses);
                    uses.forEach(reg -> reg.node.moveList.add(mv));
                    defs.forEach(reg -> reg.node.moveList.add(mv));
                    worklistMoves.add(mv);
                }

                lives.add(PhysicalReg.zero);
                lives.addAll(defs);
                for (var def : defs) {
                    for (var live : lives) {
                        G.addEdge(live, def);
                    }
                }
                lives.removeAll(defs);
                lives.addAll(uses);
            }
        }
    }

    public void buildSpilled() {
        for (var block : tempFunc.blocks) {
            var lives = filterSpilled(new HashSet<>(block.liveOut));

            // do it in reversed!
            for (int i = block.instructions.size() - 1; i >= 0; i--) {
                var inst = block.instructions.get(i);
                var uses = filterSpilled(inst.uses());
                var defs = filterSpilled(inst.defs());
                if (inst instanceof MvInstruction mv) {
                    lives.removeAll(uses);
                    uses.forEach(reg -> reg.nodeSpilled.moveList.add(mv));
                    defs.forEach(reg -> reg.nodeSpilled.moveList.add(mv));
                }

                lives.addAll(defs);
                for (var def : defs) {
                    for (var live : lives) {
                        GSpilled.addEdgeSpilled(live, def);
                    }
                }
                lives.removeAll(defs);
                lives.addAll(uses);
            }
        }
    }

    //dangerous! does things to input!
    public HashSet<SimpleReg> filterSpilled(HashSet<SimpleReg> set) {
        set.retainAll(spilledNodes);
        return set;
    }

    //this one, however, is not dangerous
    HashSet<MvInstruction> nodeMoves(SimpleReg reg) {
        var ret = new HashSet<MvInstruction>();
        for (var mv : reg.node.moveList) {
            if (activeMoves.contains(mv))
                ret.add(mv);
            else if (worklistMoves.contains(mv))
                ret.add(mv);
        }
        return ret;
    }

    public void makeWorklist() {
        for (var reg : initial) {
            if (reg.node.degree >= K) {
                spillWorklist.add(reg);
            } else if (moveRelated(reg)) {
                freezeWorklist.add(reg);
            } else {
                simplifyWorklist.add(reg);
            }
        }
        initial.clear();
    }

    public boolean moveRelated(SimpleReg reg) {
        return !nodeMoves(reg).isEmpty();
    }

    public void removeUselessMv() {
        for (var block : tempFunc.blocks) {
            var iter = block.instructions.iterator();
            while (iter.hasNext()) {
                var inst = iter.next();
                if (inst instanceof MvInstruction mv && mv.rs.color == mv.rd.color) {
                    iter.remove();
                }
            }
        }
    }

    HashSet<SimpleReg> adjacent(SimpleReg reg) {
        var ret = new HashSet<>(reg.node.adjList);
        ret.removeAll(selectedStack);
        ret.removeAll(coloredNodes);
        return ret;
    }

    public void graphColoring() {
        init();
        new LivingAnalysis().runOnFunc(tempFunc);
        build();
        makeWorklist();
        do {
            //do not change the order below!
            if (!simplifyWorklist.isEmpty())
                simplify();
            else if (!worklistMoves.isEmpty())
                coalesce();
            else if (!freezeWorklist.isEmpty())
                freeze();
            else if (!spillWorklist.isEmpty())
                selectSpill();
        } while (!simplifyWorklist.isEmpty() || !worklistMoves.isEmpty() || !freezeWorklist.isEmpty()
                || !spillWorklist.isEmpty());

        assignColors();
        if (!spilledNodes.isEmpty()) {
            initSpilled();
            buildSpilled();
            assignColorsSpilled();
            rewriteProgram();
            graphColoring();
        }
    }

    public void simplify() {
        var iter = simplifyWorklist.iterator();
        var reg = iter.next();
        iter.remove();
        selectedStack.push(reg);
        adjacent(reg).forEach(this::decrementDegree);
    }

    SimpleReg getAlias(SimpleReg reg) {
        if (!coalescedNodes.contains(reg))
            return reg;
        var a = getAlias(alias.get(reg));
        alias.put(reg, a);
        return a;
    }

    //the functions below deals with the degree change when removing a node.
    //they change the degree, and assess whether others should be put in worklist.

    void decrementDegree(SimpleReg reg) {
        int d = reg.node.degree;
        reg.node.degree--;
        if (d == K) {
            var adj = adjacent(reg);
            adj.add(reg);
            enableMoves(adj);
            spillWorklist.remove(reg);
            if (moveRelated(reg))
                freezeWorklist.add(reg);
            else
                simplifyWorklist.add(reg);
        }
    }

    public void enableMoves(Set<SimpleReg> regs) {
        for (SimpleReg reg : regs) {
            var moves = nodeMoves(reg);
            for (var mv : moves) {
                if (activeMoves.contains(mv)) {
                    activeMoves.remove(mv);
                    worklistMoves.add(mv);
                }
            }
        }
    }

    public void addWorklist(SimpleReg reg) {
        if (!(reg instanceof PhysicalReg) &&
                !moveRelated(reg) && reg.node.degree < K) {
            freezeWorklist.remove(reg);
            simplifyWorklist.add(reg);
        }
    }

    public void freeze() {
        var iter = freezeWorklist.iterator();
        var reg = iter.next();
        iter.remove();
        simplifyWorklist.add(reg);
        freezeMoves(reg);
    }

    public void freezeMoves(SimpleReg u) {
        for (var mv : nodeMoves(u)) {
            SimpleReg v = getAlias(mv.rs);
            if (getAlias(u) == v)
                v = getAlias(mv.rd);
            activeMoves.remove(mv);
            frozenMoves.add(mv);
            if (nodeMoves(v).isEmpty() && v.node.degree < K) {
                freezeWorklist.remove(v);
                simplifyWorklist.add(v);
            }
        }
    }

    //don't ask me why this work, ask George and Briggs

    public void coalesce() {
        var iter = worklistMoves.iterator();
        var mv = iter.next();

        var u = getAlias(mv.rd);
        var v = getAlias(mv.rs);
        if (v instanceof PhysicalReg) {
            var tmp = u;
            u = v;
            v = tmp;
        }
        var edge = new IGEdge(u, v);
        iter.remove();

        if (u == v) {
            coalescedMoves.add(mv);
            addWorklist(u);
        } else if (v instanceof PhysicalReg || G.adjSet.contains(edge)) {
            constrainedMoves.add(mv);
            addWorklist(u);
            addWorklist(v);
        } else if (u instanceof PhysicalReg && george(u, v)
                || !(u instanceof PhysicalReg) && briggs(u, v)) {
            coalescedMoves.add(mv);
            combine(u, v);
            addWorklist(u);
        } else {
            activeMoves.add(mv);
        }
    }

    //George: u's neighbours must have small degree or interfere v

    public boolean george(SimpleReg u, SimpleReg v) {
        for (var t : adjacent(u)) {
            if (t.node.degree < K)
                continue;
            if (t instanceof PhysicalReg)
                continue;
            if (G.adjSet.contains(new IGEdge(t, v)))
                continue;
            return false;
        }
        return true;
    }

    //Briggs: coalescing u and v must result in less than K neighbour of > K

    public boolean briggs(SimpleReg u, SimpleReg v) {
        var adj = adjacent(u);
        adj.addAll(adjacent(v));
        int cnt = 0;
        for (var n : adj) {
            if (n.node.degree >= K)
                cnt++;
        }
        return cnt < K;
    }

    public void combine(SimpleReg u, SimpleReg v) {
        if (freezeWorklist.contains(v))
            freezeWorklist.remove(v);
        else
            spillWorklist.remove(v);
        coalescedNodes.add(v);
        alias.put(v, u);
        u.node.moveList.addAll(v.node.moveList);
        enableMoves(Set.of(v));
        for (var t : adjacent(v)) {
            G.addEdge(t, u);
            decrementDegree(t);
        }
        if (u.node.degree >= K && freezeWorklist.contains(u)) {
            freezeWorklist.remove(u);
            spillWorklist.add(u);
        }
    }

    public void selectSpill() {
        SimpleReg minReg = null;
        double minCost = Double.POSITIVE_INFINITY;
        for (var reg : spillWorklist) {
            double regCost = reg.node.frequency / reg.node.degree;
            // avoid choosing nodes introduced by the load and store of
            // previously spilled registers
            if (introduced.contains(reg))
                regCost += 1e10;
            if (regCost < minCost) {
                minReg = reg;
                minCost = regCost;
            }
        }
        spillWorklist.remove(minReg);
        simplifyWorklist.add(minReg);
        freezeMoves(minReg);
    }

    public void assignColors() {
        while (!selectedStack.empty()) {
            var reg = selectedStack.pop();
            var availColors = new ArrayList<>(PhysicalReg.assignable);
            for (var t : reg.node.adjList) {
                t = getAlias(t);
                if (t instanceof PhysicalReg || coloredNodes.contains(t)) {
                    availColors.remove(t.color);
                    //not suspicious if you promise you are right!
                }

            }
            if (availColors.isEmpty()) {
                spilledNodes.add(reg);
            } else {
                coloredNodes.add(reg);
                reg.color = availColors.iterator().next();
            }
        }

        for (var reg : coalescedNodes) {
            reg.color = getAlias(reg).color;
        }
    }

    public void assignColorsSpilled() {
        int i = 0, num = spilledNodes.size();
        IGNode[] nodes = new IGNode[num];
        for (var reg : spilledNodes) {
            nodes[i++] = reg.nodeSpilled;
        }
        var colors = new HashSet<SimpleReg>();
        i = 0;
        while (i != num) {
            Arrays.sort(nodes, i, num);
            var node = nodes[i++];
            var reg = node.origin;
            var availColors = new HashSet<>(colors);
            for (var t : node.adjList) {
                t = getAlias(t);
                availColors.remove(t.color);
                t.nodeSpilled.degree--;
            }
            if (availColors.isEmpty()) {
                colors.add(reg);
                reg.color = reg;
            } else {
                reg.color = availColors.iterator().next();
            }
        }
        for (var reg : colors) {
            reg.stackOffset = new asm.operand.Stack(tempFunc.spilledReg, asm.operand.Stack.StackType.spill);
            tempFunc.spilledReg++;
        }
    }


    //temporary Reg creation
    public void rewriteProgram() {
        for (var block : tempFunc.blocks) {
            var iter = block.instructions.listIterator();
            while (iter.hasNext()) {
                var inst = iter.next();
                iter.previous();
                for (var reg : inst.uses()) {
                    var regAlias = getAlias(reg);

                    if (!spilledNodes.contains(regAlias)) {
                        if (regAlias != reg)
                            inst.replaceUse(reg, regAlias);
                        continue;
                    }
                    var tmp = new VirtualReg(((VirtualReg) reg).size);
                    var load = new LoadInstruction(tmp.size, tmp, PhysicalReg.sp,
                            regAlias.color.stackOffset, null);
                    iter.add(load);
                    inst.replaceUse(reg, tmp);
                    introduced.add(tmp);
                }
                iter.next();
                for (var reg : inst.defs()) {
                    var regAlias = getAlias(reg);
                    if (!spilledNodes.contains(regAlias)) {
                        if (regAlias != reg)
                            inst.replaceDef(reg, regAlias);
                        continue;
                    }
                    var tmp = new VirtualReg(((VirtualReg) reg).size);
                    var store = new StoreInstruction(tmp.size, tmp, PhysicalReg.sp,
                            regAlias.color.stackOffset, null);
                    iter.add(store);
                    inst.replaceDef(reg, tmp);
                    introduced.add(tmp);
                }
            }
        }
    }

}
