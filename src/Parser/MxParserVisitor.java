// Generated from MxParser.g4 by ANTLR 4.13.0

package Parser;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MxParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MxParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MxParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(MxParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction(MxParser.FunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#returnType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnType(MxParser.ReturnTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#parameterlist}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameterlist(MxParser.ParameterlistContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#parameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameter(MxParser.ParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#class}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClass(MxParser.ClassContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#constructor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstructor(MxParser.ConstructorContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#variable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable(MxParser.VariableContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(MxParser.AssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(MxParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#arraytype}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArraytype(MxParser.ArraytypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(MxParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(MxParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#for}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFor(MxParser.ForContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#forfirst}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForfirst(MxParser.ForfirstContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#while}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhile(MxParser.WhileContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#if}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIf(MxParser.IfContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#break}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreak(MxParser.BreakContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#continue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContinue(MxParser.ContinueContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#return}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturn(MxParser.ReturnContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#expressionStat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionStat(MxParser.ExpressionStatContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#funcCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncCall(MxParser.FuncCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxParser#newSize}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewSize(MxParser.NewSizeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code binaryExpression}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinaryExpression(MxParser.BinaryExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code preFixExpression}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPreFixExpression(MxParser.PreFixExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code trinaryExpression}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTrinaryExpression(MxParser.TrinaryExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code singleExpression}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleExpression(MxParser.SingleExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code newExpression}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewExpression(MxParser.NewExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code assignExpression}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignExpression(MxParser.AssignExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code functionCall}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCall(MxParser.FunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by the {@code parenExpression}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenExpression(MxParser.ParenExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code preExpression}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPreExpression(MxParser.PreExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code arrayCall}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayCall(MxParser.ArrayCallContext ctx);
	/**
	 * Visit a parse tree produced by the {@code memberCall}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemberCall(MxParser.MemberCallContext ctx);
	/**
	 * Visit a parse tree produced by the {@code postFixExpression}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPostFixExpression(MxParser.PostFixExpressionContext ctx);
}