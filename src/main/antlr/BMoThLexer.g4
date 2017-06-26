lexer grammar BMoThLexer;

@header {
package de.bmoth.antlr;
}

@members {
int curlyBracketsCount = 0;
}

fragment DIGIT: ('0'..'9');
fragment HEX_DIGIT: DIGIT | [a-fA-F];
fragment LETTER: [a-zA-Z];

Number
  : ('1'..'9') DIGIT+
  | DIGIT
  ;


MACHINE: 'MACHINE' ;
END: 'END' ;

// clauses
CONSTANTS: 'CONSTANTS';
PROPERTIES: 'PROPERTIES';
VARIABLES: 'VARIABLES';
INVARIANT: 'INVARIANT';
OPERATIONS: 'OPERATIONS';
INITIALISATION: 'INITIALISATION';
SETS: 'SETS';
DEFINITIONS: 'DEFINITIONS';


BEGIN: 'BEGIN';
SKIP_SUB: 'skip';
IF: 'IF';
ELSIF: 'ELSIF';
THEN: 'THEN';
ELSE: 'ELSE';
PRE: 'PRE';
ASSERT: 'ASSERT';
CHOICE: 'CHOICE';
SUBSTITUTION_OR: 'OR';
SELECT: 'SELECT';
WHEN: 'WHEN';
CASE: 'CASE';
OF: 'OF';
EITHER: 'EITHER';
ANY: 'ANY';
WHERE: 'WHERE';
LET: 'LET';
BE: 'BE';
IN: 'IN';
VAR: 'VAR';
WHILE: 'WHILE';
VARIANT: 'VARIANT';
DO: 'DO';


DOT: '.';

FOR_ANY: '!' | '\u2200';
EXITS: '#' | '\u2203';
LAMBDA: '%' | '\u03bb';

ASSIGN: ':=';
DOUBLE_COLON: '::' | ':' '\u2208' ;  /* becomes_element_of */

EQUIVALENCE: '<=>' | '\u21d4';
IMPLIES: EQUAL GREATER | '\u21d2';
LEFT_BRACE: '{' {curlyBracketsCount++;} ;
RIGHT_BRACE: '}' {curlyBracketsCount--;} {curlyBracketsCount>0}?;
LEFT_PAR: '(';
RIGHT_PAR: ')';
LEFT_BRACKET: '[';
RIGHT_BRACKET: ']';

MINUS: '-' | '\u2212';
SET_SUBTRACTION: '\\';
PLUS: '+';
SINGLE_QUOTE: '\'';
TILDE: '~' | '∼' | '⁻' '¹';// 0x207b 0xb9;'\u223c';

SEMICOLON: ';';
VERTICAL_BAR: '|';
DOUBLE_VERTICAL_BAR: '||' | '\u2225';
COMMA: ',';
REC: 'rec';
STRUCT: 'struct';


//predicate prefix operators
NOT: 'not' | '\u00ac';
BOOL_CAST: 'bool';

// predicate infix operators
AND: '&';
OR: 'or';

// expression infix operators P160
OVERWRITE_RELATION: '<+';
DIRECT_PRODUCT: '><'| '⊗' ; //'\u2297'; ?
CONCAT: '^';
DOMAIN_RESTRICTION: '<|';
DOMAIN_SUBTRACTION: '<<|';
RANGE_RESTRICTION:  '|>';
RANGE_SUBTRACTION:  '|>>';
INSERT_FRONT: '->'| '\u21fe';
INSERT_TAIL:  '<-' | '\u21fd';
INTERSECTION: '/\\' | '\u2229';
RESTRICT_FRONT: '/|\\' | '\u2191';
RESTRICT_TAIL: '\\|/' | '\u2193';
MAPLET: '|->' | '\u21a6';
UNION: '\\/' | '\u222a';

UNDERSCORE: '_';

//expression infix operators

MULT: '*';
DIVIDE: '/';

MOD: 'mod';
POWER_OF: '**';
INTERVAL: '..' | '\u2025';

// predicate infix opertors
EQUAL: '=';
DOUBLE_EQUAL: EQUAL EQUAL;
NOT_EQUAL: '/=' | '\u2260';
COLON: ':' ;
ELEMENT_OF: '\u2208';
INCLUSION: '<:' | '\u2286';
STRICT_INCLUSION: '<<:' | '\u2282';
NON_INCLUSION: '/<:' | '\u2288';
STRICT_NON_INCLUSION: '/<<:' | '\u2284';
NOT_BELONGING: '/:' | '\u2209';
LESS: '<';
LESS_EQUAL: LESS EQUAL | '\u2264';
GREATER: '>' ;
GREATER_EQUAL: GREATER EQUAL | '\u2265';

TRUE: 'TRUE';
FALSE: 'FALSE';


// expression prefix operators with one parameter
DOM: 'dom';
RAN: 'ran';
CARD: 'card';
CONC: 'conc';
FIRST: 'first';
FRONT: 'front';
ID: 'id';
ISEQ: 'iseq';
ISEQ1: 'iseq1' ; // add 'iseq'0x8321 ?
LAST: 'last';
MAX: 'max';
MIN: 'min';
POW: 'POW';
REV: 'rev';
SEQ: 'seq';
SEQ1: 'seq1'; // add | 'seq'0x8321 ?
TAIL: 'tail';
GENERALIZED_UNION: 'union' | '⋃';//'\u22c3';
GENERALIZED_INTER: 'inter'; //TODO unicode missing

//keyword operators
NATURAL: 'NATURAL' | '\u2115';
NATURAL1: 'NATURAL1' | '\u2115' '\u0031' | '\u2115' '\u2081';
INTEGER: 'INTEGER' | '\u2124';
INT: 'INT' ;
NAT: 'NAT' ;
NAT1:  'NAT1';
BOOL: 'BOOL';

MININT: 'MININT';
MAXINT: 'MAXINT';


//special
SIGMA: 'SIGMA' | '∑'; //0x2211;
PI: 'PI' | '∏'; //0x220f;
QUANTIFIED_UNION: 'UNION';
QUANTIFIED_INTER: 'INTER';


fragment TSQ: '\'\'\'';
fragment SQ: '\'';

StringLiteral
    :   '"' SignleStringCharacters? '"'
    | TSQ MultiLineStringCharacters? TSQ
    ;
fragment
SignleStringCharacters
    :   SingleLineStringCharacter+
    ;

fragment
SingleLineStringCharacter
    :   ~["\n]
    |    '\\' '"'
    ;

fragment
MultiLineStringCharacters
    :  MultiLineStringCharacter+
    | MultiLineStringCharacter+ SQ SQ
    | MultiLineStringCharacter+ SQ
    ;

fragment
MultiLineStringCharacter
    :  ~[']
    | SQ ~[']
    | SQ SQ ~[']
    | '\\' SQ
    ;

IDENTIFIER
  : LETTER (LETTER | DIGIT | '_')*
  ;

COMMENT
  :   '/*' (~[@] .*?)? '*/' -> skip
  ;

LINE_COMMENT
    :   '//' ~[\r\n]* -> skip
    ;

WS: [ \t\r\n]+ -> skip;

B_END: '}' {curlyBracketsCount=0;} -> mode(LTL_MODE); // reset brackets counter

mode LTL_MODE;

LTL_TRUE: 'true';
LTL_FALSE: 'false';
LTL_IMPLIES: '=>';
LTL_AND: '&';
LTL_OR: 'or';
LTL_NOT: 'not';
LTL_LEFT_PAR: '(';
LTL_RIGHT_PAR: ')';
LTL_GLOBALLY: 'G';
LTL_FINALLY: 'F';
LTL_UNTIL: 'U';
LTL_WEAK_UNTIL: 'W';
LTL_RELEASE: 'R';
LTL_NEXT: 'X';
LTL_B_START: '{' {curlyBracketsCount=0;} -> mode(DEFAULT_MODE) ; // reset brackets counter
LTL_WS: [ \t\r\n]+ -> skip;
