# Lambda Calculus Interpreter in Java using ANTLR4
This is an implementation of Lambda Calculus using the following CFG:
```
exprStar : expr SEMI 
  | expr LBRACKET NAME EQUALS expr RBRACKET
  | FREE LPAREN expr RPAREN
  | DEF NAME EQUALS expr
  | SHOW DEFS
;

expr :
    num=NUMBER
  | var=NAME
  | LPAREN e1=expr e2=expr RPAREN
  | LPAREN LAMBDA var=NAME e=expr RPAREN
  | LPAREN oper=OP e1=expr e2=expr RPAREN
  | LPAREN e1=expr RPAREN
;
```
and the following lexical specification:
```
fragment VALID_ID_START : ('a'..'z') | ('A'..'Z');
fragment VALID_ID_CHAR : ('a'..'z') | ('A'..'Z') | ('0'..'9');
fragment L          : ('L'|'l') ;
fragment A          : ('A'|'a') ;
fragment M          : ('M'|'m') ;
fragment B          : ('B'|'b') ;
fragment D          : ('D'|'d') ;
fragment F          : ('F'|'f') ;
fragment R          : ('R'|'r') ;
fragment E          : ('E'|'e') ;
fragment S          : ('S'|'s') ;
fragment H          : ('H'|'h') ;
fragment O          : ('O'|'o') ;
fragment W          : ('W'|'w') ;
NUMBER : ('0'..'9')+ | ('0'..'9')* '.' ('0'..'9')*;
LPAREN : '(';
RPAREN : ')';
LBRACKET : '[';
RBRACKET : ']';
EQUALS : '=';
SEMI : ';';
OP : '+' | '-' | '*' | '/';
LAMBDA : L A M B D A;
FREE : F R E E;
DEF : D E F;
SHOW : S H O W;
DEFS : D E F S;
NAME : VALID_ID_START VALID_ID_CHAR*;
WS : [ \r\n\t]+ -> skip;
```
