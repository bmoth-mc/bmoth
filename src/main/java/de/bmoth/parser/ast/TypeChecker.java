package de.bmoth.parser.ast;

import java.util.List;

import de.bmoth.exceptions.TypeErrorException;
import de.bmoth.exceptions.UnificationException;
import de.bmoth.parser.ast.nodes.*;
import de.bmoth.parser.ast.types.*;

public class TypeChecker extends AbstractVisitor<Type, Type> {

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
                throw new TypeErrorException(con, "Can not infer the type of constant " + con.getName());
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
                throw new TypeErrorException(var, "Can not infer the type of variable " + var.getName());
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

        /*
         * Currently there are no local variables, e.g. quantified variables or
         * VAR variables. Hence, a check is missing that all local variables
         * have type.
         */
    }

    public TypeChecker(FormulaNode formulaNode) {
        for (DeclarationNode node : formulaNode.getImplicitDeclarations()) {
            UntypedType untypedType = new UntypedType();
            node.setType(untypedType);
        }
        Node formula = formulaNode.getFormula();
        if (formula instanceof PredicateNode) {
            super.visitPredicateNode((PredicateNode) formula, BoolType.getInstance());
        } else {
            // expression formula
            Type type = super.visitExprNode((ExprNode) formula, new UntypedType());
            if (type.isUntyped()) {
                throw new TypeErrorException(formula, "Can not infer type of formula");
            }
        }

        // check that all local variables have a type, otherwise throw an
        // exception
        for (DeclarationNode node : formulaNode.getImplicitDeclarations()) {
            if (node.getType().isUntyped()) {
                throw new TypeErrorException(node, "Can not infer the type of local variable: " + node.getName());
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
        case MINUS:
        case UNARY_MINUS:
        case MOD:
        case MULT:
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
        case INTEGER:
        case NATURAL1:
        case NATURAL: {
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
            SetType found = new SetType(new CoupleType(IntegerType.getInstance(), new UntypedType()));
            try {
                found = (SetType) found.unify(expected);
            } catch (UnificationException e) {
                throw new TypeErrorException(node, expected, found);
            }
            found = (SetType) visitExprNode(expressionNodes.get(0), found);
            found = (SetType) visitExprNode(expressionNodes.get(1), found);
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
        case DOMAIN_SUBSTRACTION: {
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
        case RANGE_SUBSTRATION: {
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
            /*
             * s <- E type of result is is P(Z × T) type of s is P(Z × T) type
             * of E is T
             */
            SetType found = new SetType(new CoupleType(IntegerType.getInstance(), new UntypedType()));
            try {
                found = (SetType) found.unify(expected);
            } catch (UnificationException e) {
                throw new TypeErrorException(node, expected, found);
            }
            found = (SetType) visitExprNode(expressionNodes.get(0), found);
            Type right = ((CoupleType) found.getSubtype()).getLeft();
            visitExprNode(expressionNodes.get(1), right);
            returnType = found;
            break;
        }
        case INSERT_TAIL: {
            /*
             * s <- E type of result is is P(Z × T) type of s is P(Z × T) type
             * of E is T
             */
            SetType found = new SetType(new CoupleType(IntegerType.getInstance(), new UntypedType()));
            try {
                found = (SetType) found.unify(expected);
            } catch (UnificationException e) {
                throw new TypeErrorException(node, expected, found);
            }
            found = (SetType) visitExprNode(expressionNodes.get(1), found);
            Type right = ((CoupleType) found.getSubtype()).getRight();
            visitExprNode(expressionNodes.get(0), right);
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
        case GENERALIZED_UNION:{
            Type found = new SetType(new UntypedType());
            try {
                found = found.unify(expected);
            } catch (UnificationException e) {
                throw new TypeErrorException(node, expected, found);
            }
            SetType s =(SetType) visitExprNode(expressionNodes.get(0), new SetType(found));
            returnType = s.getSubtype();
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
    public Type visitSingleAssignSubstitution(SingleAssignSubstitution node, Type expected) {
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

    @Override
    public Type visitQuantifiedExpressionNode(QuantifiedExpressionNode node, Type expected) {
        for (DeclarationNode decl : node.getDeclarationList()) {
            decl.setType(new UntypedType());
        }
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
        for (DeclarationNode decl : node.getDeclarationList()) {
            decl.setType(new UntypedType());
        }
        super.visitPredicateNode(node.getPredicateNode(), BoolType.getInstance());
        return BoolType.getInstance();
    }

}
