package de.bmoth.parser.ast;

import de.bmoth.parser.ast.nodes.*;
import de.bmoth.parser.ast.types.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TypeChecker implements AbstractVisitor<Type, Type> {

    Set<ExpressionOperatorNode> minusNodes = new HashSet<>();
    Set<ExpressionOperatorNode> multOrCartNodes = new HashSet<>();
    Set<TypedNode> typedNodes = new HashSet<>();

    public static void typecheckMachineNode(MachineNode machineNode) {
        new TypeChecker(machineNode);
    }

    public static void typecheckFormulaNode(FormulaNode formulaNode) {
        new TypeChecker(formulaNode);
    }

    private TypeChecker(MachineNode machineNode) {
        for (EnumeratedSet eSet : machineNode.getEnumaratedSets()) {
            DeclarationNode setDeclaration = eSet.getSetDeclaration();
            UserDefinedElementType userDefinedElementType = new UserDefinedElementType(setDeclaration.getName(),
                    eSet.getElementsAsStrings());
            setDeclaration.setType(new SetType(userDefinedElementType));
            for (DeclarationNode element : eSet.getElements()) {
                element.setType(userDefinedElementType);
            }
        }

        for (DeclarationNode deferredSet : machineNode.getDeferredSets()) {
            UserDefinedElementType userDefinedElementType = new UserDefinedElementType(deferredSet.getName(), null);
            deferredSet.setType(new SetType(userDefinedElementType));
        }

        // set all constants to untyped
        machineNode.getConstants().forEach(con -> con.setType(new UntypedType()));

        // visit the properties clause
        if (machineNode.getProperties() != null) {
            visitPredicateNode(machineNode.getProperties(), BoolType.getInstance());
        }

        // check that all constants have a type, otherwise throw an exception
        machineNode.getConstants().stream().filter(DeclarationNode::isUntyped).forEach(con -> {
            throw new TypeErrorException(
                    "Can not infer the type of constant " + con.getName() + ". Type variable: " + con.getType());
        });

        // set all variables to untyped
        machineNode.getVariables().forEach(var -> var.setType(new UntypedType()));

        // visit the invariant clause
        if (machineNode.getInvariant() != null) {
            visitPredicateNode(machineNode.getInvariant(), BoolType.getInstance());
        }

        // check that all variables have type, otherwise throw an exception
        machineNode.getVariables().stream().filter(DeclarationNode::isUntyped).forEach(var -> {
            throw new TypeErrorException(
                    "Can not infer the type of variable " + var.getName() + ". Type variable: " + var.getType());
        });

        // visit the initialisation clause
        if (machineNode.getInitialisation() != null) {
            visitSubstitutionNode(machineNode.getInitialisation(), null);
        }

        // visit all operations
        machineNode.getOperations().forEach(op -> visitSubstitutionNode(op.getSubstitution(), null));

        performPostActions();

    }

    private TypeChecker(FormulaNode formulaNode) {
        for (DeclarationNode node : formulaNode.getImplicitDeclarations()) {
            node.setType(new UntypedType());
        }
        Node formula = formulaNode.getFormula();
        if (formula instanceof PredicateNode) {
            visitPredicateNode((PredicateNode) formula, BoolType.getInstance());
        } else {
            // expression formula
            Type type = visitExprNode((ExprNode) formula, new UntypedType());
            if (type.isUntyped()) {
                throw new TypeErrorException("Can not infer type of formula: " + type);
            }
        }

        // check that all implicitly declared variables have a type, otherwise
        // throw an exception
        for (DeclarationNode node : formulaNode.getImplicitDeclarations()) {
            if (node.getType().isUntyped()) {
                throw new TypeErrorException("Can not infer the type of local variable '" + node.getName()
                        + "' Current type: " + node.getType());
            }
        }
        performPostActions();
    }

    private void performPostActions() {
        // Check that all local variables have type.
        for (TypedNode node : typedNodes) {
            if (node.getType().isUntyped()) {
                if (node instanceof DeclarationNode) {
                    DeclarationNode var = (DeclarationNode) node;
                    throw new TypeErrorException("Can not infer the type of local variable " + var.getName());
                } else if (node instanceof ExpressionOperatorNode) {
                    ExpressionOperatorNode exprNode = (ExpressionOperatorNode) node;
                    throw new TypeErrorException(
                            "Can not infer the complete type of operator " + exprNode.getOperator());
                }
            }
        }

        // post actions
        for (ExpressionOperatorNode minusNode : minusNodes) {
            Type type = minusNode.getType();
            if (type instanceof SetType) {
                minusNode.setOperator(ExpressionOperatorNode.ExpressionOperator.SET_SUBTRACTION);
            }
        }

        // post actions
        for (ExpressionOperatorNode node : multOrCartNodes) {
            Type type = node.getType();
            if (type instanceof SetType) {
                node.setOperator(ExpressionOperatorNode.ExpressionOperator.CARTESIAN_PRODUCT);
            }
        }
    }

    @Override
    public Type visitPredicateOperatorNode(PredicateOperatorNode node, Type expected) {
        unify(expected, BoolType.getInstance(), node);
        List<PredicateNode> predicateArguments = node.getPredicateArguments();
        for (PredicateNode predicateNode : predicateArguments) {
            visitPredicateNode(predicateNode, BoolType.getInstance());
        }
        return BoolType.getInstance();
    }

    @Override
    public Type visitPredicateOperatorWithExprArgs(PredicateOperatorWithExprArgsNode node, Type expected) {
        unify(expected, BoolType.getInstance(), node);
        final List<ExprNode> expressionNodes = node.getExpressionNodes();
        switch (node.getOperator()) {
        case EQUAL:
        case NOT_EQUAL:
            visitExprNode(expressionNodes.get(1), visitExprNode(expressionNodes.get(0), new UntypedType()));
            break;
        case NOT_BELONGING:
        case ELEMENT_OF:
            Type setType = visitExprNode(expressionNodes.get(0), new UntypedType());
            visitExprNode(expressionNodes.get(1), new SetType(setType));
            break;
        case LESS_EQUAL:
        case LESS:
        case GREATER_EQUAL:
        case GREATER:
            visitExprNode(expressionNodes.get(0), IntegerType.getInstance());
            visitExprNode(expressionNodes.get(1), IntegerType.getInstance());
            break;
        case INCLUSION:
        case NON_INCLUSION:
        case STRICT_INCLUSION:
        case STRICT_NON_INCLUSION: {
            visitExprNode(expressionNodes.get(1),
                    visitExprNode(expressionNodes.get(0), new SetType(new UntypedType())));
            break;
        }
        default:
            throw new AssertionError("Not implemented");
        }
        return BoolType.getInstance();
    }

    @Override
    public Type visitExprOperatorNode(ExpressionOperatorNode node, Type expected) {
        List<ExprNode> expressionNodes = node.getExpressionNodes();
        switch (node.getOperator()) {
        case PLUS:
        case UNARY_MINUS:
        case MOD:
        case DIVIDE:
        case POWER_OF:
            for (ExprNode exprNode : expressionNodes) {
                visitExprNode(exprNode, IntegerType.getInstance());
            }
            return unify(expected, IntegerType.getInstance(), node);
        case MULT: {
            Type found = new IntegerOrSetOfPairs(new UntypedType(), new UntypedType());
            unify(expected, found, node);
            ExprNode left = expressionNodes.get(0);
            ExprNode right = expressionNodes.get(1);
            if (found instanceof IntegerType) {
                visitExprNode(left, IntegerType.getInstance());
                visitExprNode(right, IntegerType.getInstance());
            } else if (found instanceof SetType) {
                SetType setType = (SetType) found;
                CoupleType coupleType = (CoupleType) setType.getSubType();
                visitExprNode(left, new SetType(coupleType.getLeft()));
                visitExprNode(right, new SetType(coupleType.getRight()));
            } else if (found instanceof IntegerOrSetOfPairs) {
                IntegerOrSetOfPairs integerOrSetOfPairs = (IntegerOrSetOfPairs) found;
                Type leftType = visitExprNode(expressionNodes.get(0), integerOrSetOfPairs.getLeft());
                if (leftType instanceof IntegerType) {
                    visitExprNode(expressionNodes.get(1), IntegerType.getInstance());
                } else if (leftType instanceof SetType) {
                    SetType s = (SetType) node.getType();
                    CoupleType c = (CoupleType) s.getSubType();
                    visitExprNode(expressionNodes.get(1), new SetType(c.getRight()));
                } else {
                    IntegerOrSetOfPairs s = (IntegerOrSetOfPairs) node.getType();
                    visitExprNode(expressionNodes.get(1), s.getRight());
                }
            } else {
                throw new AssertionError();
            }
            this.multOrCartNodes.add(node);
            this.typedNodes.add(node);
            return node.getType();
        }
        case MINUS:
            unify(expected, new SetOrIntegerType(new UntypedType()), node);
            visitExprNode(expressionNodes.get(0), node.getType());
            visitExprNode(expressionNodes.get(1), node.getType());
            this.minusNodes.add(node);
            this.typedNodes.add(node);
            return node.getType();

        case INTERVAL:
            unify(expected, new SetType(IntegerType.getInstance()), node);
            visitExprNode(expressionNodes.get(0), IntegerType.getInstance());
            visitExprNode(expressionNodes.get(1), IntegerType.getInstance());
            return node.getType();
        case SET_ENUMERATION: {
            SetType found = (SetType) unify(expected, new SetType(new UntypedType()), node);
            Type subtype = found.getSubType();
            for (ExprNode exprNode : expressionNodes) {
                subtype = visitExprNode(exprNode, subtype);
            }
            return node.getType();
        }
        case MININT:
        case MAXINT:
            return unify(expected, IntegerType.getInstance(), node);
        case INTEGER:
        case NATURAL1:
        case NATURAL:
        case INT:
        case NAT:
            return unify(expected, new SetType(IntegerType.getInstance()), node);
        case FALSE:
        case TRUE:
            return unify(expected, BoolType.getInstance(), node);
        case BOOL:
            return unify(expected, new SetType(BoolType.getInstance()), node);
        case SET_SUBTRACTION:
        case INTERSECTION:
        case UNION:
            unify(expected, new SetType(new UntypedType()), node);
            visitExprNode(expressionNodes.get(0), node.getType());
            visitExprNode(expressionNodes.get(1), node.getType());
            return node.getType();
        case COUPLE: {
            Type left = visitExprNode(expressionNodes.get(0), new UntypedType());
            Type right = visitExprNode(expressionNodes.get(1), new UntypedType());
            CoupleType found = new CoupleType(left, right);
            unify(expected, found, node);
            return node.getType();
        }
        case DOMAIN: {
            SetType argument = new SetType(new CoupleType(new UntypedType(), new UntypedType()));
            argument = (SetType) visitExprNode(expressionNodes.get(0), argument);
            CoupleType subType = (CoupleType) argument.getSubType();
            SetType found = new SetType(subType.getLeft());
            unify(expected, found, node);
            return node.getType();
        }
        case RANGE: {
            SetType argument = new SetType(new CoupleType(new UntypedType(), new UntypedType()));
            argument = (SetType) visitExprNode(expressionNodes.get(0), argument);
            CoupleType subType = (CoupleType) argument.getSubType();
            SetType found = new SetType(subType.getRight());
            unify(expected, found, node);
            return node.getType();
        }
        case CONCAT:
            unify(expected, new SequenceType(new UntypedType()), node);
            visitExprNode(expressionNodes.get(0), node.getType());
            visitExprNode(expressionNodes.get(1), node.getType());
            return node.getType();
        case DIRECT_PRODUCT: {
            /*
             * E ⊗ F type of result is is P(T ×(U × V)) type of E is P(T × U)
             * type of F is P(T × V)
             *
             */
            SetType found = new SetType(
                    new CoupleType(new UntypedType(), new CoupleType(new UntypedType(), new UntypedType())));
            found = (SetType) unify(expected, found, node);
            CoupleType c1 = (CoupleType) found.getSubType();
            CoupleType c2 = (CoupleType) c1.getRight();
            Type T = c1.getLeft();
            Type U = c2.getLeft();
            Type V = c2.getRight();
            SetType leftArg = (SetType) visitExprNode(expressionNodes.get(0), new SetType(new CoupleType(T, U)));
            T = ((CoupleType) leftArg.getSubType()).getLeft();
            visitExprNode(expressionNodes.get(1), new SetType(new CoupleType(T, V)));
            return node.getType();
        }
        case DOMAIN_RESTRICTION:
        case DOMAIN_SUBTRACTION: {
            // S <| r
            // S <<| r
            SetType found = new SetType(new CoupleType(new UntypedType(), new UntypedType()));
            unify(expected, found, node);
            found = (SetType) visitExprNode(expressionNodes.get(1), found);
            Type left = ((CoupleType) found.getSubType()).getLeft();
            visitExprNode(expressionNodes.get(0), new SetType(left));
            return node.getType();
        }
        case RANGE_RESTRICTION:
        case RANGE_SUBTRATION: {
            // r |> S
            // r |>> S
            SetType found = new SetType(new CoupleType(new UntypedType(), new UntypedType()));
            unify(expected, found, node);
            found = (SetType) visitExprNode(expressionNodes.get(0), node.getType());
            Type right = ((CoupleType) found.getSubType()).getLeft();
            visitExprNode(expressionNodes.get(1), new SetType(right));
            return node.getType();
        }
        case INSERT_FRONT: {
            // E -> s
            unify(expected, new SequenceType(new UntypedType()), node);
            visitExprNode(expressionNodes.get(1), node.getType());
            visitExprNode(expressionNodes.get(0), ((SequenceType) node.getType()).getSubType());
            return node.getType();
        }
        case INSERT_TAIL:
            // s <- E
            unify(expected, new SequenceType(new UntypedType()), node);
            visitExprNode(expressionNodes.get(0), node.getType());
            visitExprNode(expressionNodes.get(1), ((SequenceType) node.getType()).getSubType());
            return node.getType();
        case OVERWRITE_RELATION:
            unify(expected, new SetType(new CoupleType(new UntypedType(), new UntypedType())), node);
            visitExprNode(expressionNodes.get(0), node.getType());
            visitExprNode(expressionNodes.get(1), node.getType());
            return node.getType();
        case INVERSE_RELATION: {
            SetType argType = new SetType(new CoupleType(new UntypedType(), new UntypedType()));
            argType = (SetType) visitExprNode(expressionNodes.get(0), argType);
            CoupleType c = (CoupleType) argType.getSubType();
            SetType found = new SetType(new CoupleType(c.getRight(), c.getLeft()));
            return unify(expected, found, node);
        }
        case RESTRICT_FRONT:
        case RESTRICT_TAIL: {
            /*
             * s /|\ n s \|/ n type of result is is P(Z × T) type of s is P(Z
             * ×T) type of n is INTEGER
             */
            Type found = new SetType(new CoupleType(IntegerType.getInstance(), new UntypedType()));
            unify(expected, found, node);
            visitExprNode(expressionNodes.get(0), node.getType());
            visitExprNode(expressionNodes.get(1), IntegerType.getInstance());
            return node.getType();
        }
        case GENERALIZED_INTER:
        case GENERALIZED_UNION:
            unify(expected, new SetType(new UntypedType()), node);
            visitExprNode(expressionNodes.get(0), new SetType(node.getType()));
            return ((SetType) node.getType()).getSubType();
        case EMPTY_SEQUENCE:
            typedNodes.add(node);
            return unify(expected, new SequenceType(new UntypedType()), node);
        case SEQ_ENUMERATION: {
            SequenceType found = (SequenceType) unify(expected, new SequenceType(new UntypedType()), node);
            Type subtype = found.getSubType();
            for (ExprNode exprNode : expressionNodes) {
                subtype = visitExprNode(exprNode, subtype);
            }
            return node.getType();
        }
        case LAST:
        case FIRST:
            return unify(expected,
                    ((SequenceType) visitExprNode(expressionNodes.get(0), new SequenceType(expected))).getSubType(),
                    node);
        case FRONT:
        case TAIL:
            return visitExprNode(expressionNodes.get(0), unify(expected, new SequenceType(new UntypedType()), node));
        case SEQ:
        case SEQ1:
        case ISEQ:
        case ISEQ1: {
            SetType found = (SetType) unify(expected, new SetType(new SequenceType(new UntypedType())), node);
            Type type = ((SequenceType) found.getSubType()).getSubType();
            visitExprNode(expressionNodes.get(0), new SetType(type));
            return node.getType();
        }
        case FUNCTION_CALL: {
            // currently only for sequences
            SequenceType seqType = (SequenceType) visitExprNode(expressionNodes.get(0),
                    new SequenceType(new UntypedType()));
            visitExprNode(expressionNodes.get(1), IntegerType.getInstance());
            Type found = seqType.getSubType();
            return unify(expected, found, node);
        }
        case CARD:
            visitExprNode(expressionNodes.get(0), new SetType(new UntypedType()));
            return unify(expected, IntegerType.getInstance(), node);
        case EMPTY_SET:
            typedNodes.add(node);
            return unify(expected, new SetType(new UntypedType()), node);

        default:
            throw new AssertionError();
        }
    }

    @Override
    public Type visitIdentifierExprNode(IdentifierExprNode node, Type expected) {
        return unify(expected, node.getDeclarationNode().getType(), node);
    }

    @Override
    public Type visitCastPredicateExpressionNode(CastPredicateExpressionNode node, Type expected) {
        visitPredicateNode(node.getPredicate(), BoolType.getInstance());
        return unify(expected, BoolType.getInstance(), node);
    }

    @Override
    public Type visitIdentifierPredicateNode(IdentifierPredicateNode node, Type expected) {
        return unify(expected, node.getDeclarationNode().getType(), node);
    }

    @Override
    public Type visitNumberNode(NumberNode node, Type expected) {
        return unify(expected, IntegerType.getInstance(), node);
    }

    private Type unify(Type expected, Type found, TypedNode node) {
        try {
            Type type = found.unify(expected);
            node.setType(type);
            return type;
        } catch (UnificationException e) {
            throw new TypeErrorException(expected, found, node, e);
        }
    }

    private void setDeclarationTypes(List<DeclarationNode> list) {
        for (DeclarationNode decl : list) {
            decl.setType(new UntypedType());
            this.typedNodes.add(decl);
        }
    }

    @Override
    public Type visitQuantifiedExpressionNode(QuantifiedExpressionNode node, Type expected) {
        setDeclarationTypes(node.getDeclarationList());
        visitPredicateNode(node.getPredicateNode(), BoolType.getInstance());
        switch (node.getOperator()) {
        case QUANTIFIED_INTER:
        case QUANTIFIED_UNION:
            unify(expected, new SetType(new UntypedType()), node);
            visitPredicateNode(node.getPredicateNode(), BoolType.getInstance());
            visitExprNode(node.getExpressionNode(), node.getType());
            return node.getType();
        case SET_COMPREHENSION:
            List<Type> types = node.getDeclarationList().stream().map(TypedNode::getType).collect(Collectors.toList());
            return unify(expected, new SetType(createNestedCouple(types)), node);
        default:
            break;
        }
        throw new AssertionError("Not implemented.");
    }

    @Override
    public Type visitQuantifiedPredicateNode(QuantifiedPredicateNode node, Type expected) {
        unify(expected, BoolType.getInstance(), node);
        setDeclarationTypes(node.getDeclarationList());
        visitPredicateNode(node.getPredicateNode(), BoolType.getInstance());
        return BoolType.getInstance();
    }

    /*
     * Substitutions
     */

    @Override
    public Type visitSelectSubstitutionNode(SelectSubstitutionNode node, Type expected) {
        visitConditionsAndSubstitionsNode(node);
        return null;
    }

    @Override
    public Type visitIfSubstitutionNode(IfSubstitutionNode node, Type expected) {
        visitConditionsAndSubstitionsNode(node);
        return null;
    }

    private void visitConditionsAndSubstitionsNode(AbstractConditionsAndSubstitutionsNode node) {
        node.getConditions().stream().forEach(t -> visitPredicateNode(t, BoolType.getInstance()));
        node.getSubstitutions().stream().forEach(t -> visitSubstitutionNode(t, null));
    }

    @Override
    public Type visitConditionSubstitutionNode(ConditionSubstitutionNode node, Type expected) {
        visitPredicateNode(node.getCondition(), BoolType.getInstance());
        visitSubstitutionNode(node.getSubstitution(), expected);
        return null;
    }

    @Override
    public Type visitSingleAssignSubstitution(SingleAssignSubstitutionNode node, Type expected) {
        Type type = visitIdentifierExprNode(node.getIdentifier(), new UntypedType());
        visitExprNode(node.getValue(), type);
        return null;
    }

    @Override
    public Type visitParallelSubstitutionNode(ParallelSubstitutionNode node, Type expected) {
        for (SubstitutionNode sub : node.getSubstitutions()) {
            visitSubstitutionNode(sub, null);
        }
        return null;
    }

    @Override
    public Type visitAnySubstitution(AnySubstitutionNode node, Type expected) {
        setDeclarationTypes(node.getParameters());
        visitPredicateNode(node.getWherePredicate(), BoolType.getInstance());
        visitSubstitutionNode(node.getThenSubstitution(), null);
        return null;
    }

    @Override
    public Type visitBecomesElementOfSubstitutionNode(BecomesElementOfSubstitutionNode node, Type expected) {
        List<Type> types = node.getIdentifiers().stream().map(t -> visitIdentifierExprNode(t, new UntypedType()))
                .collect(Collectors.toList());
        SetType type = new SetType(createNestedCouple(types));
        visitExprNode(node.getExpression(), type);
        return null;
    }

    private Type createNestedCouple(List<Type> types) {
        Type left = types.get(0);
        for (int i = 1; i < types.size(); i++) {
            left = new CoupleType(left, types.get(i));
        }
        return left;
    }

    @Override
    public Type visitBecomesSuchThatSubstitutionNode(BecomesSuchThatSubstitutionNode node, Type expected) {
        node.getIdentifiers().stream().forEach(t -> visitIdentifierExprNode(t, new UntypedType()));
        visitPredicateNode(node.getPredicate(), BoolType.getInstance());
        return null;
    }

    @Override
    public Type visitSkipSubstitutionNode(SkipSubstitutionNode node, Type expected) {
        return null;
    }

}
