package optimize;

import ir.BasicBlock;
import ir.IRBuilder;
import ir.Instructions.*;
import ir.Value;
import ir.constant.IntConst;
import ir.constant.NullConst;
import ir.type.PointerType;

import java.util.*;

public class Mem2Reg {
    public IRBuilder builder;

    public Mem2Reg(ir.IRBuilder irBuilder) {
        this.builder = irBuilder;
    }

    public void runOnFunc(ir.Function func) {
        new DominateTree(false).runOnFunc(func);
        countAllocas(func);
        for (var alloca : allocas) {
            insertPhiFor(func, alloca);
        }
        variableRenaming(func.entry);
    }

    public HashMap<PhiInstruction, String> phiAllocaName = new HashMap<>();
    public ArrayList<Basic> allocas = new ArrayList<>();
    public HashMap<String, Stack<Value>> nameStack = new HashMap<>();

    public void countAllocas(ir.Function func) {
        allocas.clear();
        for (var inst : func.entry.instructions) {
            if (inst instanceof AllocaInstruction) {
                allocas.add(inst);
            }
        }
    }

    void insertPhiFor(ir.Function func, Basic alloca) {
        Queue<BasicBlock> queue = new ArrayDeque<>();
        var visited = new HashSet<BasicBlock>();
        queue.add(alloca.father);
        for (var user : alloca.users) {
            if (user instanceof StoreInstruction st && st.Op.get(1) == alloca) {
                queue.offer(st.father);
            }
        }
        while (!queue.isEmpty()) {
            var node = queue.poll();
            for (var frontier : node.node.domFrontier) {
                if (visited.contains(frontier.block)) {
                    continue;
                }
                visited.add(frontier.block);
                queue.offer(frontier.block);
                var phi = new PhiInstruction(((PointerType) alloca.type).element,
                        builder.rename(alloca.name), frontier.block);
                phiAllocaName.put(phi, alloca.name);
            }
        }
    }

    Value getReplace(String name) {
        var stack = nameStack.get(name);
        if (stack == null || stack.empty()) {
            return null;
        }
        return nameStack.get(name).lastElement();
    }

    void updateReplace(String name, ir.Value replace) {
        var stack = nameStack.computeIfAbsent(name, k -> new Stack<>());
        stack.push(replace);
    }

    void variableRenaming(BasicBlock block) {
        var popList = new ArrayList<String>();

        for (var phi : block.phiInstructions) {
            var name = phiAllocaName.get(phi);
            if (name != null) {
                updateReplace(name, phi);
                popList.add(name);
            }
        }

        var iter = block.instructions.iterator();
        while (iter.hasNext()) {
            var inst = iter.next();
            if (inst instanceof AllocaInstruction alloca) {
                ir.Value val;
                if (((PointerType) alloca.type).element instanceof PointerType) {
                    val = new NullConst();
                } else {
                    val = new IntConst(0, 32);
                }
                updateReplace(alloca.name, val);
                popList.add(alloca.name);
                iter.remove();
            }
            if (inst instanceof StoreInstruction store) {
                var ptr = store.Op.get(1);
                if (!allocas.contains(ptr))
                    continue;
                var name = ptr.name;
                updateReplace(name, store.Op.get(0));
                popList.add(name);
                iter.remove();
            }
            if (inst instanceof LoadInstruction load) {
                var ptr = load.Op.get(0);
                if (!allocas.contains(ptr))
                    continue;
                var name = ptr.name;
                var replace = getReplace(name);
                inst.replaceAllUse(replace);
                iter.remove();
            }
        }

        for (var suc : block.next) {
            for (var sucPhi : suc.phiInstructions) {
                var name = phiAllocaName.get(sucPhi);
                if (name != null) {
                    sucPhi.addBr(getReplace(name), block);
                }

            }
        }

        for (var node : block.node.children) {
            variableRenaming(node.block);
        }

        for (var name : popList) {
            nameStack.get(name).pop();
        }
    }

}
