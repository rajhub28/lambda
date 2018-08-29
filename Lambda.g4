grammar Lambda;

exprStart returns [LambdaRootNode value] : 
    e=expr SEMI 
  { 
    LambdaRootNode n = new LambdaRootNode();
    n.setNodeType("expr");
    n.setExprValue($e.value);
    $value = n;
  }
  | e1=expr LBRACKET var=NAME EQUALS e2=expr RBRACKET
  {
    LambdaRootNode n = new LambdaRootNode();
    n.setNodeType("subst");
    n.setExprValue($e1.value);
    n.setVariable($var.text);
    n.setVariableValue($e2.value);
    $value = n;
  }
  | FREE LPAREN e=expr RPAREN
  {
    LambdaRootNode n = new LambdaRootNode();
    n.setNodeType("free");
    n.setExprValue($e.value);
    $value = n;
  }
  | DEF var=NAME EQUALS e=expr
  {
    LambdaRootNode n = new LambdaRootNode();
    n.setNodeType("def");
    n.setVariable($var.text.toUpperCase());
    n.setExprValue($e.value);
    $value = n;
  }
  | SHOW DEFS
  {
    LambdaRootNode n = new LambdaRootNode();
    n.setNodeType("showdefs");
    $value = n;
  }
;

expr returns [LambdaNode value]: 
    num=NUMBER
  {
    LambdaNode n = new LambdaNode();
    n.setNodeType("num");
    n.setNumValue(Double.parseDouble($num.text));
    $value = n;
  }
  | var=NAME
  {
    LambdaNode n = new LambdaNode();
    n.setNodeType("var");
    n.setVariable($var.text.toUpperCase());
    $value = n;
  }
  | LPAREN e1=expr e2=expr RPAREN
  {
    LambdaNode n = new LambdaNode();
    n.setNodeType("app");
    n.setChild1($e1.value);
    n.setChild2($e2.value);
    $value = n;
  }
  | LPAREN LAMBDA var=NAME e=expr RPAREN
  {
    LambdaNode n = new LambdaNode();
    n.setNodeType("lambda");
    n.setVariable($var.text.toUpperCase());
    n.setChild1($e.value);
    $value = n;
  }
  | LPAREN oper=OP e1=expr e2=expr RPAREN
  {
    LambdaNode n = new LambdaNode();
    n.setNodeType("op");
    n.setOpValue($oper.text);
    n.setChild1($e1.value);
    n.setChild2($e2.value);
    $value = n;
  }
;

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
//fragment E          : ('E'|'e') ;
//fragment F          : ('F'|'f') ;
//DEF : D E F;
//White Space

//lambda :
//    DEF NAME EQUALS expr
//  | expr;
//
