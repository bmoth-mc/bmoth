package de.bmoth.parser.ast;

import java.util.List;

import de.bmoth.exceptions.TypeErrorException;
import de.bmoth.exceptions.UnificationException;
import de.bmoth.parser.ast.nodes.*;
import de.bmoth.parser.ast.types.*;

public class TypeChecker extends AbstractVisitor<Type, Type> {

	public TypeChecker(MachineNode machineNode) {
		for (DeclarationNode con : machineNode.getConstants()) {
			con.setType(new UntypedType());
		}
		if (machineNode.getProperties() != null) {
			super.visitPredicateNode(machineNode.getProperties(), BoolType.getInstance());
		}

		for (DeclarationNode var : machineNode.getVariables()) {
			var.setType(new UntypedType());
		}
		if (machineNode.getInvariant() != null) {
			super.visitPredicateNode(machineNode.getInvariant(), BoolType.getInstance());
		}

		if (machineNode.getInitialisation() != null) {
			super.visitSubstitutionNode(machineNode.getInitialisation(), null);
		}

		for (OperationNode op : machineNode.getOperations()) {
			super.visitSubstitutionNode(op.getSubstitution(), null);
		}

	}

	@Override
	public Type visitPredicateOperatorNode(PredicateOperatorNode node, Type expected) {
		try {
			BoolType.getInstance().unify(expected);
		} catch (UnificationException e) {
			throw new TypeErrorException();
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
			throw new TypeErrorException();
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
				throw new TypeErrorException();
			}
			for (ExprNode exprNode : expressionNodes) {
				visitExprNode(exprNode, IntegerType.getInstance());
			}
			return IntegerType.getInstance();
		}
		case INTERVAL: {
			Type found = new SetType(IntegerType.getInstance());
			try {
				found = found.unify(expected);
			} catch (UnificationException e) {
				throw new TypeErrorException();
			}
			visitExprNode(expressionNodes.get(0), IntegerType.getInstance());
			visitExprNode(expressionNodes.get(1), IntegerType.getInstance());
			return found;
		}
		case INTEGER:
		case NATURAL1:
		case NATURAL: {
			Type type = new SetType(IntegerType.getInstance());
			try {
				type = type.unify(expected);
			} catch (UnificationException e) {
				throw new TypeErrorException();
			}
			return type;
		}
		case FALSE:
		case TRUE: {
			try {
				BoolType.getInstance().unify(expected);
			} catch (UnificationException e) {
				throw new TypeErrorException();
			}
			return BoolType.getInstance();
		}
		case BOOL: {
			SetType found = new SetType(BoolType.getInstance());
			try {
				found.unify(expected);
			} catch (UnificationException e) {
				throw new TypeErrorException();
			}
			return found;
		}
		case UNION: {
			Type type = new SetType(new UntypedType());
			try {
				type = type.unify(expected);
			} catch (UnificationException e) {
				throw new TypeErrorException();
			}
			type = visitExprNode(expressionNodes.get(0), type);
			type = visitExprNode(expressionNodes.get(1), type);
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
			throw new TypeErrorException();
		}
	}

	@Override
	public Type visitNumberNode(NumberNode node, Type expected) {
		try {
			return IntegerType.getInstance().unify(expected);
		} catch (UnificationException e) {
			throw new TypeErrorException();
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
		visitExprNode(node.getValue(), type);
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
