package semantic;

import ast.*;
import ast.expressions.*;
import ast.statement.*;
import scope.*;

public class SemanticChecker implements Visitor {
    public GlobalScope ProgramScope;
    public Scope temp;

    public SemanticChecker(GlobalScope program) {
        this.ProgramScope = program;
        this.temp = this.ProgramScope;
        this.initialize();
    }

    public void initialize() {
        var IntType = new TypeNode(null,false,"int");
        var VoidType = new TypeNode(null,false,"void");
        var StringType = new TypeNode(null,true,"string");
        this.ProgramScope.addFunction(addFunc("print",VoidType,StringType));
        this.ProgramScope.addFunction(addFunc("println",VoidType,StringType));
        this.ProgramScope.addFunction(addFunc("printInt",VoidType,IntType));
        this.ProgramScope.addFunction(addFunc("printlnInt",VoidType,IntType));
        this.ProgramScope.addFunction(addFunc("getString",StringType));
        this.ProgramScope.addFunction(addFunc("getInt",IntType));
        this.ProgramScope.addFunction(addFunc("toString",StringType,IntType));
        this.ProgramScope.addFunction(addFunc("size",IntType));

        ClassScope StringClass = new ClassScope("string",this.ProgramScope,null);
        StringClass.addFunctionDefine(addFunc("length",IntType));
        StringClass.addFunctionDefine(addFunc("substring",StringType,IntType,IntType));
        StringClass.addFunctionDefine(addFunc("parseInt",IntType));
        StringClass.addFunctionDefine(addFunc("ord",IntType,IntType));
        this.ProgramScope.addClassScope(StringClass);
    }

    public FunctionNode addFunc(String name, TypeNode Return, TypeNode... Parameters) {
        var ParameterList = new ParameterListNode(null);
        for (var i : Parameters) {
            ParameterList.parameters.add(new ParameterNode(null,i, ""));
        }
        var ret = new FunctionNode(null,name,Return,null,ParameterList);
        return ret;
    }

    public void visit(ProgramNode node) {
        var Main = this.ProgramScope.getFunction("main");
        if (Main == null) {
            throw new Error("no main function in the program");
        }
        if (!(Main.returnType.name.equals("int")&& !Main.returnType.Array)) {
            throw new Error("main function must return int");
        }
        if (!Main.parameterlist.parameters.isEmpty()) {
            throw new Error("main function should not have any parameters");
        }
        for (var i : node.definitions) {
            i.accept(this);
        }
    }

    public void visit(ClassNode node) {
        this.temp = this.ProgramScope.getClassScope(node.name);
        for (var i : node.definitions) {
            i.accept(this);
        }
        this.temp = this.temp.father;
    }

    public void visit(ConstructorNode node) {
        node.func = new FunctionScope(new TypeNode(null, false, "void"), this.temp);
        this.temp = node.func;
        node.block.accept(this);
        this.temp = this.temp.father;
    }

    public void visit(FunctionNode node) {
        node.returnType.accept(this);
        this.temp = node.func = new FunctionScope(node.returnType, this.temp);
        node.parameterlist.accept(this);
        node.block.accept(this);
        if (!node.name.equals("main") && !node.returnType.name.equals("void") && !node.func.checkReturn) {
            throw new Error("functions that are not void should have a return statement");
        }
        this.temp = this.temp.father;
    }

    public void visit(VariableDefineNode node) {
        for (var i : node.defines) {
            i.accept(this);
        }
    }

    public void visit(SingleVariableDefineNode node) {
        node.type.accept(this);
        if (node.right != null) {
            node.right.accept(this);
            if (!node.right.type.check(node.type) &&
                    (!node.type.Class && !node.type.Array && !(node.right.type.name.equals("null")) && !node.right.type.Array)) {
                throw new Error("wrong initialize: different types can not convert to each other");
            }
        }
        if (!(this.temp instanceof ClassScope)) {
            this.temp.DefineVariable(node);
        }
    }

    public void visit(ForNode node) {
        this.temp = node.scope = new LoopScope(temp);
        if (node.one != null) {
            node.one.accept(this);
        }
        if (node.two != null) {
            node.two.accept(this);
        }
        if (node.two != null) {
            node.two.accept(this);
            if (!(node.two.type.name.equals("bool") && !node.two.type.Array)) {
                throw new Error("the third expression in for loop should be bool");
            }
        }
        if (node.three != null) {
            node.three.accept(this);
        }
        node.body.accept(this);
        this.temp = this.temp.father;
    }

    public void visit(IfNode node) {
        node.condition.accept(this);
        if (!(node.condition.type.name.equals("bool") && !node.condition.type.Array)) {
            throw new Error("if condition must be a bool");
        }
        this.temp = node.oneScope = new Scope(this.temp);
        node.one.accept(this);
        this.temp = this.temp.father;
        if (node.two != null) {
            this.temp = node.twoScope = new Scope(this.temp);
            node.two.accept(this);
            this.temp = this.temp.father;
        }
    }

    public void visit(WhileNode node) {
        this.temp = node.scope = new LoopScope(temp);
        node.condition.accept(this);
        if (!(node.condition.type.name.equals("bool") && !node.condition.type.Array)) {
            throw new Error("the condition of while should be bool");
        }
        node.body.accept(this);
        this.temp = this.temp.father;
    }

    public void visit(BreakNode node) {
        if (!this.temp.insideLoop()) {
            throw new Error("break statement should exist in a loop");
        }
    }

    public void visit(ContinueNode node) {
        if (!this.temp.insideLoop()) {
            throw new Error("continue statement should exist in a loop");
        }
    }

    public void visit(ReturnNode node) {
        TypeNode type = new TypeNode(null, false,"void");
        if (node.returnVal != null) {
            node.returnVal.accept(this);
            type = node.returnVal.type;
        }
        var funcScope = this.temp.getFunctionScope();
        var returnType = this.temp.getReturnType();
        if (!type.assignmentCheck(returnType)) {
            throw new Error("wrong return type in function");
        }
        funcScope.checkReturn = true;
    }

    public void visit(ExpressionStatementNode node) {
        if (node.expr != null) {
            node.expr.accept(this);
        }
    }

    public void visit(BlockNode node) {
        this.temp = node.scope = new Scope(temp);
        for (var i : node.statements) {
            i.accept(this);
        }
        this.temp = this.temp.father;
    }

    public void visit(TypeNode node) {
        if (node.Class) {
            var thisClass = this.ProgramScope.getClassScope(node.name);
            if (thisClass == null) {
                throw new Error("error occurred in finding the class");
            }
        }
    }

    public void visit(AssignExpressionNode node) {
        node.lhs.accept(this);
        node.rhs.accept(this);
        if (!node.rhs.type.assignmentCheck(node.lhs.type)) {

            throw new Error("cannot assign '" +
                    node.rhs.type.name + "' type to '" +
                    node.lhs.type.name + "' type");
        }
        if (!node.lhs.left) {
            throw new Error("can not assign value to a left value");
        }
        node.type = new TypeNode(node.lhs.type);
    }

    public void visit(SingleExpressionNode node) {
        var Class = this.temp.getClassScope();
        if (node.type.name != null && node.type.name.equals("this")) {
            if (Class == null) {
                throw new Error("can not use this in this function");
            }
            node.type.name = Class.name;
        } else if (node.type.name == null) {
            if (node.function) {
                FunctionNode functionDefine = null;
                if (Class != null) {
                    functionDefine = Class.getFuncDef(node.context);
                }
                if (functionDefine == null) {
                    functionDefine = this.ProgramScope.getFunction(node.context);
                }
                if (functionDefine == null) {
                    throw new Error("the function used has not been defined");
                }
                node.func = functionDefine;
            } else {
                var variable = this.temp.getVariableDefine(node.context, true);
                if (variable == null) {
                    throw new Error("the variable used has not been defined");
                }
                node.variable = variable;
                node.type = variable.type;
            }
        }
    }

    public void visit(BinaryExpressionNode node) {
        node.lhs.accept(this);
        node.rhs.accept(this);
        var op = node.op;
        var leftType = node.lhs.type;
        var rightType = node.lhs.type;
        if (op.equals("==") || op.equals("!=")) {
            if (!leftType.check(rightType) && !leftType.assignmentCheck(rightType) && !rightType.assignmentCheck(leftType))
                throw new Error("can not match types of lhs and rhs");
            node.type = new TypeNode(node.pos, false,"bool");
            return;
        }
        if (!leftType.check(node.rhs.type)) {
            throw new Error("can not match types of lhs and rhs");
        }
        if (op.equals("&&") || op.equals("||")) {
            if (!(leftType.name.equals("bool") && !leftType.Array)) {
                throw new Error("invalid type in binary expression");
            }
            node.type = new TypeNode(node.pos, false, "bool");
        } else if (op.equals("<") || op.equals("<=") ||
                op.equals(">") || op.equals(">=")) {
            if (!(leftType.name.equals("string") && !leftType.Array) &&
                    !(leftType.name.equals("int") && !leftType.Array)) {
                throw new Error("invalid type in binary expression");
            }
            node.type = new TypeNode(node.pos, false, "bool");
        } else {
            if (op.equals("+") && (leftType.name.equals("string") && !leftType.Array)) {
                node.type = new TypeNode(node.pos, true, "string");
                return;
            }
            if (!(leftType.name.equals("int") && !leftType.Array)) {
                throw new Error("invalid type in binary expression");
            }
            node.type = new TypeNode(node.pos, false, "int");
        }
    }

    public void visit(FunctionCallNode node) {
        node.function.accept(this);
        var params = node.function.func.parameterlist.parameters;
        if (params.size() != node.args.size()) {
            throw new Error("argument number does not match");
        }
        for (int i = 0; i < node.args.size(); ++i) {
            var arg = node.args.get(i);
            arg.accept(this);
            var param = params.get(i);
            if (!arg.type.assignmentCheck(param.type)) {
                throw new Error("argument type does not match function definition");
            }
        }
        node.type = new TypeNode(node.function.func.returnType);
    }

    public void visit(ParameterListNode node) {
        for (var i : node.parameters) {
            i.accept(this);
        }
    }

    public void visit(ParameterNode node) {
        this.temp.DefineVariable(new SingleVariableDefineNode( node.pos,node.type, node.name, null));
    }

    public void visit(ArrayCallNode node) {
        node.array.accept(this);
        if (!node.array.type.Array) {
            throw new Error("can not access array-call in a non-array type");
        }
        node.index.accept(this);
        if (!(node.index.type.name.equals("int") && !node.index.type.Array)) {
            throw new Error("the index of an array must be a non-array int");
        }
        node.type = new TypeNode(node.array.type);
        node.type.arraySize--;
        if (node.type.arraySize == 0) {
            node.type.Array = false;
        }
    }

    public void visit(MemberCallNode node) {
        node.instance.accept(this);
        if (node.instance.type.Array) {
            if (!node.function) {
                throw new Error("member call can not be assigned to arrays");
            }
            if (!node.member.equals("size")) {
                throw new Error("wrong member function for array type");
            }
            node.func = this.ProgramScope.getFunction("size");
            node.type = new TypeNode(node.func.returnType);
            return;
        }
        if (!node.instance.type.Class) {
            throw new Error("member function can not be used out of a class");
        }
        var clsName = node.instance.type.name;
        var Class = this.ProgramScope.getClassScope(clsName);
        if (node.function) {
            var funcDef = Class.getFuncDef(node.member);
            if (funcDef == null) {
                throw new Error("can not find this member function in class:" + Class.name);
            }
            node.func = funcDef;
            node.type = new TypeNode(node.func.returnType);
        } else {
            var varDef = Class.getVariableDefine(node.member, false);
            if (varDef == null) {

                throw new Error("can not find this member variable in class:" + Class.name);
            }
            node.varDef = varDef;
            node.type = new TypeNode(node.varDef.type);
        }
    }

    public void visit(NewExpressionNode node) {
        for (var i : node.sizes) {
            i.accept(this);
            if (!(i.type.name.equals("int") && !i.type.Array)) {
                throw new Error("array size expression must be an integer");
            }
        }
        node.type.accept(this);
    }

    public void visit(PostfixExpressionNode node) {
        node.expr.accept(this);
        if (!(node.expr.type.name.equals("int") && !node.expr.type.Array)) {
            throw new Error("Postfix operation can only be assigned to integers");
        }
        if (!node.expr.left) {
            throw new Error("only left values can have postfix operations");
        }
        node.type = new TypeNode(node.expr.type);
    }

    public void visit(PrefixExpressionNode node) {
        node.expr.accept(this);
        if (!(node.expr.type.name.equals("int") && !node.expr.type.Array)) {
            throw new Error("Prefix operation can only be assigned to integers");
        }
        if (!node.expr.left) {
            throw new Error("only left values can have prefix operations");
        }
        node.type = new TypeNode(node.expr.type);
    }

    public void visit(PreExpressionNode node) {
        node.expr.accept(this);
        var op = node.op;
        if (op.equals("+") || op.equals("-") || op.equals("~")) {
            if (!(node.expr.type.name.equals("int") && !node.expr.type.Array)) {
                throw new Error("pre-expressions should only be applied to integers");
            }
        } else {
            if (!(node.expr.type.name.equals("bool") && !node.expr.type.Array)) {
                throw new Error("'!' should only be applied to booleans");
            }
        }
        node.type = new TypeNode(node.expr.type);
    }

    public void visit(TrinaryExpressionNode node) {
        node.condition.accept(this);
        if (!(node.condition.type.name.equals("bool") && !node.condition.type.Array)) {
            throw new Error("the condition expression should be of boolean type");
        }
        node.first.accept(this);
        node.second.accept(this);
        if (node.first.type.name.equals("null")) {
            if (!node.second.type.name.equals("null") && !(node.second.type.Class || node.second.type.Array)) {
                throw new Error("two expressions should have the same return type");
            }
            node.type = node.second.type;
        } else {
            if (node.second.type.name.equals("null")) {
                if (node.first.type.Class || node.first.type.Array) {
                    node.type = node.first.type;
                } else {
                    throw new Error("two expressions should have the same return type");
                }
            } else {
                if (!node.first.type.name.equals(node.second.type.name)) {
                    throw new Error("two expressions should have the same return type");
                }
                node.type = node.first.type;
            }
        }
    }
}
