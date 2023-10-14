package asm;

import java.io.PrintStream;

public class asmPrinter implements Pass{
    public PrintStream print;

    public asmPrinter(PrintStream p) {
        this.print = p;
    }

    @Override
    public void runOnModule(asmModule module) {
        for (var v : module.globalVars) {
            print.printf("\t.data\n");
            print.printf("\t.globl %s\n", v.name);
            print.printf("%s:\n", v.name);
            if (v.size == 1)
                print.printf("\t.byte %d\n", v.initVal);
            else
                print.printf("\t.word %d\n", v.initVal);
        }

        for (var s : module.stringConsts) {
            print.printf("\t.rodata\n");
            print.printf("\t.globl %s\n", s.name);
            print.printf("%s:\n", s.name);
            print.printf("\t.asciz \"%s\"\n", s.escaped());
        }

        print.println("\t.text");
        module.funcs.forEach(this::runOnFunc);
    }

    @Override
    public void runOnFunc(Function func) {
        print.printf("\t.globl\t%s\n", func.label);
        print.printf("%s:\n", func.label);
        func.blocks.forEach(this::runOnBlock);
    }

    @Override
    public void runOnBlock(Block block) {
        print.print(block.label + ":\n");
        block.instructions.forEach(inst -> print.println("\t" + inst));
    }
}
