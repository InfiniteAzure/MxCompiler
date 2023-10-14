import Parser.MxLexer;
import Parser.MxParser;
import backend.Backend;
import errors.MxParserErrorListener;
import optimize.MiddleEnd;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import semantic.Semantic;

public class Main {
    public static void main(String[] args) throws Exception {
        var input = CharStreams.fromStream(System.in);
        var lexer = new MxLexer(input);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new MxParserErrorListener());
        var tokens = new CommonTokenStream(lexer);
        var parser = new MxParser(tokens);
        parser.removeErrorListeners();

        parser.addErrorListener(new MxParserErrorListener());

        var frontEnd = new Semantic(parser.program());
        var middleEnd = new MiddleEnd(frontEnd);
        var backEnd = new Backend(middleEnd);
    }
}