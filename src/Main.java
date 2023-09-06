import Parser.MxLexer;
import Parser.MxParser;
import asm.asmModule;
import asm.asmPrinter;
import backend.InstructionSelector;
import backend.RegAllocator;
import backend.StackAllocator;
import errors.MxParserErrorListener;
import ir.IRBuilder;
import ir.IRPrinter;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import semantic.Semantic;

import java.io.FileOutputStream;
import java.io.PrintStream;

public class Main {
    public static void main(String[] args) throws Exception {
//        CharStream input = CharStreams(System.in);
        var input = CharStreams.fromStream(System.in);
        var lexer = new MxLexer(input);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new MxParserErrorListener());
        var tokens = new CommonTokenStream(lexer);
        var parser = new MxParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new MxParserErrorListener());
        var semantic = new Semantic(parser.program());

        var irBuilder = new IRBuilder(semantic.programScope);
        irBuilder.visit(semantic.root);
        var llFile = new FileOutputStream("out.ll");
        var ll = new PrintStream(llFile);
        var irPrinter = new IRPrinter(ll, "input.mx");
        irPrinter.print(irBuilder.module);
        llFile.close();

        var ASMModule = new asmModule();
        new InstructionSelector(ASMModule).visit(irBuilder.module);
        new RegAllocator().runOnModule(ASMModule);
        new StackAllocator().runOnModule(ASMModule);

        var asmFile = new FileOutputStream("output.s");
        var asm = new PrintStream(System.out);
        new asmPrinter(asm).runOnModule(ASMModule);
        asmFile.close();
    }
}