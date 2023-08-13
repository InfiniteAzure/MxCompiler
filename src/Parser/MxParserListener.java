// Generated from MxParser.g4 by ANTLR 4.13.0

package Parser;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MxParser}.
 */
public interface MxParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MxParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(MxParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(MxParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#function}.
	 * @param ctx the parse tree
	 */
	void enterFunction(MxParser.FunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#function}.
	 * @param ctx the parse tree
	 */
	void exitFunction(MxParser.FunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#returnType}.
	 * @param ctx the parse tree
	 */
	void enterReturnType(MxParser.ReturnTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#returnType}.
	 * @param ctx the parse tree
	 */
	void exitReturnType(MxParser.ReturnTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#parameterlist}.
	 * @param ctx the parse tree
	 */
	void enterParameterlist(MxParser.ParameterlistContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#parameterlist}.
	 * @param ctx the parse tree
	 */
	void exitParameterlist(MxParser.ParameterlistContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#parameter}.
	 * @param ctx the parse tree
	 */
	void enterParameter(MxParser.ParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#parameter}.
	 * @param ctx the parse tree
	 */
	void exitParameter(MxParser.ParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#class}.
	 * @param ctx the parse tree
	 */
	void enterClass(MxParser.ClassContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#class}.
	 * @param ctx the parse tree
	 */
	void exitClass(MxParser.ClassContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#constructor}.
	 * @param ctx the parse tree
	 */
	void enterConstructor(MxParser.ConstructorContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#constructor}.
	 * @param ctx the parse tree
	 */
	void exitConstructor(MxParser.ConstructorContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#variable}.
	 * @param ctx the parse tree
	 */
	void enterVariable(MxParser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#variable}.
	 * @param ctx the parse tree
	 */
	void exitVariable(MxParser.VariableContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(MxParser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(MxParser.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(MxParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(MxParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#arraytype}.
	 * @param ctx the parse tree
	 */
	void enterArraytype(MxParser.ArraytypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#arraytype}.
	 * @param ctx the parse tree
	 */
	void exitArraytype(MxParser.ArraytypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(MxParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(MxParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(MxParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(MxParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#for}.
	 * @param ctx the parse tree
	 */
	void enterFor(MxParser.ForContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#for}.
	 * @param ctx the parse tree
	 */
	void exitFor(MxParser.ForContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#forfirst}.
	 * @param ctx the parse tree
	 */
	void enterForfirst(MxParser.ForfirstContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#forfirst}.
	 * @param ctx the parse tree
	 */
	void exitForfirst(MxParser.ForfirstContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#while}.
	 * @param ctx the parse tree
	 */
	void enterWhile(MxParser.WhileContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#while}.
	 * @param ctx the parse tree
	 */
	void exitWhile(MxParser.WhileContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#if}.
	 * @param ctx the parse tree
	 */
	void enterIf(MxParser.IfContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#if}.
	 * @param ctx the parse tree
	 */
	void exitIf(MxParser.IfContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#break}.
	 * @param ctx the parse tree
	 */
	void enterBreak(MxParser.BreakContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#break}.
	 * @param ctx the parse tree
	 */
	void exitBreak(MxParser.BreakContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#continue}.
	 * @param ctx the parse tree
	 */
	void enterContinue(MxParser.ContinueContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#continue}.
	 * @param ctx the parse tree
	 */
	void exitContinue(MxParser.ContinueContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#return}.
	 * @param ctx the parse tree
	 */
	void enterReturn(MxParser.ReturnContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#return}.
	 * @param ctx the parse tree
	 */
	void exitReturn(MxParser.ReturnContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#expressionStat}.
	 * @param ctx the parse tree
	 */
	void enterExpressionStat(MxParser.ExpressionStatContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#expressionStat}.
	 * @param ctx the parse tree
	 */
	void exitExpressionStat(MxParser.ExpressionStatContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#funcCall}.
	 * @param ctx the parse tree
	 */
	void enterFuncCall(MxParser.FuncCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#funcCall}.
	 * @param ctx the parse tree
	 */
	void exitFuncCall(MxParser.FuncCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#newSize}.
	 * @param ctx the parse tree
	 */
	void enterNewSize(MxParser.NewSizeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#newSize}.
	 * @param ctx the parse tree
	 */
	void exitNewSize(MxParser.NewSizeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code binaryExpression}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBinaryExpression(MxParser.BinaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binaryExpression}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBinaryExpression(MxParser.BinaryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code preFixExpression}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterPreFixExpression(MxParser.PreFixExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code preFixExpression}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitPreFixExpression(MxParser.PreFixExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code trinaryExpression}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterTrinaryExpression(MxParser.TrinaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code trinaryExpression}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitTrinaryExpression(MxParser.TrinaryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code singleExpression}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterSingleExpression(MxParser.SingleExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code singleExpression}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitSingleExpression(MxParser.SingleExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code newExpression}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterNewExpression(MxParser.NewExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code newExpression}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitNewExpression(MxParser.NewExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code assignExpression}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAssignExpression(MxParser.AssignExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code assignExpression}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAssignExpression(MxParser.AssignExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code functionCall}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCall(MxParser.FunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code functionCall}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCall(MxParser.FunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by the {@code parenExpression}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterParenExpression(MxParser.ParenExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code parenExpression}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitParenExpression(MxParser.ParenExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code preExpression}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterPreExpression(MxParser.PreExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code preExpression}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitPreExpression(MxParser.PreExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code arrayCall}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterArrayCall(MxParser.ArrayCallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code arrayCall}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitArrayCall(MxParser.ArrayCallContext ctx);
	/**
	 * Enter a parse tree produced by the {@code memberCall}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterMemberCall(MxParser.MemberCallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code memberCall}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitMemberCall(MxParser.MemberCallContext ctx);
	/**
	 * Enter a parse tree produced by the {@code postFixExpression}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterPostFixExpression(MxParser.PostFixExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code postFixExpression}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitPostFixExpression(MxParser.PostFixExpressionContext ctx);
}