package scope;

import ir.BasicBlock;

public class LoopScope extends Scope{

    public BasicBlock ContinueBlock,BreakBlock;
    public LoopScope(Scope Father) {
        super(Father);
    }
}
