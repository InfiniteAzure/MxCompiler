package ast;

import Parser.MxParser;
import Parser.MxParser.ClassContext;
import Parser.MxParser.FunctionContext;
import Parser.MxParser.VariableContext;
import Parser.MxParserBaseVisitor;
import ast.expressions.*;
import ast.statement.*;
import tools.Position;

public class ASTBuilder extends MxParserBaseVisitor<ASTnode> {

    public ASTnode visitProgram(Parser.MxParser.ProgramContext ctx) {
        ProgramNode root = new ProgramNode(new Position(ctx));
        for (var i: ctx.children) {
            if (!(i instanceof ClassContext) && !(i instanceof FunctionContext) && !(i instanceof VariableContext)) {
                continue;
            }
            ASTnode define = visit(i);
            root.definitions.add(define);
        }
        return root;
    }

    @Override
    public ASTnode visitFunction(MxParser.FunctionContext ctx) {
        var returnType = (TypeNode) visit(ctx.returnType());
        var block = (BlockNode) visit(ctx.block());
        ParameterListNode params = new ParameterListNode(new Position(ctx));
        if (ctx.parameterlist() != null) {
            params = (ParameterListNode) visit(ctx.parameterlist());
        }
        var ret = new FunctionNode(new Position(ctx), ctx.Identifier().getText(), returnType, block, params);
        return ret;
    }

    public ASTnode visitReturnType(MxParser.ReturnTypeContext ctx) {
        if (ctx.Void() != null) {
            return new TypeNode( new Position(ctx),false, "void");
        }
        return visit(ctx.arraytype());
    }

    public ASTnode visitParameterlist(Parser.MxParser.ParameterlistContext ctx) {
        var ret = new ParameterListNode(new Position(ctx));
        for (var i : ctx.parameter()) {
            ret.add((ParameterNode) visit(i));
        }
        return ret;
    }

    public ASTnode visitParameter(Parser.MxParser.ParameterContext ctx) {
        TypeNode type = (TypeNode) visit(ctx.arraytype());
        return new ParameterNode( new Position(ctx),type, ctx.Identifier().getText());
    }

    public ASTnode visitClass(ClassContext ctx) {
        var ret = new ClassNode(new Position(ctx),ctx.Identifier().getText());
        for (var i : ctx.variable()) {
            ret.definitions.add(visit(i));
        }
        for (var i : ctx.constructor()) {
            var node = (ConstructorNode) visit(i);
            if (!node.name.equals(ret.name)) {
                throw new Error("Wrong constructor name: should match class name");
            }
            ret.definitions.add(node);
        }
        for (var i : ctx.function()) {
            ret.definitions.add(visit(i));
        }
        return ret;
    }

    public ASTnode visitConstructor(Parser.MxParser.ConstructorContext ctx) {
        var ret = new ConstructorNode(new Position(ctx),ctx.Identifier().getText(), (BlockNode) visit(ctx.block()));
        return ret;
    }

    public ASTnode visitVariable(VariableContext ctx) {
        TypeNode type = (TypeNode) visit(ctx.arraytype());
        var ret = new VariableDefineNode(new Position(ctx),type);
        for (var i : ctx.assignment()) {
            ExpressionNode initExpression = null;
            if (i.expression() != null) {
                initExpression = (ExpressionNode) visit(i.expression());
            }
            ret.defines.add(new SingleVariableDefineNode(new Position(ctx),type, i.Identifier().getText(), initExpression));
        }
        return ret;
    }

    public ASTnode visitArraytype(Parser.MxParser.ArraytypeContext ctx) {
        int dim = ctx.BracketLeft().size();
        var baseType = ctx.type();
        boolean isClass = baseType.Identifier() != null || baseType.getText().equals("string");
        if (dim == 0) {
            return new TypeNode(new Position(ctx), isClass, baseType.getText());
        }
        return new TypeNode(new Position(ctx), isClass, baseType.getText(),dim);
    }

    public ASTnode visitBlock(Parser.MxParser.BlockContext ctx) {
        var ret = new BlockNode(new Position(ctx));
        for (var i : ctx.statement()) {
            ret.statements.add((StatementNode) visit(i));
        }
        return ret;
    }

    public ASTnode visitStatement(Parser.MxParser.StatementContext ctx) {
        if (ctx.block() != null) {
            return visit(ctx.block());
        } else if (ctx.variable() != null) {
            return visit(ctx.variable());
        } else if (ctx.if_() != null) {
            return visit(ctx.if_());
        } else if (ctx.while_() != null) {
            return visit(ctx.while_());
        } else if (ctx.for_() != null) {
            return visit(ctx.for_());
        } else if (ctx.break_() != null) {
            return visit(ctx.break_());
        } else if (ctx.continue_() != null) {
            return visit(ctx.continue_());
        } else if (ctx.return_() != null) {
            return visit(ctx.return_());
        } else if (ctx.expressionStat() != null) {
            return visit(ctx.expressionStat());
        } else if (ctx.Semi() != null) {
            return new ExpressionStatementNode(new Position(ctx),null);
        }
        throw new Error("Unexpected statement");
    }

    public ASTnode visitIf(Parser.MxParser.IfContext ctx) {
        ExpressionNode condition = (ExpressionNode) visit(ctx.expression());
        StatementNode One, Two = null;
        One = (StatementNode) visit(ctx.statement(0));
        if (ctx.statement().size() > 1) {
            Two = (StatementNode) visit(ctx.statement(1));
        }
        return new IfNode( new Position(ctx),condition, One, Two);
    }

    public ASTnode visitWhile(Parser.MxParser.WhileContext ctx) {
        ExpressionNode condition = (ExpressionNode) visit(ctx.expression());
        StatementNode body = (StatementNode) visit(ctx.statement());
        return new WhileNode(new Position(ctx), condition, body);
    }

    public ASTnode visitFor(Parser.MxParser.ForContext ctx) {
        var init = ctx.forfirst();
        VariableDefineNode initVariable = null;
        ExpressionNode initExpr = null, forsecond = null, forthird = null;
        if (init.variable() != null) {
            initVariable = (VariableDefineNode) visit(init.variable());
        } else if (init.expression() != null) {
            initExpr = (ExpressionNode) visit(init.expression());
        }
        if (ctx.forsecond != null) {
            forsecond = (ExpressionNode) visit(ctx.forsecond);
        }
        if (ctx.forthird != null) {
            forthird = (ExpressionNode) visit(ctx.forthird);
        }
        StatementNode body = (StatementNode) visit(ctx.statement());
        return new ForNode(initVariable, initExpr, forsecond, forthird, body,new Position(ctx));
    }

    public ASTnode visitBreak(Parser.MxParser.BreakContext ctx) {
        return new BreakNode(new Position(ctx));
    }

    public ASTnode visitContinue(Parser.MxParser.ContinueContext ctx) {
        return new ContinueNode(new Position(ctx));
    }

    public ASTnode visitReturn(Parser.MxParser.ReturnContext ctx) {
        ExpressionNode expr = null;
        if (ctx.expression() != null) {
            expr = (ExpressionNode) visit(ctx.expression());
        }
        return new ReturnNode(new Position(ctx),expr);
    }

    public ASTnode visitExpressionStat(Parser.MxParser.ExpressionStatContext ctx) {
        return new ExpressionStatementNode(new Position(ctx),(ExpressionNode) visit(ctx.expression()));
    }

    public ASTnode visitNewExpression(Parser.MxParser.NewExpressionContext ctx) {
        TypeNode type = new TypeNode(ctx.type());
        if (ctx.newSize().size() > 0) {
            type.Array = true;
            type.arraySize = ctx.newSize().size();
        }
        var ret = new NewExpressionNode(new Position(ctx), type);

        boolean flag = true, forceEmpty = false;
        for (var i : ctx.newSize()) {
            if (flag) {
                if (i.expression() == null) {
                    throw new Error("Expected array size in new expression");
                }
                flag = false;
            }
            if (forceEmpty && i.expression() != null) {
                throw new Error("Wrong initialization of multiArray");
            }
            if (i.expression() == null) {
                forceEmpty = true;
            } else {
                ret.sizes.add((ExpressionNode) visit(i.expression()));
            }
        }
        return ret;
    }

    public ASTnode visitArrayCall(Parser.MxParser.ArrayCallContext ctx) {
        ArrayCallNode ret = new ArrayCallNode((ExpressionNode) visit(ctx.expression(0)),(ExpressionNode) visit(ctx.expression(1)), new Position(ctx));
        return ret;
    }

    public ASTnode visitPreFixExpression(Parser.MxParser.PreFixExpressionContext ctx) {
        PrefixExpressionNode ret = new PrefixExpressionNode(new Position(ctx),(ExpressionNode) visit(ctx.expression()),ctx.op.getText());
        return ret;
    }

    public ASTnode visitPostFixExpression(Parser.MxParser.PostFixExpressionContext ctx) {
        return new PostfixExpressionNode(new Position(ctx), (ExpressionNode) visit(ctx.expression()), ctx.op.getText());
    }

    public ASTnode visitPreExpression(Parser.MxParser.PreExpressionContext ctx) {
        return new PreExpressionNode(new Position(ctx), (ExpressionNode) visit(ctx.expression()), ctx.op.getText());
    }

    public ASTnode visitMemberCall(Parser.MxParser.MemberCallContext ctx) {
        return new MemberCallNode((ExpressionNode) visit(ctx.expression()), ctx.Identifier().getText(),new Position(ctx));
    }

    public ASTnode visitSingleExpression(Parser.MxParser.SingleExpressionContext ctx) {
        String typename = null;
        boolean isClass = false;
        if (ctx.Integer() != null) {
            typename = "int";
        } else if (ctx.StringChar() != null) {
            typename = "string";
            isClass = true;
        } else if (ctx.True() != null || ctx.False() != null) {
            typename = "bool";
        } else if (ctx.Null() != null) {
            typename = "null";
        } else if (ctx.This() != null) {
            typename = "this";
            isClass = true;
        }
        TypeNode type = new TypeNode(new Position(ctx),isClass,typename);
        return new SingleExpressionNode( new Position(ctx),ctx.getText(), type, ctx.Identifier() != null);
    }

    public ASTnode visitBinaryExpression(Parser.MxParser.BinaryExpressionContext ctx) {
        return new BinaryExpressionNode(
                (ExpressionNode) visit(ctx.expression(0)),
                (ExpressionNode) visit(ctx.expression(1)),
                ctx.op.getText(),new Position(ctx));
    }

    public ASTnode visitTrinaryExpression(Parser.MxParser.TrinaryExpressionContext ctx) {
        return new TrinaryExpressionNode(
                new Position(ctx),
                (ExpressionNode) visit(ctx.expression(0)),
                (ExpressionNode) visit(ctx.expression(1)),
                (ExpressionNode) visit(ctx.expression(2)));
    }

    public ASTnode visitFunctionCall(Parser.MxParser.FunctionCallContext ctx) {
        var func = (ExpressionNode) visit(ctx.expression());
        func.function = true;
        func.left = false;
        var ret = new FunctionCallNode(func,new Position(ctx));
        if (ctx.funcCall() != null) {
            for (var i : ctx.funcCall().expression()) {
                ret.args.add((ExpressionNode) visit(i));
            }
        }
        return ret;
    }

    public ASTnode visitAssignExpression(Parser.MxParser.AssignExpressionContext ctx) {
        var ret = new AssignExpressionNode(new Position(ctx), (ExpressionNode) visit(ctx.expression(0)), (ExpressionNode) visit(ctx.expression(1)));
        return ret;
    }

    public ASTnode visitParenExpression(Parser.MxParser.ParenExpressionContext ctx) {
        return visit(ctx.expression());
    }

}
