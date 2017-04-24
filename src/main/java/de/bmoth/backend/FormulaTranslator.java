package de.bmoth.backend;

import java.util.List;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;

import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.AbstractVisitor;
import de.bmoth.parser.ast.nodes.ExprNode;
import de.bmoth.parser.ast.nodes.ExpressionOperatorNode;
import de.bmoth.parser.ast.nodes.FormulaNode;
import de.bmoth.parser.ast.nodes.FormulaNode.FormulaType;
import de.bmoth.parser.ast.nodes.IdentifierExprNode;
import de.bmoth.parser.ast.nodes.NumberNode;
import de.bmoth.parser.ast.nodes.ParallelSubstitutionNode;
import de.bmoth.parser.ast.nodes.PredicateNode;
import de.bmoth.parser.ast.nodes.PredicateOperatorNode;
import de.bmoth.parser.ast.nodes.PredicateOperatorWithExprArgsNode;
import de.bmoth.parser.ast.nodes.SelectSubstitutionNode;
import de.bmoth.parser.ast.nodes.SingleAssignSubstitution;
import de.bmoth.parser.ast.types.BoolType;
import de.bmoth.parser.ast.types.IntegerType;
import de.bmoth.parser.ast.types.Type;

/**
 * This class translates a FormulaNode of the parser to a z3 backend node.
 * 
 * The second parameter of the AbstractVisitor class is the method parameter of
 * each method which is inherited form the AbstractVisitor class. In the
 * FormulaTranslator this parameter is not needed. Hence, the placeholder class
 * Void is used. Furthermore, each call to a visitXXX method of the
 * AbstractVisitor class should use the argument null.
 **/
public class FormulaTranslator extends AbstractVisitor<Expr, Void> {

	private Context z3Context;

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
			throw new RuntimeException("Invalid translation. Expected BoolExpr but found " + constraint.getClass());
		}
		BoolExpr boolExpr = (BoolExpr) constraint;
		return boolExpr;
	}

	@Override
	public Expr visitIdentifierExprNode(IdentifierExprNode node, Void n) {
		Type type = node.getDeclarationNode().getType();
		if (type instanceof IntegerType) {
			return z3Context.mkIntConst(node.getName());
		} else if (type instanceof BoolType) {
			return z3Context.mkBoolConst(node.getName());
		} else {
			// TODO
			throw new AssertionError("Not implemented: Identifier with Type " + type.getClass());
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
			Expr left = visitExprNode(expressionNodes.get(0), null);
			Expr right = visitExprNode(expressionNodes.get(1), null);
			return z3Context.mkNot(z3Context.mkEq(left, right));
		}
		case ELEMENT_OF:
		case LESS_EQUAL:
		case LESS:
		case GREATER_EQUAL:
			break;
		default:
			break;
		}
		// TODO
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
		case MINUS: {
			ArithExpr left = (ArithExpr) visitExprNode(expressionNodes.get(0), null);
			ArithExpr right = (ArithExpr) visitExprNode(expressionNodes.get(1), null);
			return z3Context.mkSub(left, right);
		}
		case MOD: {
			IntExpr left = (IntExpr) visitExprNode(expressionNodes.get(0), null);
			IntExpr right = (IntExpr) visitExprNode(expressionNodes.get(1), null);
			return z3Context.mkMod(left, right);
		}
		case MULT:
			break;
		case DIVIDE:
			break;
		case POWER_OF:
			break;
		case INTERVAL:
			break;
		case INTEGER:
			break;
		case NATURAL1:
			break;
		case NATURAL:
			break;
		case FALSE:
			return z3Context.mkFalse();
		case TRUE:
			return z3Context.mkTrue();
		case BOOL:
			break;
		case UNION:
			break;

		default:
			break;
		}
		// TODO
		throw new AssertionError("Not implemented: " + node.getOperator());
	}

	@Override
	public Expr visitNumberNode(NumberNode node, Void n) {
		return this.z3Context.mkInt(node.getValue());
	}

	@Override
	public Expr visitPredicateOperatorNode(PredicateOperatorNode node, Void n) {
		List<PredicateNode> predicateArguments = node.getPredicateArguments();
		switch (node.getOperator()) {
		default:
		case AND: {
			BoolExpr left = (BoolExpr) visitPredicateNode(predicateArguments.get(0), null);
			BoolExpr right = (BoolExpr) visitPredicateNode(predicateArguments.get(1), null);
			return z3Context.mkAnd(left, right);
		}
		case OR:
		case IMPLIES:
		case EQUIVALENCE:
		case NOT:
		case TRUE:
		case FALSE:
			break;
		}
		// TODO
		throw new AssertionError("Not implemented: " + node.getOperator());
	}

	@Override
	public Expr visitSelectSubstitutionNode(SelectSubstitutionNode node, Void expected) {
		throw new AssertionError("Not reachable");
	}

	@Override
	public Expr visitSingleAssignSubstitution(SingleAssignSubstitution node, Void expected) {
		throw new AssertionError("Not reachable");
	}

	@Override
	public Expr visitParallelSubstitutionNode(ParallelSubstitutionNode node, Void expected) {
		throw new AssertionError("Not reachable");
	}

}
