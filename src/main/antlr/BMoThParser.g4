parser grammar BMoThParser;

options { tokenVocab=BMoThLexer; }

@header {
package de.bmoth.antlr;
}

start
  : parse_unit EOF      # ParseUnit
  ;

parse_unit
  : MACHINE IDENTIFIER (clauses+=machine_clause)* END            # ParseUnitMachine
  ;

machine_clause
  : name=(PROPERTIES|INVARIANT) pred=predicate                            # PredicateClause
  | name=(CONSTANTS|VARIABLES) identifier_list                            # DeclarationClause
  | INITIALISATION substitution                                           # InitialisationClause
  | OPERATIONS  ops+=single_operation (SEMICOLON ops+=single_operation)*  # OperationsClause
  ;

single_operation
  : IDENTIFIER EQUAL substitution   # Operation
  ;

quantified_variables_list
  : identifier_list
  | LEFT_PAR identifier_list RIGHT_PAR
  ;

identifier_list
  : idents+=IDENTIFIER (',' idents+=IDENTIFIER)*
  ;

formula
  : expression            #FormulaExpression
  | predicate             #FormulaPredicate
  | substitution          #FormulaSubstitution
  ;

substitution
  : BEGIN substitution END                                                              # BlockSubstitution
  | SKIP_SUB                                                                            # SkipSubstitution
  | SELECT pred=predicate THEN sub=substitution END                                     # SelectSubstitution
  | ANY identifier_list WHERE predicate THEN substitution END                           # AnySubstitution
  | identifier_list ':=' expression_list                                                # AssignSubstitution
  | substitution DOUBLE_VERTICAL_BAR substitution                                       # ParallelSubstitution
  ;

expression_list
  : exprs+=expression (',' exprs+=expression)*
  ;

predicate
  : '(' predicate ')'                                                                     # ParenthesisPredicate
  | keyword=(TRUE|FALSE)                                                                  # KeywordPredicate
  | NOT '(' predicate ')'                                                                 # NotPredicate
  | operator=(FOR_ANY|EXITS) quantified_variables_list
      DOT LEFT_PAR predicate RIGHT_PAR                                                    # QuantificationPredicate
  | left=expression operator=(EQUAL|ELEMENT_OF|COLON|INCLUSION|STRICT_INCLUSION
    |NON_INCLUSION|STRICT_NON_INCLUSION|NOT_EQUAL|NOT_BELONGING|LESS_EQUAL
    |LESS|GREATER_EQUAL|GREATER) right=expression                                         # BinExpressionOperatorPredicate
  | left=predicate operator=EQUIVALENCE right=predicate                                   # BinPredicateOperatorPredicate //p60
  | left=predicate operator=(AND|OR) right=predicate                                      # BinPredicateOperatorPredicate //p40
  | left=predicate operator=IMPLIES right=predicate                                       # BinPredicateOperatorPredicate //p30
  ;

expression
  : Number                                                                  # NumberExpression
  | value=(TRUE|FALSE)                                                      # BooleanValueExpression
  | StringLiteral                                                           # StringExpression
  | LEFT_PAR expression RIGHT_PAR                                           # ParenthesisExpression
  | IDENTIFIER                                                              # IdentifierExpression
  | BOOl_CAST '(' predicate ')'                                             # CastPredicateExpression
  | keyword=(NATURAL|NATURAL1|INTEGER|BOOL)                                 # KeywordExpression
  // operators with precedences
  | operator=MINUS expr=expression                                                      # UnaryOperatorExpression  //P210
  | <assoc=right> left=expression operator=POWER_OF right=expression                    # BinOperatorExpression //p200
  | left=expression operator=(MULT|DIVIDE|MOD) right=expression                         # BinOperatorExpression //p190
  | left=expression operator=(PLUS|MINUS|SET_SUBTRACTION) right=expression              # BinOperatorExpression //p180
  | left=expression operator=INTERVAL right=expression                                  # BinOperatorExpression //p170
  | left=expression operator=(UNION|MAPLET) right=expression                            # BinOperatorExpression //p160
  ;
