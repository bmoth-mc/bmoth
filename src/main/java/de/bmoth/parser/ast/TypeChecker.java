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
			node.setType(new UntypedType());
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
				throw new TypeErrorException(node, "Can not infer the type of local variable + " + node.getName());
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
		default:
			break;
		}
		return BoolType.getInstance();
	}

	@Override
	public Type visitExprOperatorNode(ExpressionOperatorNode node, Type expected) {
		List<ExprNode> expressionNodes = node.getExpressionNodes();
		switch (node.getOperator()) {
		case PLUS:
		case MINUS:
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
			node.setType(IntegerType.getInstance());
			return IntegerType.getInstance();
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
			node.setType(found);
			return found;
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
			node.setType(type);
			return type;
		}
		case FALSE:
		case TRUE: {
			try {
				BoolType.getInstance().unify(expected);
			} catch (UnificationException e) {
				throw new TypeErrorException(node, expected, BoolType.getInstance());
			}
			node.setType(BoolType.getInstance());
			return BoolType.getInstance();
		}
		case BOOL: {
			SetType found = new SetType(BoolType.getInstance());
			try {
				found.unify(expected);
			} catch (UnificationException e) {
				throw new TypeErrorException(node, expected, found);
			}
			node.setType(found);
			return found;
		}
		case UNION: {
			Type type = new SetType(new UntypedType());
			try {
				type = type.unify(expected);
			} catch (UnificationException e) {
				throw new TypeErrorException(node, expected, type);
			}
			type = visitExprNode(expressionNodes.get(0), type);
			type = visitExprNode(expressionNodes.get(1), type);
			node.setType(type);
			return type;
		}

		default:
			break;
		}
		throw new AssertionError();
	}

	@Override
	public Type visitIdentifierExprNode(IdentifierExprNode node, Type expected) {
		try {
			return node.getDeclarationNode().getType().unify(expected);
		} catch (UnificationException e) {
			throw new TypeErrorException(node, expected, node.getDeclarationNode().getType());
		}
	}

	@Override
	public Type visitIdentifierPredicateNode(IdentifierPredicateNode node, Type expected) {
		try {
			return node.getDeclarationNode().getType().unify(expected);
		} catch (UnificationException e) {
			throw new TypeErrorException(node, expected, node.getDeclarationNode().getType());
		}
	}

	@Override
	public Type visitNumberNode(NumberNode node, Type expected) {
		try {
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

}
