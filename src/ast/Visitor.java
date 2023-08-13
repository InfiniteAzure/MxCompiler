package ast;

import ast.expressions.*;
import ast.statement.*;

public interface Visitor {
    void visit(ProgramNode node);
    void visit(ClassNode node);
    void visit(ConstructorNode node);
    void visit(FunctionNode node);
    void visit(ParameterListNode node);
    void visit(ParameterNode node);
    void visit(TypeNode node);

    void visit(ArrayCallNode node);
    void visit(AssignExpressionNode node);
    void visit(BinaryExpressionNode node);
    void visit(FunctionCallNode node);
    void visit(MemberCallNode node);
    void visit(NewExpressionNode node);
    void visit(PostfixExpressionNode node);
    void visit(PrefixExpressionNode node);
    void visit(SingleExpressionNode node);
    void visit(PreExpressionNode node);
    void visit(TrinaryExpressionNode node);

    void visit(BlockNode node);
    void visit(BreakNode node);
    void visit(ContinueNode node);
    void visit(ExpressionStatementNode node);
    void visit(ForNode node);
    void visit(IfNode node);
    void visit(ReturnNode node);
    void visit(SingleVariableDefineNode node);
    void visit(VariableDefineNode node);
    void visit(WhileNode node);
}
