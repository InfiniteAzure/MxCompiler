package asm;

public interface Pass {
    public void runOnBlock(Block block);
    public void runOnFunc(Function func);
    public void runOnModule(asmModule module);
}
