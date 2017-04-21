package de.bmoth.backend;

import java.util.List;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;

import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.AbstractVisitor;
import de.bmoth.parser.ast.nodes.ExprNode;
import de.bmoth.parser.ast.nodes.ExpressionOperatorNode;
import de.bmoth.parser.ast.nodes.FormulaNode;
import de.bmoth.parser.ast.nodes.IdentifierExprNode;
import de.bmoth.parser.ast.nodes.NumberNode;
import de.bmoth.parser.ast.nodes.FormulaNode.FormulaType;
import de.bmoth.parser.ast.nodes.PredicateNode;
import de.bmoth.parser.ast.nodes.PredicateOperatorWithExprArgsNode;
import de.bmoth.parser.ast.types.IntegerType;
import de.bmoth.parser.ast.types.Type;

public class FormulaTranslator extends AbstractVisitor<Expr, Void> {

	Context z3Context;

	public FormulaTranslator(Context z3Context) {
		this.z3Context = z3Context;
	}

	public static BoolExpr translatePredicate(String formula, Context z3Context) {
		FormulaNode node = Parser.getFormulaAsSemanticAst(formula);
		if (node.getFormulaType() != FormulaType.PREDICATE_FORMULA) {
			throw new RuntimeException("Expected predicate.");
		}
		FormulaTranslator formulaTranslator = new FormulaTranslator(z3Context);
		Expr constraint = formulaTranslator.visitPredicateNode((PredicateNode) node.getFormula(), null);
		if (!(constraint instanceof BoolExpr)) {
			throw new RuntimeException("Invalid translation expected boolean expr but found expression.");
		}
		BoolExpr boolExpr = (BoolExpr) constraint;
		return boolExpr;
	}

	@Override
	public Expr visitIdentifierExprNode(IdentifierExprNode node, Void n) {
		Type type = node.getDeclarationNode().getType();
		if (type instanceof IntegerType) {
			return z3Context.mkIntConst("x");
		} else {
			throw new RuntimeException("Not implemented");
		}
	}

	@Override
	public Expr visitPredicateOperatorWithExprArgs(PredicateOperatorWithExprArgsNode node, Void n) {
		final List<ExprNode> expressionNodes = node.getExpressionNodes();
		switch (node.getOperator()) {
		case EQUAL: {
			Expr left = visitExprNode(expressionNodes.get(0), null);
			Expr right = visitExprNode(expressionNodes.get(1), null);
			return z3Context.mkEq(left, right);
		}
		case NOT_EQUAL: {
			break;
		}
		case ELEMENT_OF: {
			break;
		}
		case LESS_EQUAL:
		case LESS:
		case GREATER_EQUAL:
			break;
		default:
			break;
		}
		throw new AssertionError("Not implemented: " + node.getOperator());
	}

	@Override
	public Expr visitExprOperatorNode(ExpressionOperatorNode node, Void n) {
		List<ExprNode> expressionNodes = node.getExpressionNodes();
		switch (node.getOperator()) {
		case PLUS: {
			ArithExpr left = (ArithExpr) visitExprNode(expressionNodes.get(0), null);
			ArithExpr right = (ArithExpr) visitExprNode(expressionNodes.get(1), null);
			return z3Context.mkAdd(left, right);
		}
		case MINUS:
		case MOD:
		case MULT:
		case DIVIDE:
		case POWER_OF:
		case INTERVAL:
		case INTEGER:
		case NATURAL1:
		case NATURAL:
		case FALSE:
		case TRUE:
		case BOOL:
		case UNION:
			break;

		default:
			break;
		}
		throw new AssertionError("Not implemented: " + node.getOperator());
	}

	@Override
	public Expr visitNumberNode(NumberNode node, Void n) {
		return this.z3Context.mkInt(node.getValue());
	}

}
