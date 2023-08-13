package semantic;

import ast.*;
import ast.expressions.*;
import ast.statement.*;
import scope.ClassScope;
import scope.GlobalScope;

public class SymbolCollector implements Visitor {
    public GlobalScope ProgramScope;
    public ClassScope classScope;
    //used for temporary save

    public SymbolCollector(GlobalScope g) {
        this.ProgramScope = g;
    }

    public void visit(ProgramNode node) {
        for (var i : node.definitions) {
            if (!(i instanceof VariableDefineNode)) {
                i.accept(this);
            }
        }
    }

    public void visit(ClassNode node) {
        ClassScope scope = new ClassScope(node.name,this.ProgramScope,node.pos);
        ProgramScope.addClassScope(scope);
        this.classScope = scope;
        for (var i : node.definitions) {
            i.accept(this);
        }
        this.classScope = null;
    }

    public void visit(ConstructorNode node) {
        this.classScope.addConstructor(node);
    }

    public void visit(FunctionNode node) {
        if (this.classScope != null) {
            this.classScope.addFunctionDefine(node);
        } else {
            this.ProgramScope.addFunction(node);
        }
    }

    public void visit(VariableDefineNode node) {
        for (var i : node.defines) {
            i.accept(this);
        }
    }

    public void visit(SingleVariableDefineNode node) {
        if (node.right != null) {
            throw new Error("Sorry but no initialization of variables in classes in Mx");
        }
        this.classScope.DefineVariable(node);
    }

    // everything is done but there's grammar problem so functions below are useless;

    public void visit(ForNode node) {}

    public void visit(IfNode node) {}

    public void visit(WhileNode node) {}

    public void visit(BreakNode node) {}

    public void visit(ContinueNode node) {}

    public void visit(ReturnNode node) {}

    public void visit(ExpressionStatementNode node) {}

    public void visit(BlockNode node) {}

    public void visit(TypeNode node) {}

    public void visit(AssignExpressionNode node) {}

    public void visit(SingleExpressionNode node) {}

    public void visit(BinaryExpressionNode node) {}

    public void visit(FunctionCallNode node) {}

    public void visit(ParameterListNode node) {}

    public void visit(ArrayCallNode node) {}

    public void visit(MemberCallNode node) {}

    public void visit(NewExpressionNode node) {}

    public void visit(PostfixExpressionNode node) {}

    public void visit(PrefixExpressionNode node) {}

    public void visit(PreExpressionNode node) {}

    public void visit(TrinaryExpressionNode node) {}

    public void visit(ParameterNode node) {}
}
