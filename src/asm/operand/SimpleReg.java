package asm.operand;

import asm.Operand;
import backend.IGNode;

public class SimpleReg extends Operand {
    public SimpleReg color;
    /** stack offset of spilled register */
    public Stack stackOffset;
    /** interference graph node */
    public IGNode node = new IGNode(this);
    /** node in the interference graph of spilled registers */
    public IGNode nodeSpilled = new IGNode(this);

    @Override
    public String toString() {
        if (color == null || color == this)
            return super.toString();
        return color.toString();
    }
}
