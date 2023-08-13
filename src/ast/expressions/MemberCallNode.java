package ast.expressions;

import ast.Visitor;
import ast.statement.SingleVariableDefineNode;
import tools.Position;

public class MemberCallNode extends ExpressionNode {
    public ExpressionNode instance;
    public String member;
    public SingleVariableDefineNode varDef;

    public MemberCallNode(ExpressionNode Instance, String Member, Position pos) {
        super(pos, null, true);
        this.instance = Instance;
        this.member = Member;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}