lexer grammar BMoThLexer;

@header {
package de.bmoth.antlr;
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

BEGIN: 'BEGIN';
SKIP_SUB: 'skip';


SELECT: 'SELECT';
THEN: 'THEN';
ANY: 'ANY';
WHERE: 'WHERE';


DOT: '.';

FOR_ANY: '!' | '\u2200';
EXITS: '#' | '\u2203';
LAMBDA: '%' | '\u03bb';

ASSIGN: ':=';

EQUIVALENCE: '<=>' | '\u21d4';
IMPLIES: EQUAL GREATER | '\u21d2';
LEFT_BRACE: '{';
RIGHT_BRACE: '}';
LEFT_PAR: '(';
RIGHT_PAR: ')';

MINUS: '-' | '\u2212';
SET_SUBTRACTION: '\\';
PLUS: '+' | '\u002b';
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
BOOl_CAST: 'bool';

// predicate infix operators
AND: '&';
OR: 'or';

// expression infix operators P160
OVERWRITE_RELATION: '<+';
DIRECT_PRODUCT: '><'| '⊗' ; //'\u2297'; ?
CONCAT: '^';
DOMAIN_RESTRICTION: '<|';
DOMAIN_SUBSTRACTION: '<<|';
RANGE_RESTRICTION:  '|>';
RANGE_SUBSTRATION:  '|>>';
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
EQUAL: '=' | '\u003d';
NOT_EQUAL: '/=' | '\u2260';
COLON: ':' ;
ELEMENT_OF: '\u2208';
INCLUSION: '<:' | '\u2286';
STRICT_INCLUSION: '<<:' | '\u2282';
NON_INCLUSION: '/<:' | '\u2288';
STRICT_NON_INCLUSION: '/<<:' | '\u2284';
NOT_BELONGING: '/:' | '\u2209';
LESS: '<' | '\u003c';
LESS_EQUAL: LESS EQUAL | '\u2264';
GREATER: '>' | '\u003e';
GREATER_EQUAL: GREATER EQUAL | '\u2265';

TRUE: 'TRUE';
FALSE: 'FALSE';


// expression prefix operators with one parameter
DOM: 'dom';
RAN: 'ran';

//keyword operators
NATURAL: 'NATURAL' | '\u2115';
NATURAL1: 'NATURAL1' | '\u2115' '\u0031' | '\u2115' '\u2081';
INTEGER: 'INTEGER' | '\u2124';
BOOL: 'BOOL';


//special
SIGMA: 'SIGMA' | '∑'; //0x2211;
PI: 'PI' | '∏'; //0x220f;
QUANTIFIED_UNION: 'UNION';
QUANTIFIED_INTER: 'INTER';

GENERALIZED_UNION: 'union' | '⋃';//'\u22c3';
GENERALIZED_INTER: 'inter'; //TODO unicode missing

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
