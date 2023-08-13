lexer grammar MxLexer;

@header {
package Parser;
}

BlockComment: '/*' .*? '*/' -> channel(HIDDEN);
LineComment: '//' ~[\r\n]* -> channel(HIDDEN);

Inc : '++';
Dec : '--';

Add: '+';
Sub: '-';
Mul: '*';
Div: '/';
Mod: '%';

LAnd: '&&';
LOr: '||';
LNot: '!';

Nequal: '!=';
Equal: '==';
LShift: '<<';
RShift: '>>';
BitAnd: '&';
BitOr: '|';
BitXor: '^';
BitNot: '~';

Gequal: '>=';
Lequal: '<=';
Greater: '>';
Less: '<';

Assign: '=';

WrapLeft: '(';
WrapRight: ')';
BracketLeft: '[';
BracketRight: ']';
BraceLeft: '{';
BraceRight: '}';
Comma: ',';
Semi: ';';
Dot: '.';
QuestionMark: '?';
Colon: ':';

Void: 'void';
Bool: 'bool';
Int: 'int';
String: 'string';
New: 'new';
Class: 'class';
This: 'this';
If: 'if';
Else: 'else';
For: 'for';
While: 'while';
Break: 'break';
Continue: 'continue';
Return: 'return';
Null: 'null';
True: 'true';
False: 'false';

Identifier: [A-Za-z][A-Za-z_0-9]*;
Integer: ([1-9][0-9]* | '0');

fragment EscapeChar: '\\\\' | '\\n' | '\\t' | '\\"';
StringChar: '"' (EscapeChar | .)*? '"';

WhiteSpace : (' ' | '\t') -> channel(HIDDEN);
NewLine : ('\r' | '\n') ->channel(HIDDEN);
