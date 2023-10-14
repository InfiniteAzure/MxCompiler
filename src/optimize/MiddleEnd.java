package optimize;

import ir.IRBuilder;
import ir.IRPrinter;
import semantic.Semantic;

import java.io.FileOutputStream;
import java.io.PrintStream;

public class MiddleEnd {
    public IRBuilder irBuilder;
    public ir.Module irModule;

    public MiddleEnd(Semantic semantic) throws Exception {
        this.irBuilder = new IRBuilder(semantic.programScope);
        this.irBuilder.visit(semantic.root);
        this.irModule = this.irBuilder.module;

        var cfg = new CFG();
        cfg.runOnModule(irModule);
        debugPrint("out.ll");

        var mem2reg = new Mem2Reg(irBuilder);
        this.irModule.functions.forEach(mem2reg::runOnFunc);
        debugPrint("out-mem2reg.ll");

        new ADCE().runOnModule(irModule);
        debugPrint("out-adce.ll");

        new CFGSimplifier().runOnModule(irModule);
        debugPrint("out-simplifycfg.ll");

        new LICM(irBuilder).runOnModule(irModule);
        debugPrint("out-licm.ll");

        new CFGSimplifier().runOnModule(irModule);
        debugPrint("out-simplifycfg2.ll");

        new Peephole().runOnModule(irModule);
        debugPrint("out-peephole.ll");

        new PhiElimination().runOnModule(irModule);
        debugPrint("out-phi-elim.ll");

        cfg.runOnModule(irModule);
        debugPrint("out-final.ll");

    }
    public void debugPrint(String filename) throws Exception {
        var llFile = new FileOutputStream(filename);
        var ll = new PrintStream(llFile);
        var irPrinter = new IRPrinter(ll, "input.mx");
        irPrinter.print(this.irModule);
        llFile.close();
    }
}
