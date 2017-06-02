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
  | SETS set_definition (SEMICOLON set_definition)*                       # SetsClause
  | definition_clause                                                     # DefinitionClauseIndirection // used to reuse definition_clause for definition files
  ;

set_definition
  : IDENTIFIER                                                            # DeferredSet
  | IDENTIFIER EQUAL LEFT_BRACE identifier_list RIGHT_BRACE               # EnumeratedSet
  ;


definition_clause
  : DEFINITIONS defs+=single_definition (SEMICOLON defs+=single_definition)* SEMICOLON?   # DefinitionClause
  ;

single_definition
    : name=IDENTIFIER ('(' parameters+=IDENTIFIER (',' parameters+=IDENTIFIER)* ')')? DOUBLE_EQUAL definition_body # OrdinaryDefinition
    | StringLiteral  # DefinitionFile
    ;

definition_body
  : IDENTIFIER ('(' expression_list ')')?           # DefinitionAmbiguousCall
  | expression                                      # DefinitionExpression
  | predicate                                       # DefinitionPredicate
  | substitution                                    # DefinitionSubstitution
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
  | SELECT predicate THEN substitution END                                  # SelectSubstitution // WHEN is missing
  | PRE predicate THEN substitution END                                     # PreSubstitution
  | ANY identifier_list WHERE predicate THEN substitution END               # AnySubstitution
  | identifier_list ':=' expression_list                                    # AssignSubstitution
  | substitution DOUBLE_VERTICAL_BAR substitution                           # ParallelSubstitution
  | identifier_list DOUBLE_COLON expression                                 # BecomesElementOfSubstitution
  | identifier_list (ELEMENT_OF|COLON) LEFT_PAR predicate RIGHT_PAR         # BecomesSuchThatSubstitution
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
  | IDENTIFIER '(' exprs+=expression (',' exprs+=expression)* ')'           # PredicateDefinitionCall
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
  | BOOL_CAST '(' predicate ')'                                             # CastPredicateExpression
  | IDENTIFIER                                                              # IdentifierExpression
  | '{' '}'                                                                 # EmptySetExpression
  | '{' expression_list '}'                                                 # SetEnumerationExpression
  | '{' identifier_list '|' predicate '}'                                   # SetComprehensionExpression
  | '(' exprs+=expression COMMA exprs+=expression
      (COMMA exprs+=expression)* ')'                                        # NestedCoupleAsTupleExpression
  | '[' expression_list? ']'                                                # SequenceEnumerationExpression
  | operator=(NATURAL|NATURAL1|INTEGER|INT|NAT
      |MININT|MAXINT|BOOL|TRUE|FALSE)                                       # ExpressionOperator
  | exprs+=expression '(' exprs+=expression
      (',' exprs+=expression)* ')'                                          # FunctionCallExpression
  | operator=(DOM|RAN|CARD|CONC|FIRST|FRONT|ID|ISEQ|ISEQ1
      |LAST|MAX|MIN|POW|REV|SEQ|SEQ1|TAIL
      |GENERALIZED_UNION|GENERALIZED_INTER)
        '(' expression ')'                                                  # ExpressionOperator
  | operator=(QUANTIFIED_UNION|QUANTIFIED_INTER|SIGMA|PI)
      quantified_variables_list
        DOT LEFT_PAR predicate VERTICAL_BAR expression RIGHT_PAR            # QuantifiedExpression

  // operators with precedences
  | expression operator=TILDE                                               # ExpressionOperator //p230
  | operator=MINUS expression                                               # ExpressionOperator //P210
  | <assoc=right> expression operator=POWER_OF expression                   # ExpressionOperator //p200
  | expression operator=(MULT|DIVIDE|MOD) expression                        # ExpressionOperator //p190
  | expression operator=(PLUS|MINUS|SET_SUBTRACTION) expression             # ExpressionOperator //p180
  | expression operator=INTERVAL expression                                 # ExpressionOperator //p170
  | expression operator=(OVERWRITE_RELATION|DIRECT_PRODUCT|CONCAT
      |DOMAIN_RESTRICTION|DOMAIN_SUBTRACTION|RANGE_RESTRICTION
      |RANGE_SUBTRACTION|INSERT_FRONT|INSERT_TAIL|UNION|INTERSECTION
      |RESTRICT_FRONT|RESTRICT_TAIL|MAPLET) expression                      # ExpressionOperator //p160
  ;
