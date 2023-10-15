package backend;

import asm.asmPrinter;
import optimize.MiddleEnd;

import java.io.FileOutputStream;
import java.io.PrintStream;

public class Backend {
    MiddleEnd middleEnd;
    asm.asmModule asmModule;

    public Backend(MiddleEnd middleEnd) throws Exception {
        this.middleEnd = middleEnd;

        this.asmModule = new asm.asmModule();
        new InstructionSelector(asmModule).visit(middleEnd.irModule);
        //printAsm("output.s.no.regalloc");
        new withoutRegAlloca().runOnModule(asmModule);
        new StackAllocator().runOnModule(asmModule);
        new BlockMerge().runOnModule(asmModule);
        new BlockReorder().runOnModule(asmModule);
        //printAsm("output-placement.s");
        new DIE().runOnModule(asmModule);

        printAsm("builtin.s");
    }

    void printAsm(String s) throws Exception {
        var asmFile = new FileOutputStream(s);
        var asm = new PrintStream(asmFile);
        new asmPrinter(System.out).runOnModule(asmModule);
        asmFile.close();
    }
}
