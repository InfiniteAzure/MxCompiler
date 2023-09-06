package asm;

import java.io.PrintStream;

public class asmPrinter implements Pass{
    private PrintStream print;

    public asmPrinter(PrintStream Print) {
        this.print = Print;
    }

    public void runOnModule(asmModule module) {
        if (!module.globalVars.isEmpty()) {
            print.println("\t.data");
        }
        for (var v : module.globalVars) {
            print.printf("\t.globl\t%s\n", v.name);
            print.printf("%s:\n", v.name);
            print.printf("\t%s\t%d\n", v.size == 4 ? ".word" : ".byte", v.initVal);
        }

        if (!module.stringConsts.isEmpty()) {
            print.println("\t.rodata\n");
        }
        for (var s : module.stringConsts) {
            print.printf("\t.globl\t%s\n", s.name);
            print.printf("%s:\n", s.name);
            print.printf("\t.asciz\t\"%s\"\n", s.escaped());
        }
        print.println("\t.text");
        module.funcs.forEach(this::runOnFunc);
    }

    public void runOnFunc(Function func) {
        print.printf("\t.globl\t%s\n", func.label);
        print.printf("%s:\n", func.label);
        func.blocks.forEach(this::runOnBlock);
    }

    public void runOnBlock(Block block) {
        print.print(block.label + ":\n");
        block.instructions.forEach(inst -> print.println("\t" + inst));
    }
}
