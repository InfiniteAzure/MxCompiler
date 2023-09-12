package ir;

import ast.*;
import ast.expressions.*;
import ast.statement.*;
import ir.Instructions.*;
import ir.constant.*;
import ir.type.*;
import scope.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class IRBuilder implements Visitor {
    public GlobalScope Program;
    public Scope temp;
    public Function tempFunc;
    public BasicBlock tempBlock;

    public ClassScope Class;
    public String ClassName;
    public StructType ClassType;
    public Module module = new Module();

    public IRBuilder(GlobalScope program) {
        this.Program = program;
        this.temp = program;
        this.initBuiltIn();
    }

    public void visit(ProgramNode node) {
        for (var i : node.definitions) {
            if (i instanceof ClassNode cls) {
                ClassDeclare(cls);
            }
        }

        for (var i : node.definitions) {
            if (i instanceof ClassNode cls)
                defineClassType(cls);
            if (i instanceof VariableDefineNode v)
                for (var j : v.defines) {
                    declareGlobalVariable(j);
                }
        }

        for (var i : node.definitions) {
            if (i instanceof ClassNode cls) {
                memberFunctionDeclare(cls);
            }
            if (i instanceof FunctionNode f) {
                declareFunction(f);
            }

        }

        initGlobalVariable(node);

        for (var i : node.definitions) {
            if (i instanceof ClassNode) {
                i.accept(this);
            }
        }
        for (var i : node.definitions) {
            if (i instanceof FunctionNode) {
                i.accept(this);
            }
        }
    }

    public void visit(ClassNode node) {
        ClassName = node.name;
        Class = Program.getClassScope(ClassName);
        ClassType = Program.getClassType(ClassName);
        temp = Class;
        boolean hasCtor = false;
        for (var i : node.definitions) {
            if (i instanceof VariableDefineNode)
                continue;
            if (i instanceof ConstructorNode)
                hasCtor = true;
            i.accept(this);
        }

        if (!hasCtor) {
            tempFunc = Class.getFunc(node.name);
            tempFunc.entry = new BasicBlock(rename("entry"), tempFunc);
            tempFunc.exit = new BasicBlock(rename("exit"), tempFunc);
            tempBlock = tempFunc.exit;
            Ret();
            tempBlock = tempFunc.entry;
            if (tempFunc.member) {
                var thisType = new PointerType(ClassType);
                var val = new Value(thisType, rename("%this"));
                tempFunc.addOp(val);
            }
            if (!tempBlock.terminated) {
                new BrInstruction(tempFunc.exit, tempBlock);
            }
        }
        temp = temp.father;
        ClassName = null;
        Class = null;
        ClassType = null;
    }

    public void visit(ConstructorNode node) {
        tempFunc = Class.getFunc(node.name);
        tempFunc.entry = new BasicBlock(rename("entry"), tempFunc);
        tempFunc.exit = new BasicBlock(rename("exit"), tempFunc);
        temp = node.func;
        tempBlock = tempFunc.exit;
        Ret();
        tempBlock = tempFunc.entry;
        if (tempFunc.member) {
            var thisType = new PointerType(ClassType);
            var ptr = new AllocaInstruction(getMemType(thisType), rename("%this.addr"), tempFunc.entry);
            temp.addVariable("this", ptr);
            var val = new Value(thisType, rename("%this"));
            tempFunc.addArgs(val);
            newStore(val, ptr);
        }
        node.block.accept(this);
        if (!tempBlock.terminated) {
            new BrInstruction(tempFunc.exit, tempBlock);
        }
        temp = temp.father;
    }

    public void visit(FunctionNode node) {
        if (Class != null) {
            tempFunc = Class.getFunc(node.name);
        } else {
            tempFunc = Program.getFunc(node.name);
        }
        tempFunc.entry = new BasicBlock(rename("entry"), tempFunc);
        tempFunc.exit = new BasicBlock(rename("exit"), tempFunc);
        temp = node.func;
        tempBlock = tempFunc.exit;
        var funcType = tempFunc.type();
        if (funcType.Return instanceof VoidType) {
            Ret();
        } else {
            tempFunc.returnValue = new AllocaInstruction(getMemType(funcType.Return), rename("%.retval.addr"), tempFunc.entry);
            Ret(newLoad("%.retval", tempFunc.returnValue, tempFunc.exit));
        }
        tempBlock = tempFunc.entry;
        int offset = 0;
        if (tempFunc.member) {
            offset = 1;
            var thisType = new PointerType(ClassType);
            var ptr = newAlloca(thisType, "%this.addr");
            temp.addVariable("this", ptr);
            var val = new Value(thisType, rename("%this"));
            tempFunc.addArgs(val);
            newStore(val, ptr);
        }
        for (int i = 0; i < node.parameterlist.parameters.size(); ++i) {
            var paramNode = node.parameterlist.parameters.get(i);
            var type = funcType.parameters.get(i + offset);
            var ptr = newAlloca(type, "%" + paramNode.name + ".addr");
            temp.addVariable(paramNode.name, ptr);
            var val = new Value(type, rename("%" + paramNode.name));
            tempFunc.addArgs(val);
            newStore(val, ptr);
        }
        if (node.name.equals("main")) {
            new CallInstruction(nextName(), Program.getFunc("__global_var_init"), tempBlock);
        }
        node.block.accept(this);
        if (!tempBlock.terminated) {
            new BrInstruction(tempFunc.exit, tempBlock);
        }
        temp = temp.father;
    }

    public void visit(VariableDefineNode node) {
        for (var i : node.defines) {
            i.accept(this);
        }
    }

    public void visit(SingleVariableDefineNode node) {
        var ptr = newAlloca(getType(node.type), "%" + node.name + ".addr");
        if (node.right != null) {
            node.right.accept(this);
            newStore(getValue(node.right), ptr);
        }
        temp.addVariable(node.name, ptr);
    }

    public void visit(ForNode node) {
        var condBlock = new BasicBlock(rename("for.cond"), tempFunc);
        var bodyBlock = new BasicBlock(rename("for.body"), tempFunc);
        var incBlock = new BasicBlock(rename("for.inc"), tempFunc);
        var endBlock = new BasicBlock(rename("for.end"), tempFunc);
        node.scope.ContinueBlock = incBlock;
        node.scope.BreakBlock = endBlock;
        temp = node.scope;
        if (node.initVariable != null) {
            node.initVariable.accept(this);
        }
        if (node.one != null) {
            node.one.accept(this);
        }
        new BrInstruction(condBlock, tempBlock);
        tempBlock = condBlock;
        if (node.two != null) {
            node.two.accept(this);
            new BrInstruction(getValue(node.two), bodyBlock, endBlock, tempBlock);
        } else {
            new BrInstruction(bodyBlock, tempBlock);
        }

        tempBlock = bodyBlock;
        node.body.accept(this);
        new BrInstruction(incBlock, tempBlock);
        tempBlock = incBlock;
        if (node.three != null) {
            node.three.accept(this);
        }
        new BrInstruction(condBlock, tempBlock);
        tempBlock = endBlock;
        temp = temp.father;
    }

    public void visit(IfNode node) {
        var thenBlock = new BasicBlock(rename("if.then"), tempFunc);
        var elseBlock = new BasicBlock(rename("if.else"), tempFunc);
        var endBlock = new BasicBlock(rename("if.end"), tempFunc);
        node.condition.accept(this);
        new BrInstruction(getValue(node.condition), thenBlock, elseBlock, tempBlock);

        tempBlock = thenBlock;
        temp = node.oneScope;
        node.one.accept(this);
        new BrInstruction(endBlock, tempBlock);
        temp = temp.father;

        tempBlock = elseBlock;
        if (node.two != null) {
            temp = node.twoScope;
            node.two.accept(this);
            temp = temp.father;
        }
        new BrInstruction(endBlock, tempBlock);
        tempBlock = endBlock;
    }

    public void visit(WhileNode node) {
        var condBlock = new BasicBlock(rename("while.cond"), tempFunc);
        var bodyBlock = new BasicBlock(rename("while.body"), tempFunc);
        var endBlock = new BasicBlock(rename("while.end"), tempFunc);
        node.scope.ContinueBlock = condBlock;
        node.scope.BreakBlock = endBlock;
        temp = node.scope;
        new BrInstruction(condBlock, tempBlock);
        tempBlock = condBlock;
        node.condition.accept(this);
        new BrInstruction(getValue(node.condition), bodyBlock, endBlock, tempBlock);
        tempBlock = bodyBlock;
        node.body.accept(this);
        new BrInstruction(condBlock, tempBlock);
        tempBlock = endBlock;
        temp = temp.father;
    }

    public void visit(BreakNode node) {
        new BrInstruction(temp.getLoopScope().BreakBlock, tempBlock);
    }

    public void visit(ContinueNode node) {
        new BrInstruction(temp.getLoopScope().ContinueBlock, tempBlock);
    }

    public void visit(ReturnNode node) {
        if (node.returnVal != null) {
            node.returnVal.accept(this);
            var val = getValue(node.returnVal);
            newStore(val, tempFunc.returnValue);
        }
        new BrInstruction(tempFunc.exit, tempBlock);
    }

    public void initGlobalVariable(ProgramNode node) {
        tempFunc = newBuiltinFunc("__global_var_init", false, voidType);
        module.functions.add(tempFunc);
        Program.addFunc("__global_var_init", tempFunc);
        tempFunc.entry = new BasicBlock(rename("entry"), tempFunc);
        tempFunc.exit = new BasicBlock(rename("exit"), tempFunc);
        tempBlock = tempFunc.exit;
        Ret();
        tempBlock = tempFunc.entry;
        for (var i : node.definitions) {
            if (!(i instanceof VariableDefineNode)) {
                continue;
            }
            for (var j : ((VariableDefineNode) i).defines) {
                var v = Program.globalVariables.get(j.name);
                if (j.right == null) {
                    continue;
                }
                j.right.accept(this);
                if (j.right.val instanceof Constant) {
                    v.init = j.right.val;
                } else {
                    newStore(getValue(j.right), v);
                }
            }
        }
        if (!tempBlock.terminated) {
            new BrInstruction(tempFunc.exit, tempBlock);
        }
        tempFunc = null;
        tempBlock = null;
    }

    public void visit(ExpressionStatementNode node) {
        if (node.expr != null) {
            node.expr.accept(this);
        }
    }

    public void visit(BlockNode node) {
        temp = node.scope;
        for (var i : node.statements)
            i.accept(this);
        temp = temp.father;
    }

    public void visit(AssignExpressionNode node) {
        node.rhs.accept(this);
        node.lhs.accept(this);
        newStore(getValue(node.rhs), node.lhs.ptr);
    }

    public void visit(SingleExpressionNode node) {
        if (node.function) {
            if (Class != null) {
                node.val = Class.getFunc(node.context);
            }
            if (node.val == null) {
                node.val = Program.getFunc(node.context);
            }
        } else if (node.left) {
            node.ptr = temp.getVariable(node.context, true);
            if (node.ptr == null) {
                var thisPtr = newLoad("%this", temp.getVariable("this", true), tempBlock);
                var index = Class.getVariableIndex(node.context);
                var type = ClassType.typeList.get(index);
                node.ptr = new GetElementPtrInstruction(rename("%" + node.context),
                        new PointerType(type), thisPtr, tempBlock,
                        new IntConst(0, 32), new IntConst(index, 32));
            }
        } else if (node.context.equals("this")) {
            node.ptr = temp.getVariable("this", true);
        } else {
            var typename = node.type.name;
            switch (typename) {
                case "bool" -> node.val = node.context.equals("true") ? trueConst : falseConst;
                case "int" -> node.val = new IntConst(Integer.parseInt(node.context), 32);
                case "null" -> node.val = new NullConst();
                case "string" -> {
                    var s = getString(node.substitute());
                    node.val = new GetElementPtrInstruction(nextName(), i8PtrType, s,
                            tempBlock, new IntConst(0, 32), new IntConst(0, 32));
                }
            }
        }
    }

    public void visit(BinaryExpressionNode node) {
        node.lhs.accept(this);
        if (node.op.equals("&&")) {
            node.ptr = newAlloca(i1Type, "%land.addr");
            var rhsBlock = new BasicBlock(rename("land.rhs"), tempFunc);
            var shortBlock = new BasicBlock(rename("land.short"), tempFunc);
            var endBlock = new BasicBlock(rename("land.end"), tempFunc);
            new BrInstruction(getValue(node.lhs), rhsBlock, shortBlock, tempBlock);
            tempBlock = rhsBlock;
            node.rhs.accept(this);
            newStore(getValue(node.rhs), node.ptr);
            new BrInstruction(endBlock, tempBlock);
            tempBlock = shortBlock;
            newStore(falseConst, node.ptr);
            new BrInstruction(endBlock, tempBlock);
            tempBlock = endBlock;
            return;
        } else if (node.op.equals("||")) {
            node.ptr = newAlloca(i1Type, "%lor.addr");
            var rhsBlock = new BasicBlock(rename("lor.rhs"), tempFunc);
            var shortBlock = new BasicBlock(rename("lor.short"), tempFunc);
            var endBlock = new BasicBlock(rename("lor.end"), tempFunc);
            new BrInstruction(getValue(node.lhs), shortBlock, rhsBlock, tempBlock);
            tempBlock = shortBlock;
            newStore(trueConst, node.ptr);
            new BrInstruction(endBlock, tempBlock);
            tempBlock = rhsBlock;
            node.rhs.accept(this);
            newStore(getValue(node.rhs), node.ptr);
            new BrInstruction(endBlock, tempBlock);
            tempBlock = endBlock;
            return;
        }
        node.rhs.accept(this);
        if (node.lhs.type.name.equals("string")) {
            node.val = new CallInstruction(
                    nextName(), getStringMethod(node.op), tempBlock,
                    getValue(node.lhs), getValue(node.rhs));
            return;
        }
        String op = switch (node.op) {
            case "==" -> "eq";
            case "!=" -> "ne";
            case ">" -> "sgt";
            case ">=" -> "sge";
            case "<" -> "slt";
            case "<=" -> "sle";
            default -> null;
        };
        if (op != null) {
            node.val = new IcmpInstruction(op, getValue(node.lhs), getValue(node.rhs), nextName(), tempBlock);
            return;
        }
        switch (node.op) {
            case "+" -> op = "add";
            case "-" -> op = "sub";
            case "*" -> op = "mul";
            case "/" -> op = "sdiv";
            case "%" -> op = "srem";
            case "&" -> op = "and";
            case "|" -> op = "or";
            case "^" -> op = "xor";
            case "<<" -> op = "shl";
            case ">>" -> op = "ashr";
        }
        node.val = new BinaryInstruction(op, getValue(node.lhs), getValue(node.rhs), rename("%" + op), tempBlock);
    }

    public void visit(FunctionCallNode node) {
        node.function.accept(this);
        if (!(node.function.val instanceof Function func)) {
            node.val = node.function.val;
            return;
        }
        var paramTypes = func.type().parameters;
        var args = new ArrayList<Value>();
        int offset = 0;
        if (func.member) {
            offset = 1;
            if (node.function instanceof MemberCallNode m) {
                args.add(getValue(m.instance));
            } else {
                var arg_this = newLoad("%this", temp.getVariable("this", true), tempBlock);
                args.add(arg_this);
            }
        }
        for (int i = 0; i < node.args.size(); ++i) {
            var arg = node.args.get(i);
            arg.accept(this);
            var arg_val = getValue(arg);
            arg_val.type = paramTypes.get(i + offset);
            args.add(arg_val);
        }
        node.val = new CallInstruction(nextName(), func, tempBlock, args);
    }


    public void visit(NewExpressionNode node) {
        if (node.type.Array) {
            var sizeVals = new ArrayList<Value>();
            for (var i : node.sizes) {
                i.accept(this);
                sizeVals.add(getValue(i));
            }
            if (!sizeVals.isEmpty()) {
                node.val = newArray(getType(node.type), 0, sizeVals);
            } else {
                node.val = nullConst;
            }
        } else {
            var clsName = node.type.name;
            var clsType = Program.getClassType(clsName);
            var clsScope = Program.getClassScope(clsName);

            var rawPtr = new CallInstruction(rename("%.new.ptr"), Program.getFunc("__malloc"), tempBlock, new IntConst(clsType.size(), 32));
            node.val = new BitCastInstruction(rename("%.new.clsPtr"), new PointerType(clsType), rawPtr, tempBlock);
            new CallInstruction(rename("%.new.ctor"), clsScope.getFunc(clsName), tempBlock, node.val);
        }
    }

    public void visit(PostfixExpressionNode node) {
        node.expr.accept(this);
        node.val = getValue(node.expr);
        Value val = switch (node.op) {
            case "++" -> new BinaryInstruction("add", node.val, new IntConst(1, 32), rename("%inc"), tempBlock);
            case "--" -> new BinaryInstruction("sub", node.val, new IntConst(1, 32), rename("%dec"), tempBlock);
            default -> throw new IllegalStateException("Unexpected value: " + node.op);
        };
        newStore(val, node.expr.ptr);
    }

    public void visit(PrefixExpressionNode node) {
        node.expr.accept(this);
        node.ptr = node.expr.ptr;
        switch (node.op) {
            case "++" ->
                    node.val = new BinaryInstruction("add", getValue(node.expr), new IntConst(1, 32), rename("%inc"), tempBlock);
            case "--" ->
                    node.val = new BinaryInstruction("sub", getValue(node.expr), new IntConst(1, 32), rename("%dec"), tempBlock);
        }
        newStore(node.val, node.ptr);
    }

    public void visit(PreExpressionNode node) {
        node.expr.accept(this);
        switch (node.op) {
            case "+" -> node.val = getValue(node.expr);
            case "-" ->
                    node.val = new BinaryInstruction("sub", new IntConst(0, 32), getValue(node.expr), rename("%sub"), tempBlock);
            case "!" ->
                    node.val = new BinaryInstruction("xor", getValue(node.expr), trueConst, rename("%lnot"), tempBlock);
            case "~" ->
                    node.val = new BinaryInstruction("xor", getValue(node.expr), new IntConst(-1, 32), rename("%neg"), tempBlock);
            default -> {
            }
        }
    }

    public void visit(ArrayCallNode node) {
        node.array.accept(this);
        node.index.accept(this);
        var arr = getValue(node.array);
        node.ptr = new GetElementPtrInstruction(rename("%arrayidx"), arr.type, arr, tempBlock, getValue(node.index));
    }

    public void visit(MemberCallNode node) {
        node.instance.accept(this);
        if (node.instance.type.Array) {
            var ptr = new BitCastInstruction(nextName(), i32PtrType, getValue(node.instance), tempBlock);
            var sizePtr = new GetElementPtrInstruction(nextName(), i32PtrType, ptr, tempBlock, new IntConst(-1, 32));
            node.val = newLoad(nextName(), sizePtr, tempBlock);
        } else if (node.instance.type.name.equals("string")) {
            node.val = Program.getFunc("__str_" + node.member);
        } else {
            var clsName = node.instance.type.name;
            var cls = Program.getClassScope(clsName);
            var clsType = Program.getClassType(clsName);
            if (node.function) {
                node.val = cls.getFunc(node.member);
            } else {
                var idx = cls.getVariableIndex(node.member);
                node.ptr = new GetElementPtrInstruction(rename("%" + node.member),
                        new PointerType(clsType.typeList.get(idx)), getValue(node.instance),
                        tempBlock, new IntConst(0, 32), new IntConst(idx, 32));
            }
        }
    }

    public void visit(TrinaryExpressionNode node) {
        node.condition.accept(this);
        var trueBlock = new BasicBlock(rename("ter.true"), tempFunc);
        var falseBlock = new BasicBlock(rename("ter.false"), tempFunc);
        var endBlock = new BasicBlock(rename("ter.end"), tempFunc);
        var retType = getType(node.type);
        if (retType.equals(voidType)) {
            new BrInstruction(getValue(node.condition), trueBlock, falseBlock, tempBlock);
            tempBlock = trueBlock;
            node.first.accept(this);
            new BrInstruction(endBlock,tempBlock);
            tempBlock = falseBlock;
            node.second.accept(this);
            new BrInstruction(endBlock,tempBlock);
            tempBlock = endBlock;
        } else {
            node.ptr = newAlloca(getMemType(retType),"%ter.addr");
            new BrInstruction(getValue(node.condition), trueBlock, falseBlock, tempBlock);
            tempBlock = trueBlock;
            node.first.accept(this);
            newStore(getValue(node.first),node.ptr);
            new BrInstruction(endBlock,tempBlock);
            tempBlock = falseBlock;
            node.second.accept(this);
            newStore(getValue(node.second),node.ptr);
            new BrInstruction(endBlock,tempBlock);
            tempBlock = endBlock;
        }
    }

    public void visit(ParameterListNode node) {
    }

    public void visit(ParameterNode node) {
    }

    public void visit(TypeNode node) {
    }

    public static BasicType
            i32Type = new IntType(32, false),
            i8Type = new IntType(8, false),
            i8BoolType = new IntType(8, true),
            i1Type = new IntType(1, false),
            i8PtrType = new PointerType(i8Type),
            i32PtrType = new PointerType(i32Type),
            voidType = new VoidType();
    public static IntConst
            trueConst = new IntConst(1, 1),
            falseConst = new IntConst(0, 1);
    public static NullConst nullConst = new NullConst();

    public void ClassDeclare(ClassNode node) {
        StructType Class = new StructType("%class." + node.name);
        module.classes.add(Class);
        Program.addClassType(node.name, Class);
    }

    public void defineClassType(ClassNode node) {
        var Class = Program.getClassType(node.name);
        for (var i : node.definitions) {
            if (i instanceof VariableDefineNode v) {
                for (var j : v.defines)
                    Class.typeList.add(getType(j.type));
            }
        }
    }

    private StoreInstruction newStore(Value value, Value ptr) {
        if (isBool(value.type)) {
            value = new ZextInstruction(value, i8Type, rename("%zext"), tempBlock);
        }
        return new StoreInstruction(value, ptr, tempBlock);
    }

    public BasicType getType(TypeNode type) {
        if (type.Array) {
            var elemType = new TypeNode(type);
            elemType.arraySize--;
            if (elemType.arraySize == 0) {
                elemType.Array = false;
            }
            return new PointerType(getType(elemType));
        }
        if (type.name.equals("string"))
            return i8PtrType;
        if (type.Class) {
            return new PointerType(Program.getClassType(type.name));
        }
        return getElemType(type.name);
    }

    public BasicType getElemType(String typename) {
        if (typename.equals("int")) {
            return i32Type;
        } else if (typename.equals("bool")) {
            return i1Type;
        } else if (typename.equals("string")) {
            return i8PtrType;
        } else if (typename.equals("void")) {
            return voidType;
        } else if (typename.equals("null")) {
            return i32PtrType;
        } else {
            throw new Error("wrong elementary type");
        }
    }

    public void memberFunctionDeclare(ClassNode node) {
        ClassName = node.name;
        ClassType = Program.getClassType(ClassName);
        Class = Program.getClassScope(ClassName);
        for (var i : node.definitions) {
            if (i instanceof FunctionNode functionDef) {
                var funcType = new FunctionType(getType(functionDef.returnType));
                funcType.parameters.add(new PointerType(ClassType)); // "this" pointer
                for (var j : functionDef.parameterlist.parameters) {
                    funcType.parameters.add(getType(j.type));
                }
                var funcName = "@" + ClassName + "." + functionDef.name;
                var func = new Function(funcType, funcName, true);
                module.functions.add(func);
                Class.addFunc(functionDef.name, func);
            }
        }

        var funcType = new FunctionType(voidType);
        funcType.parameters.add(new PointerType(ClassType));
        var funcName = "@" + ClassName + "." + ClassName;
        var func = new Function(funcType, funcName, true);
        module.functions.add(func);
        Class.addFunc(ClassName, func);

        ClassName = null;
        ClassType = null;
        Class = null;
    }

    public Value newArray(BasicType type, int n, ArrayList<Value> sizeValue) {
        var elementType = ((PointerType) type).element;
        Value size = sizeValue.get(n);
        var multi = new BinaryInstruction("mul", size, new IntConst(elementType.size(), 32), rename(nextName()), tempBlock);
        var mallocSize = new BinaryInstruction("add", multi, new IntConst(4, 32), rename("%.new.mallocsize"), tempBlock);
        var rawPtr = new CallInstruction(rename("%.new.ptr"), Program.getFunc("__malloc"), tempBlock, mallocSize);
        var sizePtr = new BitCastInstruction(rename("%.new.sizeptr"), i32PtrType, rawPtr, tempBlock);
        newStore(size, sizePtr);
        var tmpPtr = new GetElementPtrInstruction(nextName(), i8PtrType, rawPtr, tempBlock, new IntConst(4, 32));
        var arrayPtr = new BitCastInstruction(rename("%.new.arrPtr"), getMemType(type), tmpPtr, tempBlock);

        if (n + 1 < sizeValue.size()) {
            var ptrAddr = newAlloca(type, rename("%.new.ptr"));
            newStore(arrayPtr, ptrAddr);
            var endPtr = new GetElementPtrInstruction(rename("%.new.endPtr"), type, arrayPtr, tempBlock, size);

            var condBlock = new BasicBlock(rename("new.while.cond"), tempFunc);
            var bodyBlock = new BasicBlock(rename("new.while.body"), tempFunc);
            var endBlock = new BasicBlock(rename("new.while.end"), tempFunc);

            new BrInstruction(condBlock, tempBlock);
            tempBlock = condBlock;
            var ptr1 = newLoad("%.new.ptr", ptrAddr, tempBlock);
            var cond = new IcmpInstruction("ne", ptr1, endPtr, rename("%.new.cond"), tempBlock);
            new BrInstruction(cond, bodyBlock, endBlock, tempBlock);

            tempBlock = bodyBlock;
            var ptr2 = newLoad("%.new.ptr", ptrAddr, tempBlock);
            newStore(newArray(((PointerType) type).element, n + 1, sizeValue), ptr2);
            newStore(new GetElementPtrInstruction(nextName(), type, ptr2, tempBlock, new IntConst(1, 32)), ptrAddr);
            new BrInstruction(condBlock, tempBlock);

            tempBlock = endBlock;
        }
        return arrayPtr;
    }

    public void declareFunction(FunctionNode node) {
        var funcType = new FunctionType(getType(node.returnType));
        for (var j : node.parameterlist.parameters) {
            funcType.parameters.add(getType(j.type));
        }
        var funcName = node.name.equals("main") ? "@main" : "@func." + node.name;
        var function = new Function(funcType, funcName, false);
        module.functions.add(function);
        Program.addFunc(node.name, function);
    }

    public void declareGlobalVariable(SingleVariableDefineNode node) {
        var name = rename("@" + node.name);
        var type = getType(node.type);
        var v = new GlobalConst(getMemType(type), rename(name));
        module.globalVariables.add(v);
        Program.globalVariables.put(node.name, v);
    }

    public HashMap<String, Integer> identifiers = new HashMap<>();
    public int NameCount = 0;

    public String nextName() {
        return "%." + NameCount++;
    }

    public String rename(String rawName) {
        if (rawName.equals("%sub")) {
            int n = 0;
        }
        var cnt = identifiers.get(rawName);
        String name;
        if (cnt == null) {
            name = rawName;
            cnt = 1;
        } else {
            name = rawName + '.' + cnt;
            cnt += 1;
        }
        identifiers.put(rawName, cnt);
        return name;
    }

    public static boolean isBool(BasicType type) {
        if (!(type instanceof IntType t)) {
            return false;
        }
        return t.memorySize == 1 || t.isBool;
    }

    public BasicType getMemType(BasicType type) {
        if (isBool(type)) {
            return i8BoolType;
        }
        if (type instanceof PointerType ptrType) {
            return new PointerType(getMemType(ptrType.element));
        }
        return type;
    }

    private Value getValue(ExpressionNode node) {
        if (node.val != null)
            return node.val;
        return newLoad(nextName(), node.ptr, tempBlock);
    }

    private AllocaInstruction newAlloca(BasicType type, String name) {
        return new AllocaInstruction(getMemType(type), rename(name), tempFunc.entry);
    }

    private Value newLoad(String Name, Value ptr, BasicBlock parent) {
        var loadInst = new LoadInstruction(rename(Name), ptr, parent);
        if (isBool(((PointerType) ptr.type).element)) {
            return new TruncInstruction(loadInst, i1Type, rename(Name + ".tobool"), tempBlock);
        }
        return loadInst;
    }

    public void addBuiltinFunc(String funcName, boolean isMember, BasicType returnType, BasicType... paramTypes) {
        var func = newBuiltinFunc(funcName, isMember, returnType, paramTypes);
        module.functionsDeclarations.add(func);
        this.Program.addFunc(funcName, func);
    }

    public Function newBuiltinFunc(String funcName, boolean isMember, BasicType returnType, BasicType... paramTypes) {
        var funcType = new FunctionType(returnType);
        Collections.addAll(funcType.parameters, paramTypes);
        funcName = rename("@" + funcName);
        return new Function(funcType, funcName, isMember);
    }

    public void initBuiltIn() {
        addBuiltinFunc("print", false, voidType, i8PtrType);
        addBuiltinFunc("println", false, voidType, i8PtrType);
        addBuiltinFunc("printInt", false, voidType, i32Type);
        addBuiltinFunc("printlnInt", false, voidType, i32Type);
        addBuiltinFunc("getString", false, i8PtrType);
        addBuiltinFunc("getInt", false, i32Type);
        addBuiltinFunc("toString", false, i8PtrType, i32Type);

        addBuiltinFunc("__malloc", false, i8PtrType, i32Type);
        addBuiltinFunc("__str_length", true, i32Type, i8PtrType);
        addBuiltinFunc("__str_substring", true, i8PtrType, i8PtrType, i32Type, i32Type);
        addBuiltinFunc("__str_parseInt", true, i32Type, i8PtrType);
        addBuiltinFunc("__str_ord", true, i32Type, i8PtrType, i32Type);

        addBuiltinFunc("__str_eq", false, i1Type, i8PtrType, i8PtrType);
        addBuiltinFunc("__str_ne", false, i1Type, i8PtrType, i8PtrType);
        addBuiltinFunc("__str_gt", false, i1Type, i8PtrType, i8PtrType);
        addBuiltinFunc("__str_ge", false, i1Type, i8PtrType, i8PtrType);
        addBuiltinFunc("__str_lt", false, i1Type, i8PtrType, i8PtrType);
        addBuiltinFunc("__str_le", false, i1Type, i8PtrType, i8PtrType);

        addBuiltinFunc("__str_cat", false, i8PtrType, i8PtrType, i8PtrType);
    }

    public ReturnInstruction Ret(Value val) {
        return new ReturnInstruction(val, tempFunc.exit);
    }

    public ReturnInstruction Ret() {
        return new ReturnInstruction(tempFunc.exit);
    }

    private StringConst getString(String val) {
        for (var s : module.strings) {
            if (s.itself.equals(val)) {
                return s;
            }
        }
        var s = new StringConst(rename("@.str"), val);
        module.strings.add(s);
        return s;
    }

    public Function getStringMethod(String op) {
        String name = null;
        switch (op) {
            case "==":
                name = "eq";
                break;
            case "!=":
                name = "ne";
                break;
            case ">":
                name = "gt";
                break;
            case ">=":
                name = "ge";
                break;
            case "<":
                name = "lt";
                break;
            case "<=":
                name = "le";
                break;
            case "+":
                name = "cat";
                break;
        }
        // @formatter:on
        return Program.getFunc("__str_" + name);
    }

}
