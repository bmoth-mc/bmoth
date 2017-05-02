package de.bmoth.backend;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.ArrayExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Quantifier;
import com.microsoft.z3.Sort;
import com.microsoft.z3.Symbol;
import com.microsoft.z3.TupleSort;

import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.AbstractVisitor;
import de.bmoth.parser.ast.nodes.DeclarationNode;
import de.bmoth.parser.ast.nodes.ExprNode;
import de.bmoth.parser.ast.nodes.ExpressionOperatorNode;
import de.bmoth.parser.ast.nodes.ExpressionOperatorNode.ExpressionOperator;
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
public class FormulaToZ3Translator extends AbstractVisitor<Expr, Void> {

    private Context z3Context;
    // the context which is used to create z3 objects
    private final LinkedList<BoolExpr> constraintList = new LinkedList<>();
    // A list of z3 constraints which are separately created by the translation.
    // For example, for the B keyword NATURAL an ordinary z3 identifier will be
    // created because there no corresponding keyword in z3.
    // Additionally, a constraint axiomatizing this identifier will be added to
    // this list.
    private int tempoVariablesCounter = 0;
    // used to generate unique identifiers

    public FormulaToZ3Translator(Context z3Context) {
        this.z3Context = z3Context;
    }

    private String createFreshTemporaryVariable() {
        this.tempoVariablesCounter++;
        return "$t_" + this.tempoVariablesCounter;
    }

    public static BoolExpr translatePredicate(String formula, Context z3Context) {
        FormulaNode node = Parser.getFormulaAsSemanticAst(formula);
        if (node.getFormulaType() != FormulaType.PREDICATE_FORMULA) {
            throw new RuntimeException("Expected predicate.");
        }
        FormulaToZ3Translator formulaTranslator = new FormulaToZ3Translator(z3Context);
        Expr constraint = formulaTranslator.visitPredicateNode((PredicateNode) node.getFormula(), null);
        if (!(constraint instanceof BoolExpr)) {
            throw new RuntimeException("Invalid translation. Expected BoolExpr but found " + constraint.getClass());
        }

        BoolExpr boolExpr = (BoolExpr) constraint;
        // adding all additional constraints to result
        for (BoolExpr bExpr : formulaTranslator.constraintList) {
            boolExpr = z3Context.mkAnd(boolExpr, bExpr);
        }
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
        case ELEMENT_OF: {
            Expr left = visitExprNode(expressionNodes.get(0), null);
            ArrayExpr right = (ArrayExpr) visitExprNode(expressionNodes.get(1), null);
            return z3Context.mkSetMembership(left, right);
        }
        case LESS_EQUAL:
            break;
        case LESS: {
            ArithExpr left = (ArithExpr) visitExprNode(expressionNodes.get(0), null);
            ArithExpr right = (ArithExpr) visitExprNode(expressionNodes.get(1), null);
            return z3Context.mkLt(left, right);
        }
        case GREATER_EQUAL:
            break;
        case GREATER: {
            ArithExpr left = (ArithExpr) visitExprNode(expressionNodes.get(0), null);
            ArithExpr right = (ArithExpr) visitExprNode(expressionNodes.get(1), null);
            return z3Context.mkGt(left, right);
        }
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
        case UNARY_MINUS: {
            return z3Context.mkUnaryMinus((ArithExpr) visitExprNode(expressionNodes.get(0), null));
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
        case NATURAL: {
            Type type = node.getType();// POW(INTEGER)
            // !x.(x : INTEGER & x >= 0 <=> x : NATURAL)
            Expr x = z3Context.mkConst("x", z3Context.getIntSort());
            Expr natural = z3Context.mkConst(ExpressionOperator.NATURAL.toString(), bTypeToZ3Sort(type));
            Expr[] bound = new Expr[] { x };
            // x >= 0
            BoolExpr a = z3Context.mkGe((ArithExpr) x, z3Context.mkInt(0));
            // x : NATURAL
            BoolExpr b = z3Context.mkSetMembership(x, (ArrayExpr) natural);
            // a <=> b
            BoolExpr body = z3Context.mkEq(a, b);
            Quantifier q = z3Context.mkForall(bound, body, 1, null, null, null, null);
            this.constraintList.add(q);
            return natural;
        }
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
        case CONCAT:
        case DIRECT_PRODUCT:
        case DOMAIN_RESTRICTION:
        case DOMAIN_SUBSTRACTION:
        case GENERALIZED_INTER:
            break;
        case GENERALIZED_UNION: {
            // union(S)
            // return Res
            // !(r).(r : Res <=> #(s).(s : S & r : s)
            // !(s).(s : S <=> s <: Res)
            Expr S = visitExprNode(expressionNodes.get(0), null);
            Expr res = z3Context.mkConst(createFreshTemporaryVariable(), bTypeToZ3Sort(node.getType()));
            Expr s = z3Context.mkConst(createFreshTemporaryVariable(), bTypeToZ3Sort(node.getType()));
            Expr[] bound = new Expr[] { s };
            BoolExpr a = z3Context.mkSetMembership(s, (ArrayExpr) S);
            BoolExpr b = z3Context.mkSetSubset((ArrayExpr) s, (ArrayExpr) res);
            // a <=> b
            BoolExpr body = z3Context.mkEq(a, b);
            Quantifier q = z3Context.mkForall(bound, body, 1, null, null, null, null);
            this.constraintList.add(q);
            return res;
        }
        case INSERT_FRONT:
        case INSERT_TAIL:
        case OVERWRITE_RELATION:
        case RANGE_RESTRICTION:
        case RANGE_SUBSTRATION:
        case RESTRICT_FRONT:
        case RESTRICT_TAIL:
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
        case EQUIVALENCE: {
            BoolExpr left = (BoolExpr) visitPredicateNode(predicateArguments.get(0), null);
            BoolExpr right = (BoolExpr) visitPredicateNode(predicateArguments.get(1), null);
            return z3Context.mkEq(left, right);
        }
        case NOT:
        case TRUE:
            return z3Context.mkTrue();
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
        switch (node.getOperator()) {
        case SET_COMPREHENSION: {
            // {e| P}
            // return T
            // !(e).(e : T <=> P )
            Expr P = visitPredicateNode(node.getPredicateNode(), null);
            Expr T = z3Context.mkConst(createFreshTemporaryVariable(), bTypeToZ3Sort(node.getType()));

            Expr[] array = new Expr[node.getDeclarationList().size()];
            for (int i = 0; i < array.length; i++) {
                DeclarationNode decl = node.getDeclarationList().get(i);
                Expr e = z3Context.mkConst(decl.getName(), bTypeToZ3Sort(decl.getType()));
                array[i] = e;
            }
            Expr tuple = null;
            if (array.length > 1) {
                TupleSort tupleSort = (TupleSort) bTypeToZ3Sort(((SetType) node.getType()).getSubtype());
                tuple = tupleSort.mkDecl().apply(array);
            } else {
                tuple = array[0];
            }

            Expr[] bound = array;
            BoolExpr a = z3Context.mkSetMembership(tuple, (ArrayExpr) T);
            // a <=> P
            BoolExpr body = z3Context.mkEq(a, P);
            Quantifier q = z3Context.mkForall(bound, body, array.length, null, null, null, null);
            this.constraintList.add(q);
            return T;
        }
        case QUANTIFIED_INTER:
        case QUANTIFIED_UNION:
            break;
        default:
            break;
        }
        throw new AssertionError("Implement: " + node.getClass());
    }

    @Override
    public Expr visitQuantifiedPredicateNode(QuantifiedPredicateNode node, Void expected) {
        throw new AssertionError("Implement: " + node.getClass());
    }

}
