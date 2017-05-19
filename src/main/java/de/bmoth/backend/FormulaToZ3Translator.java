package de.bmoth.backend;

import com.microsoft.z3.*;
import de.bmoth.app.PersonalPreferences;
import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.AbstractVisitor;
import de.bmoth.parser.ast.AstTransformationForZ3;
import de.bmoth.parser.ast.nodes.*;
import de.bmoth.parser.ast.nodes.ExpressionOperatorNode.ExpressionOperator;
import de.bmoth.parser.ast.nodes.FormulaNode.FormulaType;
import de.bmoth.parser.ast.types.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class translates a FormulaNode of the parser to a z3 backend node.
 **/
public class FormulaToZ3Translator {

    private Context z3Context;
    // the context which is used to create z3 objects

    private final LinkedList<BoolExpr> constraintList = new LinkedList<>();
    // A list of z3 constraints which are separately created by the translation.
    // For example, for the B keyword NATURAL an ordinary z3 identifier will be
    // created because there no corresponding keyword in z3.
    // Additionally, a constraint axiomatizing this identifier will be added to
    // this list.
    private int tempVariablesCounter = 0;
    // used to generate unique identifiers

    List<DeclarationNode> implicitDeclarations;
    FormulaNode formulaNode;

    private String createFreshTemporaryVariable() {
        this.tempVariablesCounter++;
        return "$t_" + this.tempVariablesCounter;
    }

    public List<DeclarationNode> getImplicitDeclarations() {
        return this.implicitDeclarations;
    }

    public List<Expr> getImplicitVariablesAsZ3Expression() {
        List<Expr> list = new ArrayList<>();
        for (DeclarationNode decl : implicitDeclarations) {
            Expr mkConst = this.z3Context.mkConst(decl.getName(), bTypeToZ3Sort(decl.getType()));
            list.add(mkConst);
        }
        return list;
    }

    private FormulaToZ3Translator(Context z3Context, String formula) {
        this.z3Context = z3Context;
        formulaNode = Parser.getFormulaAsSemanticAst(formula);
        this.implicitDeclarations = formulaNode.getImplicitDeclarations();
    }

    private FormulaToZ3Translator(Context z3Context) {
        this.z3Context = z3Context;
    }

    public static BoolExpr translateVariableEqualToExpr(String name, ExprNode value, Context z3Context) {
        ExprNode exprNode = AstTransformationForZ3.transformExprNode(value);
        return translateVariableEqualToExpr(name, exprNode, z3Context, new TranslationOptions());
    }

    public static BoolExpr translateVariableEqualToExpr(String name, ExprNode value, Context z3Context,
            TranslationOptions opt) {
        FormulaToZ3Translator formulaToZ3Translator = new FormulaToZ3Translator(z3Context);
        FormulaToZ3TranslatorVisitor visitor = formulaToZ3Translator.new FormulaToZ3TranslatorVisitor();
        Expr z3Value = visitor.visitExprNode(value, opt);

        Expr variable = z3Context.mkConst(name, z3Value.getSort());

        return z3Context.mkEq(variable, z3Value);
    }

    public static BoolExpr translatePredicate(String formula, Context z3Context) {
        FormulaToZ3Translator formulaToZ3Translator = new FormulaToZ3Translator(z3Context, formula);

        if (formulaToZ3Translator.formulaNode.getFormulaType() != FormulaType.PREDICATE_FORMULA) {
            throw new RuntimeException("Expected predicate.");
        }
        PredicateNode predNode = AstTransformationForZ3
                .transformSemanticNode((PredicateNode) formulaToZ3Translator.formulaNode.getFormula());
        FormulaToZ3TranslatorVisitor visitor = formulaToZ3Translator.new FormulaToZ3TranslatorVisitor();
        Expr constraint = visitor.visitPredicateNode(predNode, new TranslationOptions());
        if (!(constraint instanceof BoolExpr)) {
            throw new RuntimeException("Invalid translation. Expected BoolExpr but found " + constraint.getClass());
        }
        BoolExpr boolExpr = (BoolExpr) constraint;
        // adding all additional constraints to result
        for (BoolExpr bExpr : formulaToZ3Translator.constraintList) {
            boolExpr = z3Context.mkAnd(boolExpr, bExpr);
        }
        return boolExpr;
    }

    public static BoolExpr translatePredicate(PredicateNode pred, Context z3Context) {
        PredicateNode predNode = AstTransformationForZ3.transformSemanticNode(pred);
        return translatePredicate(predNode, z3Context, new TranslationOptions());
    }

    public static BoolExpr translatePredicate(PredicateNode pred, Context z3Context, TranslationOptions opt) {
        PredicateNode predNode = AstTransformationForZ3.transformSemanticNode(pred);
        FormulaToZ3Translator formulaToZ3Translator = new FormulaToZ3Translator(z3Context);
        FormulaToZ3TranslatorVisitor formulaToZ3TranslatorVisitor = formulaToZ3Translator.new FormulaToZ3TranslatorVisitor();

        BoolExpr boolExpr = (BoolExpr) formulaToZ3TranslatorVisitor.visitPredicateNode(predNode, opt);
        // adding all additional constraints to result
        for (BoolExpr bExpr : formulaToZ3Translator.constraintList) {
            boolExpr = z3Context.mkAnd(boolExpr, bExpr);
        }
        return boolExpr;
    }

    public static Sort bTypeToZ3Sort(Context z3Context, Type t) {
        FormulaToZ3Translator formulaToZ3Translator = new FormulaToZ3Translator(z3Context);
        return formulaToZ3Translator.bTypeToZ3Sort(t);
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
        if (t instanceof SequenceType) {
            SequenceType s = (SequenceType) t;
            Sort subSort = bTypeToZ3Sort(s.getSubtype());
            Sort int_type = z3Context.getIntSort();
            Sort[] subSorts = new Sort[2];
            subSorts[0] = z3Context.mkArraySort(int_type, subSort);
            subSorts[1] = int_type;
            TupleSort mkTupleSort = z3Context.mkTupleSort(z3Context.mkSymbol("sequence"),
                    new Symbol[] { z3Context.mkSymbol("array"), z3Context.mkSymbol("size") }, subSorts);
            return mkTupleSort;
        }

        throw new AssertionError("Missing Type Conversion: " + t.getClass());

    }

    class FormulaToZ3TranslatorVisitor extends AbstractVisitor<Expr, TranslationOptions> {
        private String addPrimes(TranslationOptions ops, String name) {
            int numOfPrimes = ops.getPrimeLevel();
            while (numOfPrimes > 0) {
                name += "'";
                numOfPrimes--;
            }
            return name;
        }

        @Override
        public Expr visitIdentifierExprNode(IdentifierExprNode node, TranslationOptions ops) {
            Type type = node.getDeclarationNode().getType();
            return z3Context.mkConst(addPrimes(ops, node.getName()), bTypeToZ3Sort(type));

        }

        @Override
        public Expr visitCastPredicateExpressionNode(CastPredicateExpressionNode node, TranslationOptions expected) {
            return visitPredicateNode(node.getPredicate(), expected);
        }

        @Override
        public Expr visitIdentifierPredicateNode(IdentifierPredicateNode node, TranslationOptions ops) {
            return z3Context.mkBoolConst(node.getName());
        }

        @Override
        public Expr visitPredicateOperatorWithExprArgs(PredicateOperatorWithExprArgsNode node, TranslationOptions ops) {
            final List<ExprNode> expressionNodes = node.getExpressionNodes();
            switch (node.getOperator()) {
            case EQUAL: {
                Expr left = visitExprNode(expressionNodes.get(0), ops);
                Expr right = visitExprNode(expressionNodes.get(1), ops);
                return z3Context.mkEq(left, right);
            }
            case NOT_EQUAL: {
                Expr left = visitExprNode(expressionNodes.get(0), ops);
                Expr right = visitExprNode(expressionNodes.get(1), ops);
                return z3Context.mkNot(z3Context.mkEq(left, right));
            }
            case ELEMENT_OF: {
                Expr left = visitExprNode(expressionNodes.get(0), ops);
                ArrayExpr right = (ArrayExpr) visitExprNode(expressionNodes.get(1), ops);
                return z3Context.mkSetMembership(left, right);
            }
            case LESS_EQUAL: {
                ArithExpr left = (ArithExpr) visitExprNode(expressionNodes.get(0), ops);
                ArithExpr right = (ArithExpr) visitExprNode(expressionNodes.get(1), ops);
                return z3Context.mkLe(left, right);
            }
            case LESS: {
                ArithExpr left = (ArithExpr) visitExprNode(expressionNodes.get(0), ops);
                ArithExpr right = (ArithExpr) visitExprNode(expressionNodes.get(1), ops);
                return z3Context.mkLt(left, right);
            }
            case GREATER_EQUAL: {
                ArithExpr left = (ArithExpr) visitExprNode(expressionNodes.get(0), ops);
                ArithExpr right = (ArithExpr) visitExprNode(expressionNodes.get(1), ops);
                return z3Context.mkGe(left, right);
            }
            case GREATER: {
                ArithExpr left = (ArithExpr) visitExprNode(expressionNodes.get(0), ops);
                ArithExpr right = (ArithExpr) visitExprNode(expressionNodes.get(1), ops);
                return z3Context.mkGt(left, right);
            }
            case NOT_BELONGING:
                Expr left = visitExprNode(expressionNodes.get(0), ops);
                ArrayExpr right = (ArrayExpr) visitExprNode(expressionNodes.get(1), ops);
                return z3Context.mkNot(z3Context.mkSetMembership(left, right));
            case INCLUSION: {
                // a <: S
                ArrayExpr arg0 = (ArrayExpr) visitExprNode(expressionNodes.get(0), ops);
                ArrayExpr arg1 = (ArrayExpr) visitExprNode(expressionNodes.get(1), ops);
                return z3Context.mkSetSubset(arg0, arg1);
            }
            case STRICT_INCLUSION: {
                // a <<: S
                ArrayExpr arg0 = (ArrayExpr) visitExprNode(expressionNodes.get(0), ops);
                ArrayExpr arg1 = (ArrayExpr) visitExprNode(expressionNodes.get(1), ops);
                return z3Context.mkAnd(z3Context.mkNot(z3Context.mkEq(arg0, arg1)), z3Context.mkSetSubset(arg0, arg1));
            }
            case NON_INCLUSION: {
                ArrayExpr arg0 = (ArrayExpr) visitExprNode(expressionNodes.get(0), ops);
                ArrayExpr arg1 = (ArrayExpr) visitExprNode(expressionNodes.get(1), ops);
                return z3Context.mkNot(z3Context.mkSetSubset(arg0, arg1));
            }
            case STRICT_NON_INCLUSION: {
                ArrayExpr arg0 = (ArrayExpr) visitExprNode(expressionNodes.get(0), ops);
                ArrayExpr arg1 = (ArrayExpr) visitExprNode(expressionNodes.get(1), ops);
                return z3Context.mkNot(z3Context.mkAnd(z3Context.mkNot(z3Context.mkEq(arg0, arg1)),
                        z3Context.mkSetSubset(arg0, arg1)));
            }
            }
            throw new AssertionError("Not implemented: " + node.getOperator());
        }

        @Override
        public Expr visitExprOperatorNode(ExpressionOperatorNode node, TranslationOptions ops) {
            List<ExprNode> expressionNodes = node.getExpressionNodes();
            switch (node.getOperator()) {
            case PLUS: {
                ArithExpr left = (ArithExpr) visitExprNode(expressionNodes.get(0), ops);
                ArithExpr right = (ArithExpr) visitExprNode(expressionNodes.get(1), ops);
                return z3Context.mkAdd(left, right);
            }
            case UNARY_MINUS: {
                return z3Context.mkUnaryMinus((ArithExpr) visitExprNode(expressionNodes.get(0), ops));
            }
            case MINUS: {
                ArithExpr left = (ArithExpr) visitExprNode(expressionNodes.get(0), ops);
                ArithExpr right = (ArithExpr) visitExprNode(expressionNodes.get(1), ops);
                return z3Context.mkSub(left, right);
            }
            case MOD: {
                IntExpr left = (IntExpr) visitExprNode(expressionNodes.get(0), ops);
                IntExpr right = (IntExpr) visitExprNode(expressionNodes.get(1), ops);
                return z3Context.mkMod(left, right);
            }
            case MULT: {
                ArithExpr left = (ArithExpr) visitExprNode(expressionNodes.get(0), ops);
                ArithExpr right = (ArithExpr) visitExprNode(expressionNodes.get(1), ops);
                return z3Context.mkMul(left, right);
            }
            case DIVIDE: {
                ArithExpr left = (ArithExpr) visitExprNode(expressionNodes.get(0), ops);
                ArithExpr right = (ArithExpr) visitExprNode(expressionNodes.get(1), ops);
                constraintList.add(z3Context.mkNot(z3Context.mkEq(right, z3Context.mkInt(0))));
                return z3Context.mkDiv(left, right);
            }
            case POWER_OF: {
                ArithExpr left = (ArithExpr) visitExprNode(expressionNodes.get(0), ops);
                ArithExpr right = (ArithExpr) visitExprNode(expressionNodes.get(1), ops);
                return z3Context.mkPower(left, right);
            }
            case INTERVAL: {
                ArithExpr left = (ArithExpr) visitExprNode(expressionNodes.get(0), ops);
                ArithExpr right = (ArithExpr) visitExprNode(expressionNodes.get(1), ops);

                ArithExpr x = (ArithExpr) z3Context.mkConst(createFreshTemporaryVariable(), z3Context.getIntSort());
                Expr T = z3Context.mkConst(createFreshTemporaryVariable(), bTypeToZ3Sort(node.getType()));

                BoolExpr leftLe = z3Context.mkLe(left, x);
                BoolExpr rightGe = z3Context.mkGe(right, x);
                BoolExpr interval = z3Context.mkAnd(leftLe, rightGe);
                BoolExpr member = z3Context.mkSetMembership(x, (ArrayExpr) T);
                BoolExpr equality = z3Context.mkEq(interval, member);

                Expr[] bound = new Expr[] { x };

                Quantifier q = z3Context.mkForall(bound, equality, 1, null, null, null, null);
                constraintList.add(q);
                return T;
            }
            case INTEGER: {
                return z3Context.mkFullSet(z3Context.mkIntSort());
            }
            case NATURAL1: {
                Type type = node.getType();// POW(INTEGER)
                // !x.(x >= 1 <=> x : NATURAL)
                Expr x = z3Context.mkConst("x", z3Context.getIntSort());
                Expr natural1 = z3Context.mkConst("NATURAL1", bTypeToZ3Sort(type));
                Expr[] bound = new Expr[] { x };
                // x >= 1
                BoolExpr a = z3Context.mkGe((ArithExpr) x, z3Context.mkInt(1));
                // x : NATURAL
                BoolExpr b = z3Context.mkSetMembership(x, (ArrayExpr) natural1);
                // a <=> b
                BoolExpr body = z3Context.mkEq(a, b);
                Quantifier q = z3Context.mkForall(bound, body, 1, null, null, null, null);
                constraintList.add(q);
                return natural1;
            }
            case NATURAL: {
                Type type = node.getType();// POW(INTEGER)
                // !x.(x >= 0 <=> x : NATURAL)
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
                constraintList.add(q);
                return natural;
            }
            case FALSE:
                return z3Context.mkFalse();
            case TRUE:
                return z3Context.mkTrue();
            case BOOL: {
                return z3Context.mkFullSet(z3Context.mkBoolSort());
            }
            case UNION: {
                ArrayExpr[] array = new ArrayExpr[expressionNodes.size()];
                for (int i = 0; i < array.length; i++) {
                    array[i] = (ArrayExpr) visitExprNode(expressionNodes.get(i), ops);
                }
                return z3Context.mkSetUnion(array);
            }
            case COUPLE: {
                CoupleType type = (CoupleType) node.getType();
                TupleSort bTypeToZ3Sort = (TupleSort) bTypeToZ3Sort(type);

                Expr left = visitExprNode(node.getExpressionNodes().get(0), ops);
                Expr right = visitExprNode(node.getExpressionNodes().get(1), ops);

                return bTypeToZ3Sort.mkDecl().apply(left, right);
            }
            case DOMAIN:
                break;
            case INTERSECTION: {
                ArrayExpr left = (ArrayExpr) visitExprNode(expressionNodes.get(0), ops);
                ArrayExpr right = (ArrayExpr) visitExprNode(expressionNodes.get(1), ops);
                return z3Context.mkSetIntersection(left, right);
            }
            case RANGE:
                break;
            case LAST: {
                Expr expr = visitExprNode(expressionNodes.get(0), ops);
                DatatypeExpr d = (DatatypeExpr) expr;
                Expr[] args = d.getArgs();
                ArrayExpr array = (ArrayExpr) args[0];
                ArithExpr size = (ArithExpr) args[1];
                // add WD constraint
                constraintList.add(z3Context.mkLe(z3Context.mkInt(1), size));
                return z3Context.mkSelect(array, size);
            }
            case FRONT: {
                Expr expr = visitExprNode(expressionNodes.get(0), ops);
                DatatypeExpr d = (DatatypeExpr) expr;
                Expr[] args = d.getArgs();
                ArrayExpr array = (ArrayExpr) args[0];
                ArithExpr size = (ArithExpr) args[1];
                constraintList.add(z3Context.mkLe(z3Context.mkInt(1), size));
                TupleSort mkTupleSort = (TupleSort) bTypeToZ3Sort(node.getType());
                return mkTupleSort.mkDecl().apply(array, z3Context.mkSub(size, z3Context.mkInt(1)));
            }
            case TAIL:
                break;
            case CONC:
                break;
            case EMPTY_SET: // this is not missing! it is equal to an empty set
                            // enumeration below
            case SET_ENUMERATION: {
                SetType type = (SetType) node.getType();
                Type subType = type.getSubtype();
                ArrayExpr z3Set = z3Context.mkEmptySet(bTypeToZ3Sort(subType));
                for (ExprNode exprNode : expressionNodes) {
                    z3Set = z3Context.mkSetAdd(z3Set, visitExprNode(exprNode, ops));
                }
                return z3Set;
            }
            case SET_SUBTRACTION: {
                ArrayExpr left = (ArrayExpr) visitExprNode(expressionNodes.get(0), ops);
                ArrayExpr right = (ArrayExpr) visitExprNode(expressionNodes.get(1), ops);
                return z3Context.mkSetDifference(left, right);
            }
            case CONCAT:
            case DIRECT_PRODUCT:
            case DOMAIN_RESTRICTION:
            case DOMAIN_SUBTRACTION:
            case GENERALIZED_INTER:
                break;
            case GENERALIZED_UNION: {
                // union(S)
                // return Res
                // !(e).(e : Res <=> #(s).(s : S & e : s)

                SetType setType = (SetType) node.getType();
                Expr S = visitExprNode(expressionNodes.get(0), ops);
                Expr res = z3Context.mkConst(createFreshTemporaryVariable(), bTypeToZ3Sort(setType));
                Expr s = z3Context.mkConst(createFreshTemporaryVariable(), bTypeToZ3Sort(setType));
                Expr e = z3Context.mkConst(createFreshTemporaryVariable(), bTypeToZ3Sort(setType.getSubtype()));

                BoolExpr eIsInRes = z3Context.mkSetMembership(e, (ArrayExpr) res);
                BoolExpr sIsInS = z3Context.mkSetMembership(s, (ArrayExpr) S);
                BoolExpr eIsIns = z3Context.mkSetMembership(e, (ArrayExpr) s);
                Quantifier exists = z3Context.mkExists(new Expr[] { s }, z3Context.mkAnd(sIsInS, eIsIns), 1, null, null,
                        null, null);
                Quantifier q = z3Context.mkForall(new Expr[] { e }, z3Context.mkEq(eIsInRes, exists), 1, null, null,
                        null, null);
                constraintList.add(q);
                return res;
            }
            case EMPTY_SEQUENCE: {
                Sort int_type = z3Context.getIntSort();
                Type type = ((SequenceType) node.getType()).getSubtype();
                Sort rangeType = bTypeToZ3Sort(type);
                ArrayExpr a = z3Context.mkArrayConst(createFreshTemporaryVariable(), int_type, rangeType);
                TupleSort mkTupleSort = (TupleSort) bTypeToZ3Sort(node.getType());
                return mkTupleSort.mkDecl().apply(a, z3Context.mkInt(expressionNodes.size()));
            }
            case SEQ_ENUMERATION: {
                Sort int_type = z3Context.getIntSort();
                Type type = ((SequenceType) node.getType()).getSubtype();
                Sort rangeType = bTypeToZ3Sort(type);
                ArrayExpr a = z3Context.mkArrayConst(createFreshTemporaryVariable(), int_type, rangeType);
                for (int i = 0; i < expressionNodes.size(); i++) {
                    int j = i + 1;
                    IntNum index = z3Context.mkInt(j);
                    Expr value = visitExprNode(expressionNodes.get(i), ops);
                    a = z3Context.mkStore(a, index, value);
                }
                TupleSort mkTupleSort = (TupleSort) bTypeToZ3Sort(node.getType());
                return mkTupleSort.mkDecl().apply(a, z3Context.mkInt(expressionNodes.size()));
            }
            case FIRST: {
                Expr expr = visitExprNode(expressionNodes.get(0), ops);
                DatatypeExpr d = (DatatypeExpr) expr;
                Expr[] args = d.getArgs();
                ArrayExpr array = (ArrayExpr) args[0];
                ArithExpr size = (ArithExpr) args[1];
                // add WD constraint
                constraintList.add(z3Context.mkLe(z3Context.mkInt(1), size));
                return z3Context.mkSelect(array, z3Context.mkInt(1));
            }
            case FUNCTION_CALL: {
                Expr expr = visitExprNode(expressionNodes.get(0), ops);
                DatatypeExpr d = (DatatypeExpr) expr;
                Expr[] args = d.getArgs();
                ArrayExpr array = (ArrayExpr) args[0];
                ArithExpr size = (ArithExpr) args[1];
                ArithExpr index = (ArithExpr) visitExprNode(expressionNodes.get(1), ops);
                // add WD constraint
                constraintList
                        .add(z3Context.mkAnd(z3Context.mkGe(index, z3Context.mkInt(1)), z3Context.mkLe(index, size)));
                return z3Context.mkSelect(array, index);
            }
            case CARD: {
                break;
            }
            case INSERT_FRONT:
            case INSERT_TAIL:
            case OVERWRITE_RELATION:
            case RANGE_RESTRICTION:
            case RANGE_SUBTRATION:
            case RESTRICT_FRONT:
            case RESTRICT_TAIL:
                break;
            case SEQ:
                break;
            case SEQ1:
                break;
            case ISEQ:
                break;
            case ISEQ1:
                break;
            case CARTESIAN_PRODUCT:{
                ArrayExpr left = (ArrayExpr) visitExprNode(expressionNodes.get(0), ops);
                ArrayExpr right = (ArrayExpr) visitExprNode(expressionNodes.get(1), ops);

                SetType t= (SetType) node.getType();
                CoupleType subType =(CoupleType) t.getSubtype();

                
                CoupleType type = (CoupleType) subType;
                TupleSort bTypeToZ3Sort = (TupleSort) bTypeToZ3Sort(type);
                
                ArithExpr x = (ArithExpr) z3Context.mkConst(createFreshTemporaryVariable(), bTypeToZ3Sort(subType.getLeft()));
                ArithExpr y = (ArithExpr) z3Context.mkConst(createFreshTemporaryVariable(), bTypeToZ3Sort(subType.getRight()));

                ArrayExpr T = (ArrayExpr) z3Context.mkConst(createFreshTemporaryVariable(), bTypeToZ3Sort(node.getType()));
                Expr C = bTypeToZ3Sort.mkDecl().apply(x, y);

                BoolExpr xInLeft = z3Context.mkSetMembership(x, left);
                BoolExpr yInRight = z3Context.mkSetMembership(y, right);
                BoolExpr cInT = z3Context.mkSetMembership(C, T);
                
                BoolExpr cartesian = z3Context.mkAnd(xInLeft, yInRight);
                BoolExpr equality = z3Context.mkEq(cartesian, cInT);
                
                Expr[] bound = new Expr[]{x,y};

                Quantifier q = z3Context.mkForall(bound, equality, 2, null, null, null, null);
                constraintList.add(q);
                return T;
            }
            case INT: {
                Type type = node.getType();// POW(INTEGER)
                int max_int = PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MAX_INT);
                int min_int = PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MIN_INT);
                // !x.((x >= MIN_INT & x <= MAX_INT) <=> x : INT)
                Expr integer = z3Context.mkConst(ExpressionOperator.INT.toString(), bTypeToZ3Sort(type));
                Expr x = z3Context.mkConst("x", z3Context.getIntSort());
                Expr[] bound = new Expr[] { x };
                // x >= MIN_INT
                BoolExpr a = z3Context.mkGe((ArithExpr) x, z3Context.mkInt(min_int));
                // x :INT
                BoolExpr b = z3Context.mkSetMembership(x, (ArrayExpr) integer);
                // x <= max_int
                BoolExpr c = z3Context.mkLe((ArithExpr) x, z3Context.mkInt(max_int));
                // a <=> b <=> c
                BoolExpr body = z3Context.mkEq(z3Context.mkAnd(a, c), b);
                Quantifier q = z3Context.mkForall(bound, body, 1, null, null, null, null);
                constraintList.add(q);
                return integer;
            }
            case MAXINT: {
                int max_int = PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MAX_INT);
                return z3Context.mkInt(max_int);
            }
            case MININT: {
                int min_int = PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MIN_INT);
                return z3Context.mkInt(min_int);
            }
            case NAT: {
                Type type = node.getType();// POW(INTEGER)
                int max_int = PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MAX_INT);
                // !x.((x >= 0 & x <= MAX_INT) <=> x : NAT)
                Expr x = z3Context.mkConst("x", z3Context.getIntSort());
                Expr nat = z3Context.mkConst(ExpressionOperator.NAT.toString(), bTypeToZ3Sort(type));
                Expr[] bound = new Expr[] { x };
                // x >= 0
                BoolExpr a = z3Context.mkGe((ArithExpr) x, z3Context.mkInt(0));
                // x : NAT
                BoolExpr b = z3Context.mkSetMembership(x, (ArrayExpr) nat);
                // x <= max_int
                BoolExpr c = z3Context.mkLe((ArithExpr) x, z3Context.mkInt(max_int));
                // a <=> b <=> c
                BoolExpr body = z3Context.mkEq(z3Context.mkAnd(a, c), b);
                Quantifier q = z3Context.mkForall(bound, body, 1, null, null, null, null);
                constraintList.add(q);
                return nat;
            }
            default:
                break;
            }
            throw new AssertionError("Not implemented: " + node.getOperator());
        }

        @Override
        public Expr visitNumberNode(NumberNode node, TranslationOptions ops) {
            return z3Context.mkInt(node.getValue());
        }

        @Override
        public Expr visitPredicateOperatorNode(PredicateOperatorNode node, TranslationOptions ops) {
            List<PredicateNode> predicateArguments = node.getPredicateArguments();
            switch (node.getOperator()) {
            case AND: {
                BoolExpr[] list = new BoolExpr[predicateArguments.size()];
                for (int i = 0; i < list.length; i++) {
                    list[i] = (BoolExpr) visitPredicateNode(predicateArguments.get(i), ops);
                }
                return z3Context.mkAnd(list);
            }
            case OR: {
                BoolExpr[] list = new BoolExpr[predicateArguments.size()];
                for (int i = 0; i < list.length; i++) {
                    list[i] = (BoolExpr) visitPredicateNode(predicateArguments.get(i), ops);
                }
                return z3Context.mkOr(list);
            }
            case IMPLIES: {
                BoolExpr left = (BoolExpr) visitPredicateNode(predicateArguments.get(0), ops);
                BoolExpr right = (BoolExpr) visitPredicateNode(predicateArguments.get(1), ops);
                return z3Context.mkImplies(left, right);
            }
            case EQUIVALENCE: {
                BoolExpr left = (BoolExpr) visitPredicateNode(predicateArguments.get(0), ops);
                BoolExpr right = (BoolExpr) visitPredicateNode(predicateArguments.get(1), ops);
                return z3Context.mkEq(left, right);
            }
            case NOT: {
                BoolExpr child = (BoolExpr) visitPredicateNode(predicateArguments.get(0), ops);
                return z3Context.mkNot(child);
            }
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
        public Expr visitSelectSubstitutionNode(SelectSubstitutionNode node, TranslationOptions opt) {
            throw new AssertionError("Not reachable");
        }

        @Override
        public Expr visitSingleAssignSubstitution(SingleAssignSubstitutionNode node, TranslationOptions opt) {
            throw new AssertionError("Not reachable");
        }

        @Override
        public Expr visitParallelSubstitutionNode(ParallelSubstitutionNode node, TranslationOptions opt) {
            throw new AssertionError("Not reachable");
        }

        @Override
        public Expr visitQuantifiedExpressionNode(QuantifiedExpressionNode node, TranslationOptions opt) {
            switch (node.getOperator()) {
            case SET_COMPREHENSION: {
                // {e| P}
                // return T
                // !(e).(e : T <=> P )
                Expr P = visitPredicateNode(node.getPredicateNode(), opt);
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
                constraintList.add(q);
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
        public Expr visitQuantifiedPredicateNode(QuantifiedPredicateNode node, TranslationOptions opt) {
            switch (node.getOperator()) {
            case EXISTENTIAL_QUANTIFICATION: {
                Expr[] identifiers = new Expr[node.getDeclarationList().size()];
                for (int i = 0; i < node.getDeclarationList().size(); i++) {
                    DeclarationNode declNode = node.getDeclarationList().get(i);
                    identifiers[i] = z3Context.mkConst(declNode.getName(), bTypeToZ3Sort(declNode.getType()));
                }
                Expr predicate = visitPredicateNode(node.getPredicateNode(), opt);
                Quantifier q = z3Context.mkExists(identifiers, predicate, identifiers.length, null, null, null, null);
                return q;
            }
            case UNIVERSAL_QUANTIFICATION:
                Expr[] identifiers = new Expr[node.getDeclarationList().size()];
                for (int i = 0; i < node.getDeclarationList().size(); i++) {
                    DeclarationNode declNode = node.getDeclarationList().get(i);
                    identifiers[i] = z3Context.mkConst(declNode.getName(), bTypeToZ3Sort(declNode.getType()));
                }
                Expr predicate = visitPredicateNode(node.getPredicateNode(), opt);
                Quantifier q = z3Context.mkForall(identifiers, predicate, identifiers.length, null, null, null, null);
                return q;
            }

            throw new AssertionError("Implement: " + node.getClass());
        }

        @Override
        public Expr visitAnySubstitution(AnySubstitutionNode node, TranslationOptions opt) {
            throw new AssertionError("Not reachable");
        }
    }

}
