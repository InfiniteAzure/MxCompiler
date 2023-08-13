package semantic;

import Parser.MxParser;
import ast.ASTBuilder;
import ast.ProgramNode;
import scope.GlobalScope;

public class Semantic {

    public ProgramNode root;
    public GlobalScope programScope;

    public Semantic(MxParser.ProgramContext programContext) {
        ASTBuilder builder = new ASTBuilder();
        this.root = (ProgramNode) builder.visit(programContext);
        this.programScope = new GlobalScope();
        SymbolCollector symbolCollector = new SymbolCollector(programScope);
        symbolCollector.visit(root);
        SemanticChecker semanticChecker = new SemanticChecker(programScope);
        semanticChecker.visit(root);
    }
}
