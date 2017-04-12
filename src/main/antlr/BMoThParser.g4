parser grammar BMoThParser;

options { tokenVocab=BMoThLexer; }

@header {
package de.bmoth.antlr;
}

start
  : parse_unit EOF      # ParseUnit
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
  : IDENTIFIER EQUAL substitution   # Operation
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

predicate
  : '(' predicate ')'                                                       # ParenthesisPredicate
  | operator=(TRUE|FALSE)                                                   # PredicateOperator
  | operator=NOT '(' predicate ')'                                          # PredicateOperator
  | operator=(FOR_ANY|EXITS) quantified_variables_list
      DOT LEFT_PAR predicate RIGHT_PAR                                      # QuantificationPredicate
  | expression operator=(EQUAL|NOT_EQUAL|COLON|ELEMENT_OF
    |LESS_EQUAL|LESS|GREATER_EQUAL|GREATER) expression                      # PredicateOperatorWithExprArgs
  | predicate operator=EQUIVALENCE predicate                                # PredicateOperator //p60
  | predicate operator=(AND|OR) predicate                                   # PredicateOperator //p40
  | predicate operator=IMPLIES predicate                                    # PredicateOperator //p30
  ;

expression
  : Number                                                                  # NumberExpression
  | LEFT_PAR expression RIGHT_PAR                                           # ParenthesesExpression
  | IDENTIFIER                                                              # IdentifierExpression
  | BOOl_CAST '(' predicate ')'                                             # CastPredicateExpression
  | operator=(NATURAL|NATURAL1|INTEGER|BOOL|TRUE|FALSE)                     # ExpressionOperator
  // operators with precedences
  | operator=MINUS expression                                               # ExpressionOperator //P210
  | <assoc=right> expression operator=POWER_OF expression                   # ExpressionOperator //p200
  | expression operator=(MULT|DIVIDE|MOD) expression                        # ExpressionOperator //p190
  | expression operator=(PLUS|MINUS|SET_SUBTRACTION) expression             # ExpressionOperator //p180
  | expression operator=INTERVAL expression                                 # ExpressionOperator //p170
  | expression operator=(UNION) expression                                  # ExpressionOperator //p160
  ;
