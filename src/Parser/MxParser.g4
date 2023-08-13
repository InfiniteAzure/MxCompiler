parser grammar MxParser;

@header {
package Parser;
}

options {
	tokenVocab = MxLexer;
}

program: (function | class | variable)* EOF;

function: returnType Identifier '(' parameterlist? ')' block;
returnType: arraytype | Void;
parameterlist: parameter (Comma parameter)*;
parameter: arraytype Identifier;

class: Class Identifier '{' (function | variable | constructor)* '}' Semi;
constructor: Identifier '(' ')' block;

variable:arraytype assignment (Comma assignment)* Semi;
assignment: Identifier (Assign expression)?;
type: Int | Bool | String | Identifier;
arraytype: type ('[' ']')*;

block: ('{' statement* '}');

statement:  block
            | variable
            | for
            | while
            | if
            | break
            | continue
            | expressionStat
            | return
            | Semi;

for: For '(' forfirst forsecond = expression? Semi forthird = expression? ')' statement;
forfirst: (expression? Semi) | variable;
while: While '(' expression ')' statement;
if: If '(' expression ')' statement (Else statement)?;
break: Break Semi;
continue: Continue Semi;
return: Return expression? Semi;
expressionStat: expression Semi;

funcCall: expression (',' expression)*;

newSize: '[' expression? ']';

expression:
             New type ('(' ')' | newSize*) #newExpression
             | '(' expression ')' #parenExpression
             | expression '[' expression ']' #arrayCall
             | expression  '(' funcCall? ')' #functionCall
             | expression Dot Identifier #memberCall
             | expression op = (Inc | Dec) #postFixExpression
             | <assoc = right> op = (Inc | Dec) expression #preFixExpression
             | <assoc = right> op = (Add | Sub | LNot | BitNot) expression #preExpression
             | expression op = ( Mul | Div | Mod ) expression #binaryExpression
             | expression op = ( Add | Sub ) expression #binaryExpression
             | expression op = ( LShift | RShift ) expression #binaryExpression
             | expression op = ( Greater | Less | Gequal | Lequal) expression #binaryExpression
             | expression op = ( Equal | Nequal ) expression #binaryExpression
             | expression op = ( BitAnd | BitOr | BitXor | LAnd | LOr) expression #binaryExpression
             | <assoc = right> expression '?' expression ':' expression #trinaryExpression
             | <assoc = right> expression Assign expression #assignExpression
             | (Integer | StringChar | Null | True | False | Identifier | This) #singleExpression
             ;


