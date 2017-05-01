parser grammar BMoThParser;

options { tokenVocab=BMoThLexer; }

@header {
package de.bmoth.antlr;
}

start
  : parse_unit EOF                                                        # ParseUnit
  ;

parse_unit
  : MACHINE IDENTIFIER (clauses+=machine_clause)* END                     # MachineParseUnit
  ;

machine_clause
  : clauseName=(PROPERTIES|INVARIANT) pred=predicate                      # PredicateClause
  | clauseName=(CONSTANTS|VARIABLES) identifier_list                      # DeclarationClause
  | INITIALISATION substitution                                           # InitialisationClause
  | OPERATIONS  ops+=single_operation (SEMICOLON ops+=single_operation)*  # OperationsClause
  ;

single_operation
  : IDENTIFIER EQUAL substitution                                         # Operation
  ;

quantified_variables_list
  : identifier_list
  | LEFT_PAR identifier_list RIGHT_PAR
  ;

identifier_list
  : identifiers+=IDENTIFIER (',' identifiers+=IDENTIFIER)*
  ;

substitution
  : BEGIN substitution END                                                  # BlockSubstitution
  | SKIP_SUB                                                                # SkipSubstitution
  | SELECT condition=predicate THEN sub=substitution END                    # SelectSubstitution
  | ANY identifier_list WHERE predicate THEN substitution END               # AnySubstitution
  | identifier_list ':=' expression_list                                    # AssignSubstitution
  | substitution DOUBLE_VERTICAL_BAR substitution                           # ParallelSubstitution
  ;

expression_list
  : exprs+=expression (',' exprs+=expression)*
  ;

formula
  : predicate
  | expression
  ;

predicate
  : '(' predicate ')'                                                       # ParenthesesPredicate
  | IDENTIFIER                                                              # PredicateIdentifier
  | operator=(FOR_ANY|EXITS) quantified_variables_list
      DOT LEFT_PAR predicate RIGHT_PAR                                      # QuantifiedPredicate
  | operator=(TRUE|FALSE)                                                   # PredicateOperator
  | operator=NOT '(' predicate ')'                                          # PredicateOperator
  | expression operator=(EQUAL|NOT_EQUAL|COLON|ELEMENT_OF|NOT_BELONGING
      |INCLUSION|STRICT_INCLUSION|NON_INCLUSION|STRICT_NON_INCLUSION
      |LESS_EQUAL|LESS|GREATER_EQUAL|GREATER) expression                    # PredicateOperatorWithExprArgs
  | predicate operator=EQUIVALENCE predicate                                # PredicateOperator //p60
  | predicate operator=(AND|OR) predicate                                   # PredicateOperator //p40
  | predicate operator=IMPLIES predicate                                    # PredicateOperator //p30
  ;

expression
  : Number                                                                  # NumberExpression
  | LEFT_PAR expression RIGHT_PAR                                           # ParenthesesExpression
  | IDENTIFIER                                                              # IdentifierExpression
  | '{' expression_list '}'                                                 # SetEnumerationExpression
  | '{' identifier_list '|' predicate '}'                                   # SetComprehensionExpression
  | '(' exprs+=expression COMMA exprs+=expression (COMMA exprs+=expression)* ')'  # NestedCoupleAsTupleExpression
  | BOOl_CAST '(' predicate ')'                                             # CastPredicateExpression
  | operator=(NATURAL|NATURAL1|INTEGER|BOOL|TRUE|FALSE)                     # ExpressionOperator
  | operator=(DOM|RAN) '(' expression ')'                                   # ExpressionOperator
  // operators with precedences
  | operator=MINUS expression                                               # ExpressionOperator //P210
  | <assoc=right> expression operator=POWER_OF expression                   # ExpressionOperator //p200
  | expression operator=(MULT|DIVIDE|MOD) expression                        # ExpressionOperator //p190
  | expression operator=(PLUS|MINUS|SET_SUBTRACTION) expression             # ExpressionOperator //p180
  | expression operator=INTERVAL expression                                 # ExpressionOperator //p170
  | expression operator=(OVERWRITE_RELATION|DIRECT_PRODUCT|CONCAT
      |DOMAIN_RESTRICTION|DOMAIN_SUBSTRACTION|RANGE_RESTRICTION
      |RANGE_SUBSTRATION|INSERT_FRONT|INSERT_TAIL|UNION|INTERSECTION
      |RESTRICT_FRONT|RESTRICT_TAIL|MAPLET) expression                      # ExpressionOperator //p160
  ;
