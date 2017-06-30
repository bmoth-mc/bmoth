package de.bmoth.parser.ast;

import de.bmoth.parser.ast.nodes.*;
import de.bmoth.parser.ast.nodes.ltl.*;
import de.bmoth.parser.ast.types.*;
import de.bmoth.parser.ast.visitors.AbstractVisitor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TypeChecker implements AbstractVisitor<BType, BType> {

    private static final String TYPE_ERROR = "TYPE_ERROR";

    private Set<ExpressionOperatorNode> minusNodes = new HashSet<>();
    private Set<ExpressionOperatorNode> multOrCartNodes = new HashSet<>();
    private Set<TypedNode> typedNodes = new HashSet<>();

    public static void typecheckMachineNode(MachineNode machineNode) throws TypeErrorException {
        TypeChecker typeChecker = new TypeChecker();
        try {
            typeChecker.checkMachineNode(machineNode);
        } catch (TypeCheckerVisitorException e) {
            final Logger logger = Logger.getLogger(e.getClass().getName());
            logger.log(Level.SEVERE, TYPE_ERROR, e);
            throw e.getTypeErrorException();
        }

    }

    public static void typecheckFormulaNode(FormulaNode formulaNode) throws TypeErrorException {
        TypeChecker typeChecker = new TypeChecker();
        try {
            typeChecker.checkFormulaNode(formulaNode);
        } catch (TypeCheckerVisitorException e) {
            final Logger logger = Logger.getLogger(e.getClass().getName());
            logger.log(Level.SEVERE, TYPE_ERROR, e);
            throw e.getTypeErrorException();
        }
    }

    public static void typecheckLTLFormulaNode(LTLFormula ltlFormulaAst) throws TypeErrorException {
        TypeChecker typeChecker = new TypeChecker();
        try {
            typeChecker.checkLTLFormulaNode(ltlFormulaAst);
        } catch (TypeCheckerVisitorException e) {
            final Logger logger = Logger.getLogger(e.getClass().getName());
            logger.log(Level.SEVERE, TYPE_ERROR, e);
            throw e.getTypeErrorException();
        }
    }

    class TypeCheckerVisitorException extends RuntimeException {
        private static final long serialVersionUID = 1744515995462230895L;
        private final TypeErrorException typeErrorException;

        TypeCheckerVisitorException(TypeErrorException typeErrorException) {
            this.typeErrorException = typeErrorException;
        }

        public TypeErrorException getTypeErrorException() {
            return this.typeErrorException;
        }
    }

    private TypeChecker() {
    }

    private void checkFormulaNode(FormulaNode formulaNode) {
        for (DeclarationNode node : formulaNode.getImplicitDeclarations()) {
            node.setType(new UntypedType());
        }
        Node formula = formulaNode.getFormula();
        if (formula instanceof PredicateNode) {
            visitPredicateNode((PredicateNode) formula, BoolType.getInstance());
        } else {
            // expression formula
            BType type = visitExprNode((ExprNode) formula, new UntypedType());
            if (type.isUntyped()) {
                throw new TypeCheckerVisitorException(new TypeErrorException("Can not infer type of formula: " + type));
            }
        }

        // check that all implicitly declared variables have a type, otherwise
        // throw an exception
        for (DeclarationNode node : formulaNode.getImplicitDeclarations()) {
            if (node.getType().isUntyped()) {
                throw new TypeCheckerVisitorException(
                        new TypeErrorException("Can not infer the type of local variable '" + node.getName()
                                + "' Current type: " + node.getType()));
            }
        }
        performPostActions();
    }

    private void checkLTLFormulaNode(LTLFormula ltlFormulaAst) {
        for (DeclarationNode node : ltlFormulaAst.getImplicitDeclarations()) {
            node.setType(new UntypedType());
        }
        visitLTLNode(ltlFormulaAst.getLTLNode(), null);

        // check that all implicitly declared variables have a type, otherwise
        // throw an exception
        for (DeclarationNode node : ltlFormulaAst.getImplicitDeclarations()) {
            if (node.getType().isUntyped()) {
                throw new TypeCheckerVisitorException(
                        new TypeErrorException("Can not infer the type of local variable '" + node.getName()
                                + "' Current type: " + node.getType()));
            }
        }
        performPostActions();
    }

    private void checkMachineNode(MachineNode machineNode) {
        for (EnumeratedSetDeclarationNode eSet : machineNode.getEnumaratedSets()) {
            DeclarationNode setDeclaration = eSet.getSetDeclaration();
            EnumeratedSetElementType userDefinedElementType = new EnumeratedSetElementType(setDeclaration.getName(),
                    eSet.getElementsAsStrings());
            setDeclaration.setType(new SetType(userDefinedElementType));
            for (DeclarationNode element : eSet.getElements()) {
                element.setType(userDefinedElementType);
            }
        }

        for (DeclarationNode deferredSet : machineNode.getDeferredSets()) {
            DeferredSetElementType userDefinedElementType = new DeferredSetElementType(deferredSet.getName());
            deferredSet.setType(new SetType(userDefinedElementType));
        }

        // set all constants to untyped
        machineNode.getConstants().forEach(con -> con.setType(new UntypedType()));

        // visit the properties clause
        if (machineNode.getProperties() != null) {
            visitPredicateNode(machineNode.getProperties(), BoolType.getInstance());
        }

        // check that all constants have a type, otherwise throw an exception
        for (DeclarationNode con : machineNode.getConstants()) {
            if (con.isUntyped()) {
                throw new TypeCheckerVisitorException(new TypeErrorException(
                        "Can not infer the type of constant " + con.getName() + ". Type variable: " + con.getType()));
            }
        }

        // set all variables to untyped
        machineNode.getVariables().forEach(var -> var.setType(new UntypedType()));

        // visit the invariant clause
        if (machineNode.getInvariant() != null) {
            visitPredicateNode(machineNode.getInvariant(), BoolType.getInstance());
        }

        // check that all variables have type, otherwise throw an exception
        for (DeclarationNode var : machineNode.getVariables()) {
            if (var.isUntyped()) {
                throw new TypeCheckerVisitorException(new TypeErrorException(
                        "Can not infer the type of variable " + var.getName() + ". Type variable: " + var.getType()));
            }
        }

        // visit the initialisation clause
        if (machineNode.getInitialisation() != null) {
            visitSubstitutionNode(machineNode.getInitialisation(), null);
        }

        // visit all operations
        machineNode.getOperations().forEach(op -> visitSubstitutionNode(op.getSubstitution(), null));

        performPostActions();
    }

    private void performPostActions() {
        // Check that all local variables have type.
        for (TypedNode node : typedNodes) {
            if (node.getType().isUntyped()) {
                if (node instanceof DeclarationNode) {
                    DeclarationNode var = (DeclarationNode) node;
                    throw new TypeCheckerVisitorException(
                            new TypeErrorException("Can not infer the type of local variable " + var.getName()));
                } else if (node instanceof ExpressionOperatorNode) {
                    ExpressionOperatorNode exprNode = (ExpressionOperatorNode) node;
                    throw new TypeCheckerVisitorException(new TypeErrorException(
                            "Can not infer the complete type of operator " + exprNode.getOperator()));
                }
            }
        }

        // post actions
        for (ExpressionOperatorNode minusNode : minusNodes) {
            BType type = minusNode.getType();
            if (type instanceof SetType) {
                minusNode.setOperator(ExpressionOperatorNode.ExpressionOperator.SET_SUBTRACTION);
            }
        }

        // post actions
        for (ExpressionOperatorNode node : multOrCartNodes) {
            BType type = node.getType();
            if (type instanceof SetType) {
                node.setOperator(ExpressionOperatorNode.ExpressionOperator.CARTESIAN_PRODUCT);
            }
        }
    }

    @Override
    public BType visitPredicateOperatorNode(PredicateOperatorNode node, BType expected) {
        unify(expected, BoolType.getInstance(), node);
        List<PredicateNode> predicateArguments = node.getPredicateArguments();
        for (PredicateNode predicateNode : predicateArguments) {
            visitPredicateNode(predicateNode, BoolType.getInstance());
        }
        return BoolType.getInstance();
    }

    @Override
    public BType visitPredicateOperatorWithExprArgs(PredicateOperatorWithExprArgsNode node, BType expected) {
        unify(expected, BoolType.getInstance(), node);
        final List<ExprNode> expressionNodes = node.getExpressionNodes();
        switch (node.getOperator()) {
        case EQUAL:
        case NOT_EQUAL:
            visitExprNode(expressionNodes.get(1), visitExprNode(expressionNodes.get(0), new UntypedType()));
            break;
        case NOT_BELONGING:
        case ELEMENT_OF:
            BType setType = visitExprNode(expressionNodes.get(0), new UntypedType());
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
    public BType visitExprOperatorNode(ExpressionOperatorNode node, BType expected) {
        List<ExprNode> expressionNodes = node.getExpressionNodes();
        switch (node.getOperator()) {
        case PLUS:
        case UNARY_MINUS:
        case MOD:
        case DIVIDE:
        case POWER_OF:
            expressionNodes.forEach(n -> visitExprNode(n, IntegerType.getInstance()));
            return unify(expected, IntegerType.getInstance(), node);
        case MULT: {
            BType found = new IntegerOrSetOfPairs(new UntypedType(), new UntypedType());
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
                BType leftType = visitExprNode(expressionNodes.get(0), integerOrSetOfPairs.getLeft());
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
            BType subtype = found.getSubType();
            for (ExprNode exprNode : expressionNodes) {
                subtype = visitExprNode(exprNode, subtype);
            }
            return node.getType();
        }
        case MIN:
        case MAX:
            unify(expected, IntegerType.getInstance(), node);
            visitExprNode(expressionNodes.get(0), new SetType(IntegerType.getInstance()));
            return node.getType();
        case MININT:
        case MAXINT:
            return unify(expected, IntegerType.getInstance(), node);
        case INTEGER:
        case NATURAL:
        case NATURAL1:
        case INT:
        case NAT:
        case NAT1:
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
        case COUPLE:
            return unify(expected, new CoupleType(visitExprNode(expressionNodes.get(0), new UntypedType()),
                    visitExprNode(expressionNodes.get(1), new UntypedType())), node);
        case DOMAIN: {
            SetType argument = new SetType(new CoupleType(new UntypedType(), new UntypedType()));
            argument = (SetType) visitExprNode(expressionNodes.get(0), argument);
            CoupleType subType = (CoupleType) argument.getSubType();
            SetType found = new SetType(subType.getLeft());
            unify(expected, found, node);
            return node.getType();
        }
        case RANGE: {
            SetType setType = (SetType) unify(expected, new SetType(new UntypedType()), node);
            visitExprNode(expressionNodes.get(0), new SetType(new CoupleType(new UntypedType(), setType.getSubType())));
            return node.getType();
        }
        case CONCAT:
            unify(expected, new SetType(new CoupleType(IntegerType.getInstance(), new UntypedType())), node);
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
            BType typeOfT = c1.getLeft();
            BType typeOfU = c2.getLeft();
            BType typeOfV = c2.getRight();
            SetType leftArg = (SetType) visitExprNode(expressionNodes.get(0),
                    new SetType(new CoupleType(typeOfT, typeOfU)));
            typeOfT = ((CoupleType) leftArg.getSubType()).getLeft();
            visitExprNode(expressionNodes.get(1), new SetType(new CoupleType(typeOfT, typeOfV)));
            return node.getType();
        }
        case DOMAIN_RESTRICTION:
        case DOMAIN_SUBTRACTION:
            // S <| r
            // S <<| r
            unify(expected, createNewRelationType(), node);
            visitExprNode(expressionNodes.get(1), node.getType());
            visitExprNode(expressionNodes.get(0), new SetType(getLeftTypeOfRelationType(node.getType())));
            return node.getType();
        case RANGE_RESTRICTION:
        case RANGE_SUBTRATION:
            // r |> S
            // r |>> S
            unify(expected, createNewRelationType(), node);
            visitExprNode(expressionNodes.get(0), node.getType());
            visitExprNode(expressionNodes.get(1), new SetType(getRightTypeOfRelationType(node.getType())));
            return node.getType();
        case INSERT_FRONT:
            // E -> s
            unify(expected, new SetType(new CoupleType(IntegerType.getInstance(), new UntypedType())), node);
            visitExprNode(expressionNodes.get(1), node.getType());
            visitExprNode(expressionNodes.get(0), getRightTypeOfRelationType(node.getType()));
            return node.getType();
        case INSERT_TAIL:
            // s <- E
            unify(expected, new SetType(new CoupleType(IntegerType.getInstance(), new UntypedType())), node);
            visitExprNode(expressionNodes.get(0), node.getType());
            visitExprNode(expressionNodes.get(1), getRightTypeOfRelationType(node.getType()));
            return node.getType();
        case OVERWRITE_RELATION:
            unify(expected, new SetType(new CoupleType(new UntypedType(), new UntypedType())), node);
            visitExprNode(expressionNodes.get(0), node.getType());
            visitExprNode(expressionNodes.get(1), node.getType());
            return node.getType();
        case INVERSE_RELATION:
            SetType argType = (SetType) visitExprNode(expressionNodes.get(0),
                    new SetType(new CoupleType(new UntypedType(), new UntypedType())));
            CoupleType c = (CoupleType) argType.getSubType();
            return unify(expected, new SetType(new CoupleType(c.getRight(), c.getLeft())), node);
        case RESTRICT_FRONT:
        case RESTRICT_TAIL:
            // s /|\ n s \|/ n
            // type of result is is P(Z × T)
            // type of s is P(Z×T)
            // type of n is INTEGER
            unify(expected, new SetType(new CoupleType(IntegerType.getInstance(), new UntypedType())), node);
            visitExprNode(expressionNodes.get(0), node.getType());
            visitExprNode(expressionNodes.get(1), IntegerType.getInstance());
            return node.getType();
        case GENERALIZED_INTER:
        case GENERALIZED_UNION:
            unify(expected, new SetType(new UntypedType()), node);
            visitExprNode(expressionNodes.get(0), new SetType(node.getType()));
            return ((SetType) node.getType()).getSubType();
        case EMPTY_SEQUENCE:
            typedNodes.add(node);
            return unify(expected, new SetType(new CoupleType(IntegerType.getInstance(), new UntypedType())), node);
        case SEQ_ENUMERATION: {
            SetType found = (SetType) unify(expected,
                    new SetType(new CoupleType(IntegerType.getInstance(), new UntypedType())), node);
            BType elementType = ((CoupleType) found.getSubType()).getRight();
            for (ExprNode exprNode : expressionNodes) {
                elementType = visitExprNode(exprNode, elementType);
            }
            return node.getType();
        }
        case LAST:
        case FIRST:
            return unify(expected, getRightTypeOfRelationType(visitExprNode(expressionNodes.get(0),
                    new SetType(new CoupleType(IntegerType.getInstance(), new UntypedType())))), node);
        case FRONT:
        case TAIL:
            return visitExprNode(expressionNodes.get(0),
                    unify(expected, new SetType(new CoupleType(IntegerType.getInstance(), new UntypedType())), node));
        case SEQ:
        case SEQ1:
        case ISEQ:
        case ISEQ1: {
            SetType found = (SetType) unify(expected,
                    new SetType(new SetType(new CoupleType(IntegerType.getInstance(), new UntypedType()))), node);
            SetType type = (SetType) found.getSubType();
            CoupleType coupleType = (CoupleType) type.getSubType();
            visitExprNode(expressionNodes.get(0), new SetType(coupleType.getRight()));
            return node.getType();
        }
        case FUNCTION_CALL: {
            // visit arguments
            List<ExprNode> arguments = expressionNodes.stream().filter(e -> expressionNodes.get(0) != e)
                    .collect(Collectors.toList());
            arguments.forEach(e -> visitExprNode(e, new UntypedType()));

            // collect types of arguments; Note, this should be done after all
            // arguments have been visited because the type object of a previous
            // argument could change.
            List<BType> typesList = arguments.stream().map(ExprNode::getType).collect(Collectors.toList());
            BType domType = createNestedCouple(typesList);

            // visit function base
            SetType baseType = (SetType) visitExprNode(expressionNodes.get(0),
                    new SetType(new CoupleType(domType, new UntypedType())));
            return unify(expected, ((CoupleType) baseType.getSubType()).getRight(), node);
        }
        case CARD:
            visitExprNode(expressionNodes.get(0), new SetType(new UntypedType()));
            return unify(expected, IntegerType.getInstance(), node);
        case EMPTY_SET:
            typedNodes.add(node);
            return unify(expected, new SetType(new UntypedType()), node);
        case SET_RELATION: {
            SetType found = new SetType(new SetType(new CoupleType(new UntypedType(), new UntypedType())));
            found = (SetType) unify(expected, found, node);
            SetType set = (SetType) found.getSubType();
            CoupleType couple = (CoupleType) set.getSubType();
            visitExprNode(node.getExpressionNodes().get(0), new SetType(couple.getLeft()));
            visitExprNode(node.getExpressionNodes().get(1), new SetType(couple.getRight()));
            return node.getType();
        }
        default:
            throw new AssertionError();
        }
    }

    private SetType createNewRelationType() {
        return new SetType(new CoupleType(new UntypedType(), new UntypedType()));
    }

    private BType getRightTypeOfRelationType(BType s) {
        SetType setType = (SetType) s;
        CoupleType c = (CoupleType) setType.getSubType();
        return c.getRight();
    }

    private BType getLeftTypeOfRelationType(BType s) {
        SetType setType = (SetType) s;
        CoupleType c = (CoupleType) setType.getSubType();
        return c.getLeft();
    }

    @Override
    public BType visitIdentifierExprNode(IdentifierExprNode node, BType expected) {
        return unify(expected, node.getDeclarationNode().getType(), node);
    }

    @Override
    public BType visitCastPredicateExpressionNode(CastPredicateExpressionNode node, BType expected) {
        visitPredicateNode(node.getPredicate(), BoolType.getInstance());
        return unify(expected, BoolType.getInstance(), node);
    }

    @Override
    public BType visitIdentifierPredicateNode(IdentifierPredicateNode node, BType expected) {
        return unify(expected, node.getDeclarationNode().getType(), node);
    }

    @Override
    public BType visitNumberNode(NumberNode node, BType expected) {
        return unify(expected, IntegerType.getInstance(), node);
    }

    private BType unify(BType expected, BType found, TypedNode node) {
        try {
            BType type = found.unify(expected);
            node.setType(type);
            return type;
        } catch (UnificationException e) {
            throw new TypeCheckerVisitorException(new TypeErrorException(expected, found, node, e));
        }
    }

    private void setDeclarationTypes(List<DeclarationNode> list) {
        for (DeclarationNode decl : list) {
            decl.setType(new UntypedType());
            this.typedNodes.add(decl);
        }
    }

    @Override
    public BType visitQuantifiedExpressionNode(QuantifiedExpressionNode node, BType expected) {
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
            List<BType> types = node.getDeclarationList().stream().map(TypedNode::getType).collect(Collectors.toList());
            return unify(expected, new SetType(createNestedCouple(types)), node);
        default:
            break;
        }
        throw new AssertionError("Not implemented.");
    }

    @Override
    public BType visitQuantifiedPredicateNode(QuantifiedPredicateNode node, BType expected) {
        unify(expected, BoolType.getInstance(), node);
        setDeclarationTypes(node.getDeclarationList());
        visitPredicateNode(node.getPredicateNode(), BoolType.getInstance());
        return BoolType.getInstance();
    }

    /*
     * Substitutions
     */

    @Override
    public BType visitSelectSubstitutionNode(SelectSubstitutionNode node, BType expected) {
        visitConditionsAndSubstitionsNode(node);
        return null;
    }

    @Override
    public BType visitIfSubstitutionNode(IfSubstitutionNode node, BType expected) {
        visitConditionsAndSubstitionsNode(node);
        return null;
    }

    private void visitConditionsAndSubstitionsNode(AbstractIfAndSelectSubstitutionsNode node) {
        node.getConditions().forEach(t -> visitPredicateNode(t, BoolType.getInstance()));
        node.getSubstitutions().forEach(t -> visitSubstitutionNode(t, null));
        if (node.getElseSubstitution() != null) {
            visitSubstitutionNode(node.getElseSubstitution(), null);
        }

    }

    @Override
    public BType visitConditionSubstitutionNode(ConditionSubstitutionNode node, BType expected) {
        visitPredicateNode(node.getCondition(), BoolType.getInstance());
        visitSubstitutionNode(node.getSubstitution(), expected);
        return null;
    }

    @Override
    public BType visitSingleAssignSubstitution(SingleAssignSubstitutionNode node, BType expected) {
        BType type = visitIdentifierExprNode(node.getIdentifier(), new UntypedType());
        visitExprNode(node.getValue(), type);
        return null;
    }

    @Override
    public BType visitParallelSubstitutionNode(ParallelSubstitutionNode node, BType expected) {
        for (SubstitutionNode sub : node.getSubstitutions()) {
            visitSubstitutionNode(sub, null);
        }
        return null;
    }

    @Override
    public BType visitAnySubstitution(AnySubstitutionNode node, BType expected) {
        setDeclarationTypes(node.getParameters());
        visitPredicateNode(node.getWherePredicate(), BoolType.getInstance());
        visitSubstitutionNode(node.getThenSubstitution(), null);
        return null;
    }

    @Override
    public BType visitBecomesElementOfSubstitutionNode(BecomesElementOfSubstitutionNode node, BType expected) {
        List<BType> types = node.getIdentifiers().stream().map(t -> visitIdentifierExprNode(t, new UntypedType()))
                .collect(Collectors.toList());
        SetType type = new SetType(createNestedCouple(types));
        visitExprNode(node.getExpression(), type);
        return null;
    }

    private BType createNestedCouple(List<BType> types) {
        BType left = types.get(0);
        for (int i = 1; i < types.size(); i++) {
            left = new CoupleType(left, types.get(i));
        }
        return left;
    }

    @Override
    public BType visitBecomesSuchThatSubstitutionNode(BecomesSuchThatSubstitutionNode node, BType expected) {
        node.getIdentifiers().forEach(t -> visitIdentifierExprNode(t, new UntypedType()));
        visitPredicateNode(node.getPredicate(), BoolType.getInstance());
        return null;
    }

    @Override
    public BType visitSkipSubstitutionNode(SkipSubstitutionNode node, BType expected) {
        return null;
    }

    @Override
    public BType visitEnumerationSetNode(EnumerationSetNode node, BType expected) {
        return unify(expected, node.getEnumeratedSetDeclarationNode().getSetDeclaration().getType(), node);
    }

    @Override
    public BType visitDeferredSetNode(DeferredSetNode node, BType expected) {
        return unify(expected, node.getDeclarationNode().getType(), node);
    }

    @Override
    public BType visitEnumeratedSetElementNode(EnumeratedSetElementNode node, BType expected) {
        return unify(expected, node.getDeclarationNode().getType(), node);
    }

    @Override
    public BType visitLTLPrefixOperatorNode(LTLPrefixOperatorNode node, BType expected) {
        visitLTLNode(node.getArgument(), expected);
        return null;
    }

    @Override
    public BType visitLTLKeywordNode(LTLKeywordNode node, BType expected) {
        return null;
    }

    @Override
    public BType visitLTLInfixOperatorNode(LTLInfixOperatorNode node, BType expected) {
        visitLTLNode(node.getLeft(), expected);
        visitLTLNode(node.getRight(), expected);
        return null;
    }

    @Override
    public BType visitLTLBPredicateNode(LTLBPredicateNode node, BType expected) {
        visitPredicateNode(node.getPredicate(), BoolType.getInstance());
        return null;
    }

}
