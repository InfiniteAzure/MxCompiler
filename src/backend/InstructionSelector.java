package backend;

import asm.Block;
import asm.Function;
import asm.asmModule;
import asm.instruction.LoadInstruction;
import asm.instruction.StoreInstruction;
import asm.instruction.*;
import asm.operand.*;
import ir.IRVisitor;
import ir.Instructions.*;
import ir.type.PointerType;

import java.util.ArrayList;
import java.util.Objects;

public class InstructionSelector implements IRVisitor {
    public asmModule module;
    public Function tempFunc;
    public Block tempBlock;

    public PhysicalReg sp = PhysicalReg.regMap.get("sp");
    public PhysicalReg ra = PhysicalReg.regMap.get("ra");
    public PhysicalReg a0 = PhysicalReg.regMap.get("a0");

    public PhysicalReg RegA(int i) {
        return PhysicalReg.regMap.get("a" + i);
    }

    public SimpleReg getReg(ir.Value val) {
        if (val.asm != null) {
            return (SimpleReg) val.asm;
        }
        var constVal = getConstVal(val);
        if (constVal != null) {
            if (constVal == 0) {
                val.asm = PhysicalReg.regMap.get("zero");
            } else {
                var reg = new VirtualReg(val.type.size());
                new LiInstruction(reg, new Imm(constVal), tempBlock);
                return reg;
            }
        } else {
            val.asm = new VirtualReg(val.type.size());
        }
        return (SimpleReg) val.asm;
    }

    public Integer getConstVal(ir.Value v) {
        Integer constVal = null;
        if (v instanceof ir.constant.IntConst x)
            constVal = x.itself;
        else if (v instanceof ir.constant.NullConst)
            constVal = 0;
        return constVal;
    }

    public InstructionSelector(asmModule Module) {
        this.module = Module;
    }

    public void visit(ir.Module irModule) {
        for (var v : irModule.globalVariables) {
            var init = getConstVal(v.init);
            int init_;
            init_ = Objects.requireNonNullElse(init, 0);
            v.asm = new GlobalVariable(v.name.substring(1), init_, v.type.size());
            module.globalVars.add((GlobalVariable) v.asm);
        }
        for (var s : irModule.strings) {
            s.asm = new StringConst(s.name.substring(1), s.itself);
            module.stringConsts.add((StringConst) s.asm);
        }
        for (var i : irModule.functions) {
            i.accept(this);
        }
    }

    public void visit(ir.Function func) {
        tempFunc = new asm.Function(func.name.substring(1));
        module.funcs.add(tempFunc);
        VirtualReg.VirtualRegCount = 0;

        for (var i : func.blocks) {
            i.asm = new Block(i.name);
            tempFunc.blocks.add((Block) i.asm);
        }
        tempBlock = (Block) func.entry.asm;

        new ITypeInstruction("addi", sp, sp, new Stack(0, Stack.StackType.decSp), tempBlock);

        VirtualReg savedRa = new VirtualReg(4);
        new MvInstruction(savedRa, ra, tempBlock);
        var savedRegs = new ArrayList<SimpleReg>();
        for (var reg : PhysicalReg.Callee) {
            var rd = new VirtualReg(4);
            savedRegs.add(rd);
            new MvInstruction(rd, reg, tempBlock);
        }

        for (int i = 0; i < func.Op.size(); ++i) {
            var arg = func.Op.get(i);
            if (i < 8) {
                arg.asm = RegA(i);
            } else {
                var reg = new VirtualReg(4);
                arg.asm = reg;
                new LoadInstruction(4, reg, sp, new Stack(i - 8, Stack.StackType.getArg), tempBlock);
            }
        }

        func.blocks.forEach(x -> x.accept(this));

        tempBlock = (Block) func.exit.asm;
        int i = 0;
        for (var reg : PhysicalReg.Callee) {
            new MvInstruction(reg, savedRegs.get(i), tempBlock);
            i++;
        }
        new MvInstruction(ra, savedRa, tempBlock);

        new ITypeInstruction("addi", sp, sp, new Stack(0, Stack.StackType.incSp), tempBlock);

        new asm.instruction.RetInstruction(tempBlock);
    }

    public void visit(ir.BasicBlock block) {
        tempBlock = (Block) block.asm;
        block.instructions.forEach(x -> x.accept(this));
    }

    public void visit(AllocaInstruction instruction) {
        instruction.asm = new Stack(tempFunc.allocaCnt, Stack.StackType.alloca);
        tempFunc.allocaCnt++;
    }

    public void visit(BinaryInstruction inst) {
        String op = switch (inst.op) {
            case "add" -> "add";
            case "sub" -> "sub";
            case "and" -> "and";
            case "or" -> "or";
            case "xor" -> "xor";
            case "mul" -> "mul";
            case "sdiv" -> "div";
            case "srem" -> "rem";
            case "shl" -> "sll";
            case "ashr" -> "sra";
            default -> null;
        };
        boolean hasIType = switch (inst.op) {
            case "mul", "sdiv", "srem" -> false;
            default -> true;
        };
        boolean commutative = switch (op) {
            case "add", "and", "or", "xor", "mul" -> true;
            default -> false;
        };
        var op1 = inst.Op.get(0);
        var op2 = inst.Op.get(1);
        if (hasIType) {
            if (commutative && op1 instanceof ir.constant.IntConst) {
                var tmp = op1;
                op1 = op2;
                op2 = tmp;
            }
            if (op2 instanceof ir.constant.IntConst x) {
                String iop = op + "i";
                int val = x.itself;
                if (op.equals("sub")) {
                    iop = "addi";
                    val = -val;
                }
                if (val < 1 << 11 && val >= -(1 << 11)) {
                    new ITypeInstruction(iop, getReg(inst), getReg(op1), new Imm(val), tempBlock);
                    return;
                }
            }
        }
        new RTypeInstruction(op, getReg(inst), getReg(op1), getReg(op2), tempBlock);
    }

    public void visit(BrInstruction inst) {
        if (inst.Op.size() == 1) {
            new JumpInstruction((Block) inst.Op.get(0).asm, tempBlock);
        } else {
            new BeqzInstruction(getReg(inst.Op.get(0)), (Block) inst.Op.get(2).asm, tempBlock);
            new JumpInstruction((Block) inst.Op.get(1).asm, tempBlock);
        }
    }

    public void visit(ir.Instructions.CallInstruction inst) {
        for (int i = 0; i + 1 < inst.Op.size(); ++i) {
            var arg = inst.Op.get(i + 1);
            if (i < 8) {
                new MvInstruction(RegA(i), getReg(arg), tempBlock);
            } else {
                tempFunc.spilledArg = Math.max(tempFunc.spilledArg, i - 8);
                var offset = new Stack(i - 8, Stack.StackType.putArg);
                new asm.instruction.StoreInstruction(4, getReg(arg), sp, offset, tempBlock);
            }
        }

        new asm.instruction.CallInstruction(inst.Op.get(0).name.substring(1), tempBlock);

        if (!(inst.type instanceof ir.type.VoidType)) {
            new MvInstruction(getReg(inst), a0, tempBlock);
        }
    }

    public void visit(GetElementPtrInstruction inst) {
        var ptr = inst.Op.get(0);
        var ptrElemType = ((PointerType) ptr.type).element;
        if (ptrElemType instanceof ir.type.ArrayType) {
            var reg = new VirtualReg(4);
            var s = (StringConst) ptr.asm;
            new LuiInstruction(reg, new Relocation(s, Relocation.RelocationType.hi), tempBlock);
            new ITypeInstruction("addi", reg, reg, new Relocation(s, Relocation.RelocationType.lo), tempBlock);
            inst.asm = reg;
        } else if (ptrElemType instanceof ir.type.StructType) {
            var tmp = new VirtualReg(4);
            // TODO optimize
            new ITypeInstruction("slli", tmp, getReg(inst.Op.get(2)), new Imm(2), tempBlock);
            new RTypeInstruction("add", getReg(inst), getReg(ptr), tmp, tempBlock);
        } else {
            // TODO optimize
            SimpleReg tmp;
            if (ptrElemType.size() < 4) {
                tmp = getReg(inst.Op.get(1));
            } else {
                tmp = new VirtualReg(4);
                new ITypeInstruction("slli", tmp, getReg(inst.Op.get(1)), new Imm(2), tempBlock);
            }
            new RTypeInstruction("add", getReg(inst), getReg(ptr), tmp, tempBlock);
        }
    }

    public void visit(IcmpInstruction inst) {
        VirtualReg tmp = null;
        switch (inst.op) {
            case "slt" ->
                    new RTypeInstruction("slt", getReg(inst), getReg(inst.Op.get(0)), getReg(inst.Op.get(1)), tempBlock);
            case "sgt" ->
                    new RTypeInstruction("slt", getReg(inst), getReg(inst.Op.get(1)), getReg(inst.Op.get(0)), tempBlock);
            case "eq" -> {
                tmp = new VirtualReg(4);
                new RTypeInstruction("sub", tmp, getReg(inst.Op.get(0)), getReg(inst.Op.get(1)), tempBlock);
                new ITypeInstruction("seqz", getReg(inst), tmp, null, tempBlock);
            }
            case "ne" -> {
                tmp = new VirtualReg(4);
                new RTypeInstruction("sub", tmp, getReg(inst.Op.get(0)), getReg(inst.Op.get(1)), tempBlock);
                new ITypeInstruction("snez", getReg(inst), tmp, null, tempBlock);
            }
            case "sge" -> { // a >= b -> !(a < b)
                tmp = new VirtualReg(4);
                new RTypeInstruction("slt", tmp, getReg(inst.Op.get(0)), getReg(inst.Op.get(1)), tempBlock);
                new ITypeInstruction("xori", getReg(inst), tmp, new Imm(1), tempBlock);
            }
            case "sle" -> { // a <= b -> !(b < a)
                tmp = new VirtualReg(4);
                new RTypeInstruction("slt", tmp, getReg(inst.Op.get(1)), getReg(inst.Op.get(0)), tempBlock);
                new ITypeInstruction("xori", getReg(inst), tmp, new Imm(1), tempBlock);
            }
        }
    }

    public void visit(ir.Instructions.LoadInstruction inst) {
        var ptr = inst.Op.get(0);
        var size = ((PointerType) ptr.type).element.size();
        if (ptr instanceof ir.constant.Constant v) {
            var tmp = new VirtualReg(4);
            var obj = (GlobalObject) v.asm;
            new LuiInstruction(tmp, new Relocation(obj, Relocation.RelocationType.hi), tempBlock);
            new LoadInstruction(size, getReg(inst), tmp, new Relocation(obj, Relocation.RelocationType.lo), tempBlock);
        } else {
            if (ptr.asm instanceof Stack x)
                new LoadInstruction(size, getReg(inst), sp, x, tempBlock);
            else
                new LoadInstruction(size, getReg(inst), (SimpleReg) ptr.asm, new Imm(0), tempBlock);
        }
    }

    public void visit(ir.Instructions.ReturnInstruction inst) {
        if (!inst.Op.isEmpty()) {
            new MvInstruction(a0, getReg(inst.Op.get(0)), tempBlock);
        }
    }

    public void visit(ir.Instructions.StoreInstruction inst) {
        var ptr = inst.Op.get(1);
        var val = inst.Op.get(0);
        if (ptr instanceof ir.constant.Constant v) {
            var tmp = new VirtualReg(4);
            var obj = (GlobalObject) v.asm;
            new LuiInstruction(tmp, new Relocation(obj, Relocation.RelocationType.hi), tempBlock);
            new StoreInstruction(val.type.size(), getReg(val), tmp, new Relocation(obj, Relocation.RelocationType.lo),tempBlock);
        } else {
            if (ptr.asm instanceof Stack x)
                new StoreInstruction(val.type.size(), getReg(val), sp, x, tempBlock);
            else
                new StoreInstruction(val.type.size(), getReg(val), (SimpleReg) ptr.asm, new Imm(0), tempBlock);
        }
    }

    public void visit(BitCastInstruction inst) {
        new MvInstruction(getReg(inst), getReg(inst.Op.get(0)), tempBlock);
    }

    public void visit(TruncInstruction inst) {
        var tmp = new VirtualReg(4);
        new ITypeInstruction("andi", tmp, getReg(inst.Op.get(0)), new Imm(1), tempBlock);
        new MvInstruction(getReg(inst), tmp, tempBlock);
    }

    public void visit(ZextInstruction inst) {
        var tmp = new VirtualReg(4);
        new ITypeInstruction("andi", tmp, getReg(inst.Op.get(0)), new Imm(1), tempBlock);
        new MvInstruction(getReg(inst), tmp, tempBlock);
    }

}
