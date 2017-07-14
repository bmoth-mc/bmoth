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
  | SETS set_definition (';' set_definition)*                             # SetsClause
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
    : name=IDENTIFIER (LEFT_PAR identifier_list RIGHT_PAR)? DOUBLE_EQUAL definition_body  # OrdinaryDefinition
    | StringLiteral  # DefinitionFile
    ;

definition_body
  : IDENTIFIER (LEFT_PAR expression_list RIGHT_PAR)?           # DefinitionAmbiguousCall
  | expression                                                 # DefinitionExpression
  | predicate                                                  # DefinitionPredicate
  | substitution                                               # DefinitionSubstitution
  ;

single_operation
  :  ( outputParams=identifier_list  OUTPUT_PARAMS)? IDENTIFIER ( LEFT_PAR params=identifier_list RIGHT_PAR )? EQUAL substitution   # Operation
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
  | SELECT preds+=predicate THEN subs+=substitution
      (WHEN preds+=predicate THEN subs+=substitution)*
      (ELSE elseSub=substitution)? END                                     # SelectSubstitution
  | CASE expr=expression OF
      EITHER either=expression_list THEN sub=substitution
      (SUBSTITUTION_OR or_exprs+=expression_list THEN or_subs+=substitution)+
      (ELSE else_sub=substitution)? END END                                 # CaseSubstitution
  | keyword=(PRE|ASSERT) predicate THEN substitution END                    # ConditionSubstitution
  | ANY identifier_list WHERE predicate THEN substitution END               # AnySubstitution
  | identifier_list ':=' expression_list                                    # AssignSubstitution
  | substitution DOUBLE_VERTICAL_BAR substitution                           # ParallelSubstitution
  | identifier_list DOUBLE_COLON expression                                 # BecomesElementOfSubstitution
  | identifier_list (ELEMENT_OF|COLON) LEFT_PAR predicate RIGHT_PAR         # BecomesSuchThatSubstitution
  | IF preds+=predicate THEN subs+=substitution
      (ELSIF preds+=predicate THEN subs+=substitution)*
      (ELSE elseSub=substitution)? END                                      # IfSubstitution
  | WHILE condition=predicate DO substitution INVARIANT invariant=predicate
      VARIANT variant=expression END                                        # WhileSubstitution
  ;

expression_list
  : exprs+=expression (',' exprs+=expression)*
  ;

formula
  : predicate EOF
  | expression EOF
  ;

predicate
  : LEFT_PAR predicate RIGHT_PAR                                                        # ParenthesesPredicate
  | IDENTIFIER                                                                          # PredicateIdentifier
  | IDENTIFIER LEFT_PAR exprs+=expression (',' exprs+=expression)* RIGHT_PAR            # PredicateDefinitionCall
  | operator=(FOR_ANY|EXITS) quantified_variables_list
      DOT LEFT_PAR predicate RIGHT_PAR                                                  # QuantifiedPredicate
  | operator=(TRUE|FALSE)                                                               # PredicateOperator
  | operator=NOT LEFT_PAR predicate RIGHT_PAR                                           # PredicateOperator
  | expression operator=(EQUAL|NOT_EQUAL|COLON|ELEMENT_OF|NOT_BELONGING
      |INCLUSION|STRICT_INCLUSION|NON_INCLUSION|STRICT_NON_INCLUSION
      |LESS_EQUAL|LESS|GREATER_EQUAL|GREATER) expression                                # PredicateOperatorWithExprArgs
  | predicate operator=EQUIVALENCE predicate                                            # PredicateOperator //p60
  | predicate operator=(AND|OR) predicate                                               # PredicateOperator //p40
  | predicate operator=IMPLIES predicate                                                # PredicateOperator //p30
  ;

expression
  : Number                                                                              # NumberExpression
  | LEFT_PAR expression RIGHT_PAR                                                       # ParenthesesExpression
  | BOOL_CAST LEFT_PAR predicate RIGHT_PAR                                              # CastPredicateExpression
  | IDENTIFIER                                                                          # IdentifierExpression
  | StringLiteral                                                                       # StringExpression
  | LEFT_BRACE RIGHT_BRACE                                                              # EmptySetExpression
  | LEFT_BRACE expression_list RIGHT_BRACE                                              # SetEnumerationExpression
  | LEFT_BRACE identifier_list '|' predicate RIGHT_BRACE                                # SetComprehensionExpression
  | LEFT_PAR exprs+=expression COMMA exprs+=expression
      (COMMA exprs+=expression)* RIGHT_PAR                                              # NestedCoupleAsTupleExpression
  | '[' expression_list? ']'                                                            # SequenceEnumerationExpression
  | '<''>'                                                                              # EmptySequenceExpression
  | operator=(NATURAL|NATURAL1|INTEGER|INT|NAT|NAT1
      |MININT|MAXINT|BOOL|TRUE|FALSE)                                                   # ExpressionOperator
  | exprs+=expression LEFT_PAR exprs+=expression
      (',' exprs+=expression)* RIGHT_PAR                                                # FunctionCallExpression
  | operator=(DOM|RAN|CARD|CONC|FIRST|FRONT|ID|ISEQ|ISEQ1
      |LAST|MAX|MIN|POW|REV|SEQ|SEQ1|TAIL
      |GENERALIZED_UNION|GENERALIZED_INTER)
        LEFT_PAR expression RIGHT_PAR                                                   # ExpressionOperator
  | operator=(QUANTIFIED_UNION|QUANTIFIED_INTER|SIGMA|PI)
      quantified_variables_list
        DOT LEFT_PAR predicate VERTICAL_BAR expression RIGHT_PAR                        # QuantifiedExpression

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
  | expression operator=(SET_RELATION|PARTIAL_FUNCTION|TOTAL_FUNCTION
      |TOTAL_INJECTION|PARTIAL_INJECTION|TOTAL_SURJECTION|PARTIAL_SURJECTION
      |TOTAL_BIJECTION|PARTIAL_BIJECTION) expression                         # ExpressionOperator //p125
  ;

ltlStart
  : ltlFormula EOF
  ;

ltlFormula
  : LTL_LEFT_PAR ltlFormula LTL_RIGHT_PAR                             # LTLParentheses
  | keyword=(LTL_TRUE|LTL_FALSE)                                      # LTLKeyword
  | operator=(LTL_GLOBALLY|LTL_FINALLY|LTL_NEXT|LTL_NOT) ltlFormula   # LTLPrefixOperator
  | LTL_B_START predicate B_END                                       # LTLBPredicate
  | ltlFormula operator=LTL_IMPLIES               ltlFormula          # LTLInfixOperator
  | ltlFormula operator=(LTL_UNTIL|LTL_WEAK_UNTIL|LTL_RELEASE)   ltlFormula          # LTLInfixOperator
  | ltlFormula operator=(LTL_AND|LTL_OR)  ltlFormula                  # LTLInfixOperator
  ;
