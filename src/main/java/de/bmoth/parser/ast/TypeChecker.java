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
		super.visitPredicateNode(machineNode.getProperties(), BooleanType.getInstance());

		for (DeclarationNode var : machineNode.getVariables()) {
			var.setType(new UntypedType());
		}
		super.visitPredicateNode(machineNode.getInvariant(), BooleanType.getInstance());
	}

	@Override
	public Type visitPredicateOperatorNode(PredicateOperatorNode node, Type expected) {

		return BooleanType.getInstance();
	}

	@Override
	public Type visitPredicateOperatorWithExprArgs(PredicateOperatorWithExprArgs node, Type expected) {
		try {
			BooleanType.getInstance().unify(expected);
		} catch (UnificationException e) {
			throw new TypeErrorException();
		}
		switch (node.getOperator()) {
		case EQUAL:
		case UNEQUAL: {
			List<ExprNode> expressionNodes = node.getExpressionNodes();
			Type type = visitExprNode(expressionNodes.get(0), new UntypedType());
			visitExprNode(expressionNodes.get(0), type);
			break;
		}
		default:
			break;
		}
		return BooleanType.getInstance();
	}

	@Override
	public Type visitExprOperatorNode(ExprOperatorNode node, Type expected) {
		List<ExprNode> expressionNodes = node.getExpressionNodes();
		switch (node.getExpressionOperator()) {
		case PLUS:
		case MINUS: {
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

}
