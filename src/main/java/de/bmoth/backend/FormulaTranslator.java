package de.bmoth.backend;

import java.util.List;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.ArrayExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Sort;
import com.microsoft.z3.Symbol;
import com.microsoft.z3.TupleSort;

import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.AbstractVisitor;
import de.bmoth.parser.ast.nodes.ExprNode;
import de.bmoth.parser.ast.nodes.ExpressionOperatorNode;
import de.bmoth.parser.ast.nodes.FormulaNode;
import de.bmoth.parser.ast.nodes.FormulaNode.FormulaType;
import de.bmoth.parser.ast.nodes.IdentifierExprNode;
import de.bmoth.parser.ast.nodes.IdentifierPredicateNode;
import de.bmoth.parser.ast.nodes.NumberNode;
import de.bmoth.parser.ast.nodes.ParallelSubstitutionNode;
import de.bmoth.parser.ast.nodes.PredicateNode;
import de.bmoth.parser.ast.nodes.PredicateOperatorNode;
import de.bmoth.parser.ast.nodes.PredicateOperatorWithExprArgsNode;
import de.bmoth.parser.ast.nodes.QuantifiedExpressionNode;
import de.bmoth.parser.ast.nodes.QuantifiedPredicateNode;
import de.bmoth.parser.ast.nodes.SelectSubstitutionNode;
import de.bmoth.parser.ast.nodes.SingleAssignSubstitution;
import de.bmoth.parser.ast.types.BoolType;
import de.bmoth.parser.ast.types.CoupleType;
import de.bmoth.parser.ast.types.IntegerType;
import de.bmoth.parser.ast.types.SetType;
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

    public static Expr translateExpression(String formula, Context z3Context) {
        FormulaNode node = Parser.getFormulaAsSemanticAst(formula);
        if (node.getFormulaType() != FormulaType.EXPRESSION_FORMULA) {
            throw new RuntimeException("Expected expression.");
        }
        FormulaTranslator formulaTranslator = new FormulaTranslator(z3Context);
        Expr expr = formulaTranslator.visitExprNode((ExprNode) node.getFormula(), null);
        return expr;
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
        return z3Context.mkConst(node.getName(), bTypeToZ3Sort(type));

    }

    @Override
    public Expr visitIdentifierPredicateNode(IdentifierPredicateNode node, Void n) {
        return z3Context.mkBoolConst(node.getName());
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
            break;
        case LESS: {
            ArithExpr left = (ArithExpr) visitExprNode(expressionNodes.get(0), null);
            ArithExpr right = (ArithExpr) visitExprNode(expressionNodes.get(1), null);
            return z3Context.mkLt(left, right);
        }
        case GREATER_EQUAL:
            break;
        case GREATER:
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
        case MULT: {
            ArithExpr left = (ArithExpr) visitExprNode(expressionNodes.get(0), null);
            ArithExpr right = (ArithExpr) visitExprNode(expressionNodes.get(1), null);
            return z3Context.mkMul(left, right);
        }
        case DIVIDE: {
            ArithExpr left = (ArithExpr) visitExprNode(expressionNodes.get(0), null);
            ArithExpr right = (ArithExpr) visitExprNode(expressionNodes.get(1), null);
            return z3Context.mkDiv(left, right);
        }
        case POWER_OF: {
            ArithExpr left = (ArithExpr) visitExprNode(expressionNodes.get(0), null);
            ArithExpr right = (ArithExpr) visitExprNode(expressionNodes.get(1), null);
            return z3Context.mkPower(left, right);
        }
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
        case COUPLE: {
            CoupleType type = (CoupleType) node.getType();
            TupleSort bTypeToZ3Sort = (TupleSort) bTypeToZ3Sort(type);

            Expr left = visitExprNode(node.getExpressionNodes().get(0), null);
            Expr right = visitExprNode(node.getExpressionNodes().get(1), null);

            return bTypeToZ3Sort.mkDecl().apply(left, right);
        }
        case DOMAIN:
            break;
        case INTERSECTION:
            break;
        case RANGE:
            break;
        case SET_ENUMERATION: {
            SetType type = (SetType) node.getType();
            Type subType = type.getSubtype();
            ArrayExpr z3Set = z3Context.mkEmptySet(bTypeToZ3Sort(subType));
            for (ExprNode exprNode : expressionNodes) {
                z3Set = z3Context.mkSetAdd(z3Set, visitExprNode(exprNode, null));
            }
            return z3Set;
        }
        case SET_SUBTRACTION:
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
        case AND: {
            BoolExpr left = (BoolExpr) visitPredicateNode(predicateArguments.get(0), null);
            BoolExpr right = (BoolExpr) visitPredicateNode(predicateArguments.get(1), null);
            return z3Context.mkAnd(left, right);
        }
        case OR: {
            BoolExpr left = (BoolExpr) visitPredicateNode(predicateArguments.get(0), null);
            BoolExpr right = (BoolExpr) visitPredicateNode(predicateArguments.get(1), null);
            return z3Context.mkOr(left, right);
        }
        case IMPLIES: {
            BoolExpr left = (BoolExpr) visitPredicateNode(predicateArguments.get(0), null);
            BoolExpr right = (BoolExpr) visitPredicateNode(predicateArguments.get(1), null);
            return z3Context.mkImplies(left, right);
        }
        case EQUIVALENCE:
        case NOT:
        case TRUE:
            break;
        case FALSE:
            return z3Context.mkFalse();
        default:
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

    public Sort bTypeToZ3Sort(Type t) {
        if (t instanceof IntegerType) {
            return z3Context.getIntSort();
        }
        if (t instanceof BoolType) {
            return z3Context.getBoolSort();
        }
        if (t instanceof SetType) {
            SetType s = (SetType) t;
            Sort subSort = bTypeToZ3Sort(s.getSubtype());
            return z3Context.mkSetSort(subSort);
        }
        if (t instanceof CoupleType) {
            CoupleType c = (CoupleType) t;
            Sort[] subSorts = new Sort[2];
            subSorts[0] = bTypeToZ3Sort(c.getLeft());
            subSorts[1] = bTypeToZ3Sort(c.getRight());
            return z3Context.mkTupleSort(z3Context.mkSymbol("couple"),
                    new Symbol[] { z3Context.mkSymbol("left"), z3Context.mkSymbol("right") }, subSorts);
        }

        throw new AssertionError("Missing Type Conversion: " + t.getClass());

    }

    @Override
    public Expr visitQuantifiedExpressionNode(QuantifiedExpressionNode node, Void expected) {
        throw new AssertionError("Implement: " + node.getClass());
    }

    @Override
    public Expr visitQuantifiedPredicateNode(QuantifiedPredicateNode node, Void expected) {
        throw new AssertionError("Implement: " + node.getClass());
    }

}
