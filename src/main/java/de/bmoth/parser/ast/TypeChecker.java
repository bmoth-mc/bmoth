package de.bmoth.parser.ast;

import de.bmoth.exceptions.TypeErrorException;
import de.bmoth.exceptions.UnificationException;
import de.bmoth.parser.ast.nodes.*;
import de.bmoth.parser.ast.types.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TypeChecker extends AbstractVisitor<Type, Type> {

    Set<ExpressionOperatorNode> minusNodes = new HashSet<>();
    Set<ExpressionOperatorNode> multOrCartNodes = new HashSet<>();
    Set<TypedNode> typedNodes = new HashSet<>();

    public TypeChecker(MachineNode machineNode) {
        // set all constants to untyped
        for (DeclarationNode con : machineNode.getConstants()) {
            con.setType(new UntypedType());
        }
        // visit the properties clause
        if (machineNode.getProperties() != null) {
            super.visitPredicateNode(machineNode.getProperties(), BoolType.getInstance());
        }

        // check that all constants have a type, otherwise throw an exception
        for (DeclarationNode con : machineNode.getConstants()) {
            if (con.getType().isUntyped()) {
                throw new TypeErrorException(con,
                    "Can not infer the type of constant " + con.getName() + ". Type variable: " + con.getType());
            }
        }

        // set all variables to untyped
        for (DeclarationNode var : machineNode.getVariables()) {
            var.setType(new UntypedType());
        }

        // visit the invariant clause
        if (machineNode.getInvariant() != null) {
            super.visitPredicateNode(machineNode.getInvariant(), BoolType.getInstance());
        }
        // check that all variables have type, otherwise throw an exception
        for (DeclarationNode var : machineNode.getVariables()) {
            if (var.getType().isUntyped()) {
                throw new TypeErrorException(var,
                    "Can not infer the type of variable " + var.getName() + ". Type variable: " + var.getType());
            }
        }

        // visit the initialisation clause
        if (machineNode.getInitialisation() != null) {
            super.visitSubstitutionNode(machineNode.getInitialisation(), null);
        }

        // visit all operations
        for (OperationNode op : machineNode.getOperations()) {
            super.visitSubstitutionNode(op.getSubstitution(), null);
        }

        performPostActions();
    }

    public TypeChecker(FormulaNode formulaNode) {
        for (DeclarationNode node : formulaNode.getImplicitDeclarations()) {
            node.setType(new UntypedType());
        }
        Node formula = formulaNode.getFormula();
        if (formula instanceof PredicateNode) {
            super.visitPredicateNode((PredicateNode) formula, BoolType.getInstance());
        } else {
            // expression formula
            Type type = super.visitExprNode((ExprNode) formula, new UntypedType());
            if (type.isUntyped()) {
                throw new TypeErrorException(formula, "Can not infer type of formula: " + type);
            }
        }

        // check that all implicitly declared variables have a type, otherwise
        // throw an exception
        for (DeclarationNode node : formulaNode.getImplicitDeclarations()) {
            if (node.getType().isUntyped()) {
                throw new TypeErrorException(node, "Can not infer the type of local variable '" + node.getName()
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
                    throw new TypeErrorException(var, "Can not infer the type of local variable " + var.getName());
                } else if (node instanceof ExpressionOperatorNode) {
                    ExpressionOperatorNode exprNode = (ExpressionOperatorNode) node;
                    throw new TypeErrorException(node,
                        "Can not infer the complete type of operator " + exprNode.getOperator());
                }
            }
        }

        // post actions
        for (ExpressionOperatorNode minusNode : minusNodes) {
            Type type = minusNode.getType();
            if (type instanceof SetType) {
                minusNode.changeOperator(ExpressionOperatorNode.ExpressionOperator.SET_SUBTRACTION);
            }
        }

        // post actions
        for (ExpressionOperatorNode node : multOrCartNodes) {
            Type type = node.getType();
            if (type instanceof SetType) {
                node.changeOperator(ExpressionOperatorNode.ExpressionOperator.CARTESIAN_PRODUCT);
            }
        }
    }

    @Override
    public Type visitPredicateOperatorNode(PredicateOperatorNode node, Type expected) {
        try {
            BoolType.getInstance().unify(expected);
        } catch (UnificationException e) {
            throw new TypeErrorException(node, expected, BoolType.getInstance());
        }
        List<PredicateNode> predicateArguments = node.getPredicateArguments();
        for (PredicateNode predicateNode : predicateArguments) {
            visitPredicateNode(predicateNode, BoolType.getInstance());
        }
        node.setType(BoolType.getInstance());
        return BoolType.getInstance();
    }

    @Override
    public Type visitPredicateOperatorWithExprArgs(PredicateOperatorWithExprArgsNode node, Type expected) {
        try {
            BoolType.getInstance().unify(expected);
        } catch (UnificationException e) {
            throw new TypeErrorException(node, expected, BoolType.getInstance());
        }
        final List<ExprNode> expressionNodes = node.getExpressionNodes();
        switch (node.getOperator()) {
            case EQUAL:
            case NOT_EQUAL: {
                Type type = visitExprNode(expressionNodes.get(0), new UntypedType());
                visitExprNode(expressionNodes.get(1), type);
                break;
            }
            case NOT_BELONGING:
            case ELEMENT_OF: {
                Type type = visitExprNode(expressionNodes.get(0), new UntypedType());
                visitExprNode(expressionNodes.get(1), new SetType(type));
                break;
            }
            case LESS_EQUAL:
            case LESS:
            case GREATER_EQUAL:
            case GREATER: {
                visitExprNode(expressionNodes.get(0), IntegerType.getInstance());
                visitExprNode(expressionNodes.get(1), IntegerType.getInstance());
                break;
            }
            case INCLUSION:
            case NON_INCLUSION:
            case STRICT_INCLUSION:
            case STRICT_NON_INCLUSION: {
                Type type = visitExprNode(expressionNodes.get(0), new SetType(new UntypedType()));
                visitExprNode(expressionNodes.get(1), type);
                break;
            }
            default:
                throw new AssertionError("Not implemented");
        }
        node.setType(BoolType.getInstance());
        return BoolType.getInstance();
    }

    @Override
    public Type visitExprOperatorNode(ExpressionOperatorNode node, Type expected) {
        List<ExprNode> expressionNodes = node.getExpressionNodes();
        Type returnType = null;
        switch (node.getOperator()) {
            case PLUS:
            case UNARY_MINUS:
            case MOD:
            case DIVIDE:
            case POWER_OF: {
                try {
                    IntegerType.getInstance().unify(expected);
                } catch (UnificationException e) {
                    throw new TypeErrorException(node, expected, IntegerType.getInstance());
                }
                for (ExprNode exprNode : expressionNodes) {
                    visitExprNode(exprNode, IntegerType.getInstance());
                }
                returnType = IntegerType.getInstance();
                break;
            }
            case MULT: {
                UntypedType dd = new UntypedType();
                Type found = new IntegerOrSetOfPairs(new UntypedType(), dd);
                // System.out.println(dd);
                try {
                    found = found.unify(expected);
                } catch (UnificationException e) {
                    throw new TypeErrorException(node, expected, found);
                }
                node.setType(found);
                ExprNode left = expressionNodes.get(0);
                ExprNode right = expressionNodes.get(1);
                if (found instanceof IntegerType) {
                    visitExprNode(left, IntegerType.getInstance());
                    visitExprNode(right, IntegerType.getInstance());
                } else if (found instanceof SetType) {
                    SetType setType = (SetType) found;
                    CoupleType coupleType = (CoupleType) setType.getSubtype();
                    visitExprNode(left, new SetType(coupleType.getLeft()));
                    visitExprNode(right, new SetType(coupleType.getRight()));
                } else if (found instanceof IntegerOrSetOfPairs) {
                    IntegerOrSetOfPairs integerOrSetOfPairs = (IntegerOrSetOfPairs) found;
                    Type leftType = visitExprNode(expressionNodes.get(0), integerOrSetOfPairs.getLeft());
                    if (leftType instanceof IntegerType) {
                        visitExprNode(expressionNodes.get(1), IntegerType.getInstance());
                    } else if (leftType instanceof SetType) {
                        SetType s = (SetType) node.getType();
                        CoupleType c = (CoupleType) s.getSubtype();
                        visitExprNode(expressionNodes.get(1), new SetType(c.getRight()));
                    } else {
                        IntegerOrSetOfPairs s = (IntegerOrSetOfPairs) node.getType();
                        visitExprNode(expressionNodes.get(1), s.getRight());
                    }
                } else {
                    throw new RuntimeException();
                }
                this.multOrCartNodes.add(node);
                this.typedNodes.add(node);
                // System.out.println(node.getType());
                returnType = node.getType();
                break;
            }
            case MINUS: {
                Type found = new SetOrIntegerType(new UntypedType());
                try {
                    found = found.unify(expected);
                } catch (UnificationException e) {
                    throw new TypeErrorException(node, expected, found);
                }
                found = visitExprNode(expressionNodes.get(0), found);
                found = visitExprNode(expressionNodes.get(1), found);
                returnType = found;
                this.minusNodes.add(node);
                this.typedNodes.add(node);
                break;
            }
            case INTERVAL: {
                Type found = new SetType(IntegerType.getInstance());
                try {
                    found = found.unify(expected);
                } catch (UnificationException e) {
                    throw new TypeErrorException(node, expected, found);
                }
                visitExprNode(expressionNodes.get(0), IntegerType.getInstance());
                visitExprNode(expressionNodes.get(1), IntegerType.getInstance());
                returnType = found;
                break;
            }
            case SET_ENUMERATION: {
                SetType found = new SetType(new UntypedType());
                try {
                    found = (SetType) found.unify(expected);
                } catch (UnificationException e) {
                    throw new TypeErrorException(node, expected, found);
                }
                Type subtype = found.getSubtype();
                for (ExprNode exprNode : expressionNodes) {
                    subtype = visitExprNode(exprNode, subtype);
                }
                returnType = new SetType(subtype);
                break;
            }
            case MININT:
            case MAXINT: {
                try {
                    IntegerType.getInstance().unify(expected);
                } catch (UnificationException e) {
                    throw new TypeErrorException(node, expected, IntegerType.getInstance());
                }
                return IntegerType.getInstance();
            }
            case INTEGER:
            case NATURAL1:
            case NATURAL:
            case INT:
            case NAT: {
                Type type = new SetType(IntegerType.getInstance());
                try {
                    type = type.unify(expected);
                } catch (UnificationException e) {
                    throw new TypeErrorException(node, expected, type);
                }
                returnType = type;
                break;
            }
            case FALSE:
            case TRUE: {
                try {
                    BoolType.getInstance().unify(expected);
                } catch (UnificationException e) {
                    throw new TypeErrorException(node, expected, BoolType.getInstance());
                }
                returnType = BoolType.getInstance();
                break;
            }
            case BOOL: {
                SetType found = new SetType(BoolType.getInstance());
                try {
                    found.unify(expected);
                } catch (UnificationException e) {
                    throw new TypeErrorException(node, expected, found);
                }
                returnType = found;
                break;
            }
            case SET_SUBTRACTION:
            case INTERSECTION:
            case UNION: {
                UntypedType untypedType = new UntypedType();
                Type type = new SetType(untypedType);
                try {
                    type = type.unify(expected);
                } catch (UnificationException e) {
                    throw new TypeErrorException(node, expected, type);
                }
                type = visitExprNode(expressionNodes.get(0), type);
                type = visitExprNode(expressionNodes.get(1), type);

                returnType = type;
                break;
            }
            case COUPLE: {
                Type left = visitExprNode(expressionNodes.get(0), new UntypedType());
                Type right = visitExprNode(expressionNodes.get(1), new UntypedType());
                CoupleType couple = new CoupleType(left, right);
                try {
                    couple = couple.unify(expected);
                } catch (UnificationException e) {
                    throw new TypeErrorException(node, expected, couple);
                }
                returnType = couple;
                break;
            }
            case DOMAIN: {
                SetType argument = new SetType(new CoupleType(new UntypedType(), new UntypedType()));
                argument = (SetType) visitExprNode(expressionNodes.get(0), argument);
                CoupleType subType = (CoupleType) argument.getSubtype();
                SetType found = new SetType(subType.getLeft());
                try {
                    found = (SetType) found.unify(expected);
                } catch (UnificationException e) {
                    throw new TypeErrorException(node, expected, found);
                }
                returnType = found;
                break;
            }
            case RANGE: {
                SetType argument = new SetType(new CoupleType(new UntypedType(), new UntypedType()));
                argument = (SetType) visitExprNode(expressionNodes.get(0), argument);
                CoupleType subType = (CoupleType) argument.getSubtype();
                SetType found = new SetType(subType.getRight());
                try {
                    found = (SetType) found.unify(expected);
                } catch (UnificationException e) {
                    throw new TypeErrorException(node, expected, found);
                }
                returnType = found;
                break;
            }
            case CONCAT: {
                SequenceType found = new SequenceType(new UntypedType());
                try {
                    found = (SequenceType) found.unify(expected);
                } catch (UnificationException e) {
                    throw new TypeErrorException(node, expected, found);
                }
                found = (SequenceType) visitExprNode(expressionNodes.get(0), found);
                found = (SequenceType) visitExprNode(expressionNodes.get(1), found);
                returnType = found;
                break;
            }
            case DIRECT_PRODUCT: {
            /*
             * E ⊗ F type of result is is P(T ×(U × V)) type of E is P(T × U)
             * type of F is P(T × V)
             *
             */
                SetType found = new SetType(
                    new CoupleType(new CoupleType(new UntypedType(), new UntypedType()), new UntypedType()));
                try {
                    found = (SetType) found.unify(expected);
                } catch (UnificationException e) {
                    throw new TypeErrorException(node, expected, found);
                }
                CoupleType c1 = (CoupleType) found.getSubtype();
                CoupleType c2 = (CoupleType) c1.getRight();
                Type T = c1.getLeft();
                Type U = c2.getLeft();
                Type V = c2.getRight();
                SetType leftArg = (SetType) visitExprNode(expressionNodes.get(0), new SetType(new CoupleType(T, U)));
                T = ((CoupleType) leftArg.getSubtype()).getLeft();
                visitExprNode(expressionNodes.get(1), new SetType(new CoupleType(T, V)));
                returnType = found;
                break;
            }
            case DOMAIN_RESTRICTION:
            case DOMAIN_SUBTRACTION: {
                SetType found = new SetType(new CoupleType(new UntypedType(), new UntypedType()));
                try {
                    found = (SetType) found.unify(expected);
                } catch (UnificationException e) {
                    throw new TypeErrorException(node, expected, found);
                }
                found = (SetType) visitExprNode(expressionNodes.get(1), found);
                Type left = ((CoupleType) found.getSubtype()).getLeft();
                visitExprNode(expressionNodes.get(0), new SetType(left));
                returnType = found;
                break;
            }
            case RANGE_RESTRICTION:
            case RANGE_SUBTRATION: {
                SetType found = new SetType(new CoupleType(new UntypedType(), new UntypedType()));
                try {
                    found = (SetType) found.unify(expected);
                } catch (UnificationException e) {
                    throw new TypeErrorException(node, expected, found);
                }
                found = (SetType) visitExprNode(expressionNodes.get(0), found);
                Type right = ((CoupleType) found.getSubtype()).getLeft();
                visitExprNode(expressionNodes.get(1), new SetType(right));
                returnType = found;
                break;
            }
            case INSERT_FRONT: {
                // E -> s
                SequenceType found = new SequenceType(new UntypedType());
                try {
                    found = (SequenceType) found.unify(expected);
                } catch (UnificationException e) {
                    throw new TypeErrorException(node, expected, found);
                }
                found = (SequenceType) visitExprNode(expressionNodes.get(1), found);
                Type elemType = found.getSubtype();
                visitExprNode(expressionNodes.get(0), elemType);
                returnType = found;
                break;
            }
            case INSERT_TAIL: {
                // s <- E
                SequenceType found = new SequenceType(new UntypedType());
                try {
                    found = (SequenceType) found.unify(expected);
                } catch (UnificationException e) {
                    throw new TypeErrorException(node, expected, found);
                }
                found = (SequenceType) visitExprNode(expressionNodes.get(0), found);
                Type elemType = found.getSubtype();
                visitExprNode(expressionNodes.get(1), elemType);
                returnType = found;
                break;
            }
            case OVERWRITE_RELATION: {
                Type found = new SetType(new CoupleType(new UntypedType(), new UntypedType()));
                try {
                    found = found.unify(expected);
                } catch (UnificationException e) {
                    throw new TypeErrorException(node, expected, found);
                }
                found = visitExprNode(expressionNodes.get(0), found);
                found = visitExprNode(expressionNodes.get(1), found);
                returnType = found;
                break;
            }

            case RESTRICT_FRONT:
            case RESTRICT_TAIL: {
            /*
             * s /|\ n s \|/ n type of result is is P(Z × T) type of s is P(Z
             * ×T) type of n is INTEGER
             */
                Type found = new SetType(new CoupleType(IntegerType.getInstance(), new UntypedType()));
                try {
                    found = found.unify(expected);
                } catch (UnificationException e) {
                    throw new TypeErrorException(node, expected, found);
                }
                found = visitExprNode(expressionNodes.get(0), found);
                visitExprNode(expressionNodes.get(1), IntegerType.getInstance());
                returnType = found;
                break;
            }
            case GENERALIZED_INTER:
            case GENERALIZED_UNION: {
                Type found = new SetType(new UntypedType());
                try {
                    found = found.unify(expected);
                } catch (UnificationException e) {
                    throw new TypeErrorException(node, expected, found);
                }
                SetType s = (SetType) visitExprNode(expressionNodes.get(0), new SetType(found));
                returnType = s.getSubtype();
                break;
            }
            case EMPTY_SEQUENCE: {
                SequenceType found = new SequenceType(new UntypedType());
                try {
                    found = (SequenceType) found.unify(expected);
                } catch (UnificationException e) {
                    throw new TypeErrorException(node, expected, found);
                }
                returnType = found;
                typedNodes.add(node);
                break;
            }
            case SEQ_ENUMERATION: {
                SequenceType found = new SequenceType(new UntypedType());
                try {
                    found = (SequenceType) found.unify(expected);
                } catch (UnificationException e) {
                    throw new TypeErrorException(node, expected, found);
                }
                Type subtype = found.getSubtype();
                for (ExprNode exprNode : expressionNodes) {
                    subtype = visitExprNode(exprNode, subtype);
                }
                returnType = new SequenceType(subtype);
                break;
            }
            case LAST:
            case FIRST: {
                SequenceType seq = new SequenceType(expected);
                seq = (SequenceType) visitExprNode(expressionNodes.get(0), seq);
                returnType = seq.getSubtype();
                break;
            }
            case FRONT:
            case TAIL: {
                SequenceType found = new SequenceType(new UntypedType());
                try {
                    found = (SequenceType) found.unify(expected);
                } catch (UnificationException e) {
                    throw new TypeErrorException(node, expected, found);
                }
                found = (SequenceType) visitExprNode(expressionNodes.get(0), found);
                returnType = found;
                break;
            }
            case SEQ:
            case SEQ1:
            case ISEQ:
            case ISEQ1: {
                SetType found = new SetType(new SequenceType(new UntypedType()));
                try {
                    found = (SetType) found.unify(expected);
                } catch (UnificationException e) {
                    throw new TypeErrorException(node, expected, found);
                }
                Type type = ((SequenceType) found.getSubtype()).getSubtype();
                visitExprNode(expressionNodes.get(0), new SetType(type));
                returnType = found;
                break;
            }
            case FUNCTION_CALL: {
                // currently only for sequences
                {
                    SequenceType seqType = (SequenceType) visitExprNode(expressionNodes.get(0),
                        new SequenceType(new UntypedType()));
                    visitExprNode(expressionNodes.get(1), IntegerType.getInstance());
                    Type found = seqType.getSubtype();
                    try {
                        found = found.unify(expected);
                    } catch (UnificationException e) {
                        throw new TypeErrorException(node, expected, found);
                    }
                    returnType = found;
                    break;
                }
            }
            case CARD: {
                try {
                    IntegerType.getInstance().unify(expected);
                } catch (UnificationException e) {
                    throw new TypeErrorException(node, expected, IntegerType.getInstance());
                }
                visitExprNode(expressionNodes.get(0), new SetType(new UntypedType()));
                returnType = IntegerType.getInstance();
                break;
            }
            case EMPTY_SET: {
                SetType found = new SetType(new UntypedType());
                try {
                    found.unify(expected);
                } catch (UnificationException e) {
                    throw new TypeErrorException(node, expected, IntegerType.getInstance());
                }
                returnType = found;
                typedNodes.add(node);
                break;
            }
            default:
                throw new AssertionError();
        }
        if (returnType == null) {
            throw new AssertionError();
        } else {
            node.setType(returnType);
            return returnType;
        }
    }

    @Override
    public Type visitIdentifierExprNode(IdentifierExprNode node, Type expected) {
        try {
            Type result = node.getDeclarationNode().getType().unify(expected);
            node.setType(result);
            return result;
        } catch (UnificationException e) {
            throw new TypeErrorException(node, expected, node.getDeclarationNode().getType());
        }
    }

    @Override
    public Type visitCastPredicateExpressionNode(CastPredicateExpressionNode node, Type expected) {
        try {
            Type boolType = BoolType.getInstance();
            node.setType(boolType);
            super.visitPredicateNode(node.getPredicate(), BoolType.getInstance());
            return boolType.unify(expected);
        } catch (UnificationException e) {
            throw new TypeErrorException(node, expected, BoolType.getInstance());
        }
    }

    @Override
    public Type visitIdentifierPredicateNode(IdentifierPredicateNode node, Type expected) {
        try {
            node.setType(BoolType.getInstance());
            return node.getDeclarationNode().getType().unify(expected);
        } catch (UnificationException e) {
            throw new TypeErrorException(node, expected, node.getDeclarationNode().getType());
        }
    }

    @Override
    public Type visitNumberNode(NumberNode node, Type expected) {
        try {
            node.setType(IntegerType.getInstance());
            return IntegerType.getInstance().unify(expected);
        } catch (UnificationException e) {
            throw new TypeErrorException(node, expected, IntegerType.getInstance());
        }
    }

    @Override
    public Type visitSelectSubstitutionNode(SelectSubstitutionNode node, Type expected) {
        super.visitPredicateNode(node.getCondition(), BoolType.getInstance());
        super.visitSubstitutionNode(node.getSubstitution(), expected);
        return null;
    }

    @Override
    public Type visitSingleAssignSubstitution(SingleAssignSubstitutionNode node, Type expected) {
        Type type = visitIdentifierExprNode(node.getIdentifier(), new UntypedType());
        super.visitExprNode(node.getValue(), type);
        return null;
    }

    @Override
    public Type visitParallelSubstitutionNode(ParallelSubstitutionNode node, Type expected) {
        for (SubstitutionNode sub : node.getSubstitutions()) {
            super.visitSubstitutionNode(sub, null);
        }
        return null;
    }

    private void setTypes(List<DeclarationNode> list) {
        for (DeclarationNode decl : list) {
            decl.setType(new UntypedType());
            this.typedNodes.add(decl);
        }
    }

    @Override
    public Type visitQuantifiedExpressionNode(QuantifiedExpressionNode node, Type expected) {
        setTypes(node.getDeclarationList());
        super.visitPredicateNode(node.getPredicateNode(), BoolType.getInstance());
        switch (node.getOperator()) {
            case QUANTIFIED_INTER:
            case QUANTIFIED_UNION: {
                Type found = new SetType(new UntypedType());
                try {
                    found = found.unify(expected);
                } catch (UnificationException e) {
                    throw new TypeErrorException(node, expected, IntegerType.getInstance());
                }
                visitPredicateNode(node.getPredicateNode(), BoolType.getInstance());
                found = visitExprNode(node.getExpressionNode(), found);

                node.setType(found);
                return found;
            }
            case SET_COMPREHENSION: {
                Type left = node.getDeclarationList().get(0).getType();
                for (int i = 1; i < node.getDeclarationList().size(); i++) {
                    Type right = node.getDeclarationList().get(0).getType();
                    left = new CoupleType(left, right);
                }
                Type found = new SetType(left);
                try {
                    found = found.unify(expected);
                    node.setType(found);
                    return found;
                } catch (UnificationException e) {
                    throw new TypeErrorException(node, expected, IntegerType.getInstance());
                }
            }
            default:
                break;
        }
        throw new AssertionError("Not implemented.");
    }

    @Override
    public Type visitQuantifiedPredicateNode(QuantifiedPredicateNode node, Type expected) {
        try {
            BoolType.getInstance().unify(expected);
        } catch (UnificationException e) {
            throw new TypeErrorException(node, expected, IntegerType.getInstance());
        }
        setTypes(node.getDeclarationList());
        super.visitPredicateNode(node.getPredicateNode(), BoolType.getInstance());
        return BoolType.getInstance();
    }

    @Override
    public Type visitAnySubstitution(AnySubstitutionNode node, Type expected) {
        setTypes(node.getParameters());
        super.visitPredicateNode(node.getWherePredicate(), BoolType.getInstance());
        super.visitSubstitutionNode(node.getThenSubstitution(), null);
        return null;
    }

}
