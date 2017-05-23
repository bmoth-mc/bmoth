package de.bmoth.backend.z3;

import com.microsoft.z3.*;
import de.bmoth.app.PersonalPreferences;
import de.bmoth.backend.TranslationOptions;
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
import java.util.stream.Collectors;

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

    private FuncDecl pow = null;

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
                new Symbol[]{z3Context.mkSymbol("left"), z3Context.mkSymbol("right")}, subSorts);
        }
        if (t instanceof SequenceType) {
            SequenceType s = (SequenceType) t;
            Sort subSort = bTypeToZ3Sort(s.getSubtype());
            Sort intType = z3Context.getIntSort();
            Sort[] subSorts = new Sort[2];
            subSorts[0] = z3Context.mkArraySort(intType, subSort);
            subSorts[1] = intType;
            return z3Context.mkTupleSort(z3Context.mkSymbol("sequence"),
                new Symbol[]{z3Context.mkSymbol("array"), z3Context.mkSymbol("size")}, subSorts);
        }

        throw new AssertionError("Missing Type Conversion: " + t.getClass());

    }

    class FormulaToZ3TranslatorVisitor extends AbstractVisitor<Expr, TranslationOptions> {
        private String addPrimes(TranslationOptions ops, String name) {
            int numOfPrimes = ops.getPrimeLevel();
            StringBuilder nameBuilder = new StringBuilder(name);
            while (numOfPrimes > 0) {
                nameBuilder.append("'");
                numOfPrimes--;
            }
            return nameBuilder.toString();
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
            final List<Expr> arguments = node.getExpressionNodes().stream().map(it -> visitExprNode(it, ops)).collect(Collectors.toList());
            switch (node.getOperator()) {
                case EQUAL:
                    return z3Context.mkEq(arguments.get(0), arguments.get(1));
                case NOT_EQUAL:
                    return z3Context.mkNot(z3Context.mkEq(arguments.get(0), arguments.get(1)));
                case ELEMENT_OF:
                    return z3Context.mkSetMembership(arguments.get(0), (ArrayExpr) arguments.get(1));
                case LESS_EQUAL:
                    return z3Context.mkLe((ArithExpr) arguments.get(0), (ArithExpr) arguments.get(1));
                case LESS:
                    return z3Context.mkLt((ArithExpr) arguments.get(0), (ArithExpr) arguments.get(1));
                case GREATER_EQUAL:
                    return z3Context.mkGe((ArithExpr) arguments.get(0), (ArithExpr) arguments.get(1));
                case GREATER:
                    return z3Context.mkGt((ArithExpr) arguments.get(0), (ArithExpr) arguments.get(1));
                case NOT_BELONGING:
                    return z3Context.mkNot(z3Context.mkSetMembership(arguments.get(0), (ArrayExpr) arguments.get(1)));
                case INCLUSION:
                    // a <: S
                    return z3Context.mkSetSubset((ArrayExpr) arguments.get(0), (ArrayExpr) arguments.get(1));
                case STRICT_INCLUSION:
                    // a <<: S
                    return z3Context.mkAnd(z3Context.mkNot(z3Context.mkEq(arguments.get(0), arguments.get(1))), z3Context.mkSetSubset((ArrayExpr) arguments.get(0), (ArrayExpr) arguments.get(1)));
                case NON_INCLUSION:
                    return z3Context.mkNot(z3Context.mkSetSubset((ArrayExpr) arguments.get(0), (ArrayExpr) arguments.get(1)));
                case STRICT_NON_INCLUSION:
                    return z3Context.mkNot(z3Context.mkAnd(z3Context.mkNot(z3Context.mkEq(arguments.get(0), arguments.get(1))),
                        z3Context.mkSetSubset((ArrayExpr) arguments.get(0), (ArrayExpr) arguments.get(1))));
                default:
                    throw new AssertionError("Not implemented: " + node.getOperator());
            }
        }

        @Override
        public Expr visitExprOperatorNode(ExpressionOperatorNode node, TranslationOptions ops) {
            final List<Expr> arguments = node.getExpressionNodes().stream().map(it -> visitExprNode(it, ops)).collect(Collectors.toList());
            switch (node.getOperator()) {
                case PLUS:
                    return z3Context.mkAdd((ArithExpr) arguments.get(0), (ArithExpr) arguments.get(1));
                case UNARY_MINUS:
                    return z3Context.mkUnaryMinus((ArithExpr) arguments.get(0));
                case MINUS:
                    return z3Context.mkSub((ArithExpr) arguments.get(0), (ArithExpr) arguments.get(1));
                case MOD:
                    return z3Context.mkMod((IntExpr) arguments.get(0), (IntExpr) arguments.get(1));
                case MULT:
                    return z3Context.mkMul((ArithExpr) arguments.get(0), (ArithExpr) arguments.get(1));
                case DIVIDE:
                    constraintList.add(z3Context.mkNot(z3Context.mkEq(arguments.get(1), z3Context.mkInt(0))));
                    return z3Context.mkDiv((ArithExpr) arguments.get(0), (ArithExpr) arguments.get(1));
                case POWER_OF:
                    if (pow == null) {
                        pow = initPowerOf();
                    }
                    return pow.apply(arguments.get(0), arguments.get(1));
                case INTERVAL: {
                    ArithExpr x = (ArithExpr) z3Context.mkConst(createFreshTemporaryVariable(), z3Context.getIntSort());
                    Expr T = z3Context.mkConst(createFreshTemporaryVariable(), bTypeToZ3Sort(node.getType()));

                    BoolExpr leftLe = z3Context.mkLe((ArithExpr) arguments.get(0), x);
                    BoolExpr rightGe = z3Context.mkGe((ArithExpr) arguments.get(1), x);
                    BoolExpr interval = z3Context.mkAnd(leftLe, rightGe);
                    BoolExpr member = z3Context.mkSetMembership(x, (ArrayExpr) T);
                    BoolExpr equality = z3Context.mkEq(interval, member);

                    Expr[] bound = new Expr[]{x};

                    Quantifier q = z3Context.mkForall(bound, equality, 1, null, null, null, null);
                    constraintList.add(q);
                    return T;
                }
                case INTEGER:
                    return z3Context.mkFullSet(z3Context.mkIntSort());
                case NATURAL1: {
                    Type type = node.getType();// POW(INTEGER)
                    // !x.(x >= 1 <=> x : NATURAL)
                    Expr x = z3Context.mkConst("x", z3Context.getIntSort());
                    Expr natural1 = z3Context.mkConst("NATURAL1", bTypeToZ3Sort(type));
                    Expr[] bound = new Expr[]{x};
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
                    Expr[] bound = new Expr[]{x};
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
                case BOOL:
                    return z3Context.mkFullSet(z3Context.mkBoolSort());
                case UNION:
                    return z3Context.mkSetUnion(arguments.stream().map(it -> (ArrayExpr) it).toArray(ArrayExpr[]::new));
                case COUPLE: {
                    CoupleType type = (CoupleType) node.getType();
                    TupleSort bTypeToZ3Sort = (TupleSort) bTypeToZ3Sort(type);

                    return bTypeToZ3Sort.mkDecl().apply(arguments.get(0), arguments.get(1));
                }
                case INTERSECTION:
                    return z3Context.mkSetIntersection((ArrayExpr) arguments.get(0), (ArrayExpr) arguments.get(1));
                case DOMAIN: {
                    ArrayExpr argument = (ArrayExpr) visitExprNode(node.getExpressionNodes().get(0), ops);

                    SetType setOfTuples = (SetType) node.getExpressionNodes().get(0).getType();
                    CoupleType relationType = (CoupleType) setOfTuples.getSubtype();
                    TupleSort relationSort = (TupleSort) bTypeToZ3Sort(relationType);

                    Expr dom = z3Context.mkConst(createFreshTemporaryVariable(), bTypeToZ3Sort(new SetType(relationType.getRight())));
                    Expr domMember = z3Context.mkConst(createFreshTemporaryVariable(), bTypeToZ3Sort(relationType.getRight()));
                    Expr ranMember =
                        z3Context.mkConst(createFreshTemporaryVariable(), bTypeToZ3Sort(relationType.getLeft()));

                    BoolExpr domMemberInDom = z3Context.mkSetMembership(domMember, (ArrayExpr) dom);
                    BoolExpr ranAndDomInArgument = z3Context.mkSetMembership(relationSort.mkDecl().apply(ranMember, domMember), argument);

                    Quantifier existsRan = z3Context.mkExists(new Expr[]{ranMember}, ranAndDomInArgument, 1, null, null, null, null);
                    Quantifier q = z3Context.mkForall(new Expr[]{domMember}, z3Context.mkEq(existsRan, domMemberInDom), 1, null, null,
                        null, null);
                    constraintList.add(q);
                    return dom;
                }
                case RANGE: {
                    ArrayExpr argument = (ArrayExpr) visitExprNode(node.getExpressionNodes().get(0), ops);

                    SetType setOfTuples = (SetType) node.getExpressionNodes().get(0).getType();
                    CoupleType relationType = (CoupleType) setOfTuples.getSubtype();
                    TupleSort relationSort = (TupleSort) bTypeToZ3Sort(relationType);

                    Expr ran = z3Context.mkConst(createFreshTemporaryVariable(), bTypeToZ3Sort(new SetType(relationType.getLeft())));
                    Expr domMember = z3Context.mkConst(createFreshTemporaryVariable(), bTypeToZ3Sort(relationType.getLeft()));
                    Expr ranMember =
                        z3Context.mkConst(createFreshTemporaryVariable(), bTypeToZ3Sort(relationType.getRight()));

                    BoolExpr ranMemberInRan = z3Context.mkSetMembership(ranMember, (ArrayExpr) ran);
                    BoolExpr ranAndDomInArgument = z3Context.mkSetMembership(relationSort.mkDecl().apply(ranMember, domMember), argument);

                    Quantifier existsDom = z3Context.mkExists(new Expr[]{domMember}, ranAndDomInArgument, 1, null, null, null, null);
                    Quantifier q = z3Context.mkForall(new Expr[]{ranMember}, z3Context.mkEq(existsDom, ranMemberInRan), 1, null, null,
                        null, null);
                    constraintList.add(q);
                    return ran;
                }
                case LAST: {
                    DatatypeExpr d = (DatatypeExpr) arguments.get(0);
                    Expr[] args = d.getArgs();
                    ArrayExpr array = (ArrayExpr) args[0];
                    ArithExpr size = (ArithExpr) args[1];
                    // add WD constraint
                    constraintList.add(z3Context.mkLe(z3Context.mkInt(1), size));
                    return z3Context.mkSelect(array, size);
                }
                case FRONT: {
                    DatatypeExpr d = (DatatypeExpr) arguments.get(0);
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
                    for (Expr expr : arguments) {
                        z3Set = z3Context.mkSetAdd(z3Set, expr);
                    }
                    return z3Set;
                }
                case SET_SUBTRACTION:
                    return z3Context.mkSetDifference((ArrayExpr) arguments.get(0), (ArrayExpr) arguments.get(1));
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
                    Expr S = arguments.get(0);
                    Expr res = z3Context.mkConst(createFreshTemporaryVariable(), bTypeToZ3Sort(setType));
                    Expr s = z3Context.mkConst(createFreshTemporaryVariable(), bTypeToZ3Sort(setType));
                    Expr e = z3Context.mkConst(createFreshTemporaryVariable(), bTypeToZ3Sort(setType.getSubtype()));

                    BoolExpr eIsInRes = z3Context.mkSetMembership(e, (ArrayExpr) res);
                    BoolExpr sIsInS = z3Context.mkSetMembership(s, (ArrayExpr) S);
                    BoolExpr eIsIns = z3Context.mkSetMembership(e, (ArrayExpr) s);
                    Quantifier exists = z3Context.mkExists(new Expr[]{s}, z3Context.mkAnd(sIsInS, eIsIns), 1, null, null,
                        null, null);
                    Quantifier q = z3Context.mkForall(new Expr[]{e}, z3Context.mkEq(eIsInRes, exists), 1, null, null,
                        null, null);
                    constraintList.add(q);
                    return res;
                }
                case EMPTY_SEQUENCE: {
                    Sort intType = z3Context.getIntSort();
                    Type type = ((SequenceType) node.getType()).getSubtype();
                    Sort rangeType = bTypeToZ3Sort(type);
                    ArrayExpr a = z3Context.mkArrayConst(createFreshTemporaryVariable(), intType, rangeType);
                    TupleSort mkTupleSort = (TupleSort) bTypeToZ3Sort(node.getType());
                    return mkTupleSort.mkDecl().apply(a, z3Context.mkInt(arguments.size()));
                }
                case SEQ_ENUMERATION: {
                    Sort intType = z3Context.getIntSort();
                    Type type = ((SequenceType) node.getType()).getSubtype();
                    Sort rangeType = bTypeToZ3Sort(type);
                    ArrayExpr a = z3Context.mkArrayConst(createFreshTemporaryVariable(), intType, rangeType);
                    int index = 1;
                    for (Expr value : arguments) {
                        a = z3Context.mkStore(a, z3Context.mkInt(index++), value);
                    }
                    TupleSort mkTupleSort = (TupleSort) bTypeToZ3Sort(node.getType());
                    return mkTupleSort.mkDecl().apply(a, z3Context.mkInt(arguments.size()));
                }
                case FIRST: {
                    DatatypeExpr d = (DatatypeExpr) arguments.get(0);
                    Expr[] args = d.getArgs();
                    ArrayExpr array = (ArrayExpr) args[0];
                    ArithExpr size = (ArithExpr) args[1];
                    // add WD constraint
                    constraintList.add(z3Context.mkLe(z3Context.mkInt(1), size));
                    return z3Context.mkSelect(array, z3Context.mkInt(1));
                }
                case FUNCTION_CALL: {
                    DatatypeExpr d = (DatatypeExpr) arguments.get(0);
                    Expr[] args = d.getArgs();
                    ArrayExpr array = (ArrayExpr) args[0];
                    ArithExpr size = (ArithExpr) args[1];
                    ArithExpr index = (ArithExpr) arguments.get(1);
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
                case INVERSE_RELATION: {
                    SetType nType = (SetType) node.getType();
                    CoupleType subType = (CoupleType) nType.getSubtype();
                    CoupleType revType = new CoupleType(subType.getRight(), subType.getLeft());

                    TupleSort subSort = (TupleSort) bTypeToZ3Sort(subType);
                    TupleSort revSort = (TupleSort) bTypeToZ3Sort(revType);

                    Expr tempLeft = z3Context.mkConst(createFreshTemporaryVariable(), bTypeToZ3Sort(subType.getLeft()));
                    Expr tempRight = z3Context.mkConst(createFreshTemporaryVariable(), bTypeToZ3Sort(subType.getRight()));
                    ArrayExpr tempConstant = (ArrayExpr) z3Context.mkConst(createFreshTemporaryVariable(), bTypeToZ3Sort(new SetType(revType)));

                    BoolExpr lrInExpr = z3Context.mkSetMembership(subSort.mkDecl().apply(tempLeft, tempRight), (ArrayExpr) arguments.get(0));
                    BoolExpr rlInTempExpr = z3Context.mkSetMembership(revSort.mkDecl().apply(tempRight, tempLeft), tempConstant);

                    BoolExpr equality = z3Context.mkEq(lrInExpr, rlInTempExpr);
                    Expr[] bound = new Expr[]{tempLeft, tempRight};

                    Quantifier q = z3Context.mkForall(bound, equality, 2, null, null, null, null);
                    constraintList.add(q);
                    return tempConstant;
                }
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
                case CARTESIAN_PRODUCT: {
                    SetType setType = (SetType) node.getType();
                    CoupleType coupleType = (CoupleType) setType.getSubtype();

                    TupleSort bTypeToZ3Sort = (TupleSort) bTypeToZ3Sort(coupleType);

                    ArithExpr leftExpr = (ArithExpr) z3Context.mkConst(createFreshTemporaryVariable(), bTypeToZ3Sort(coupleType.getLeft()));
                    ArithExpr rightExpr = (ArithExpr) z3Context.mkConst(createFreshTemporaryVariable(), bTypeToZ3Sort(coupleType.getRight()));

                    ArrayExpr tempConstant = (ArrayExpr) z3Context.mkConst(createFreshTemporaryVariable(), bTypeToZ3Sort(node.getType()));
                    Expr couple = bTypeToZ3Sort.mkDecl().apply(leftExpr, rightExpr);

                    BoolExpr xInLeft = z3Context.mkSetMembership(leftExpr, (ArrayExpr) arguments.get(0));
                    BoolExpr yInRight = z3Context.mkSetMembership(rightExpr, (ArrayExpr) arguments.get(1));
                    BoolExpr coupleInCartesian = z3Context.mkSetMembership(couple, tempConstant);

                    BoolExpr cartesian = z3Context.mkAnd(xInLeft, yInRight);
                    BoolExpr equality = z3Context.mkEq(cartesian, coupleInCartesian);

                    Expr[] bound = new Expr[]{leftExpr, rightExpr};

                    Quantifier q = z3Context.mkForall(bound, equality, 2, null, null, null, null);
                    constraintList.add(q);
                    return tempConstant;
                }
                case INT: {
                    Type type = node.getType();// POW(INTEGER)
                    int maxInt = PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MAX_INT);
                    int minInt = PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MIN_INT);
                    // !x.((x >= MIN_INT & x <= MAX_INT) <=> x : INT)
                    Expr integer = z3Context.mkConst(ExpressionOperator.INT.toString(), bTypeToZ3Sort(type));
                    Expr x = z3Context.mkConst("x", z3Context.getIntSort());
                    Expr[] bound = new Expr[]{x};
                    // x >= MIN_INT
                    BoolExpr a = z3Context.mkGe((ArithExpr) x, z3Context.mkInt(minInt));
                    // x :INT
                    BoolExpr b = z3Context.mkSetMembership(x, (ArrayExpr) integer);
                    // x <= max_int
                    BoolExpr c = z3Context.mkLe((ArithExpr) x, z3Context.mkInt(maxInt));
                    // a <=> b <=> c
                    BoolExpr body = z3Context.mkEq(z3Context.mkAnd(a, c), b);
                    Quantifier q = z3Context.mkForall(bound, body, 1, null, null, null, null);
                    constraintList.add(q);
                    return integer;
                }
                case MAXINT: {
                    int maxInt = PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MAX_INT);
                    return z3Context.mkInt(maxInt);
                }
                case MININT: {
                    int minInt = PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MIN_INT);
                    return z3Context.mkInt(minInt);
                }
                case NAT: {
                    Type type = node.getType();// POW(INTEGER)
                    int maxInt = PersonalPreferences.getIntPreference(PersonalPreferences.IntPreference.MAX_INT);
                    // !x.((x >= 0 & x <= MAX_INT) <=> x : NAT)
                    Expr x = z3Context.mkConst("x", z3Context.getIntSort());
                    Expr nat = z3Context.mkConst(ExpressionOperator.NAT.toString(), bTypeToZ3Sort(type));
                    Expr[] bound = new Expr[]{x};
                    // x >= 0
                    BoolExpr a = z3Context.mkGe((ArithExpr) x, z3Context.mkInt(0));
                    // x : NAT
                    BoolExpr b = z3Context.mkSetMembership(x, (ArrayExpr) nat);
                    // x <= max_int
                    BoolExpr c = z3Context.mkLe((ArithExpr) x, z3Context.mkInt(maxInt));
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

        private FuncDecl initPowerOf() {
            // create function declaration
            FuncDecl powerOf = z3Context.mkFuncDecl(ExpressionOperator.POWER_OF.toString(), new Sort[]{z3Context.mkIntSort(), z3Context.mkIntSort()}, z3Context.mkIntSort());

            // create arguments & bounds
            Expr a = z3Context.mkConst("a", z3Context.getIntSort());
            Expr b = z3Context.mkConst("b", z3Context.getIntSort());
            Expr[] bound = new Expr[]{a, b};

            // pow( a, b / 2 ) * pow( a, b / 2 )
            Expr expEven = z3Context.mkMul((ArithExpr) powerOf.apply(a, z3Context.mkDiv((ArithExpr) b, z3Context.mkInt(2))), (ArithExpr) powerOf.apply(a, z3Context.mkDiv((ArithExpr) b, z3Context.mkInt(2))));
            // a * pow( a, b - 1 )
            Expr expOdd = z3Context.mkMul((ArithExpr) a, (ArithExpr) powerOf.apply(a, z3Context.mkSub((ArithExpr) b, z3Context.mkInt(1))));

            // b % 2 == 0 ? expEven : expOdd
            Expr expEvenOdd = z3Context.mkITE(z3Context.mkEq(z3Context.mkInt(0), z3Context.mkMod((IntExpr) b, z3Context.mkInt(2))), expEven, expOdd);

            // b == 0 ? 1 : expEvenOdd
            Expr expZero = z3Context.mkITE(z3Context.mkEq(z3Context.mkInt(0), b), z3Context.mkInt(1), expEvenOdd);

            // pow( a, b ) = expZero
            Expr body = z3Context.mkEq(powerOf.apply(a, b), expZero);

            // prepare pattern
            Pattern[] patterns = new Pattern[]{z3Context.mkPattern(powerOf.apply(a, b))};

            // annotate recursive function
            Symbol recFun = z3Context.mkSymbol(":rec-fun");

            BoolExpr powConstraint = z3Context.mkForall(bound, body, bound.length, patterns, null, recFun, null);
            constraintList.add(powConstraint);

            return powerOf;
        }

        @Override
        public Expr visitNumberNode(NumberNode node, TranslationOptions ops) {
            return z3Context.mkInt(node.getValue());
        }

        @Override
        public Expr visitPredicateOperatorNode(PredicateOperatorNode node, TranslationOptions ops) {
            final List<BoolExpr> arguments = node.getPredicateArguments().stream().map(it -> (BoolExpr) visitPredicateNode(it, ops)).collect(Collectors.toList());
            switch (node.getOperator()) {
                case AND:
                    return z3Context.mkAnd(arguments.toArray(new BoolExpr[arguments.size()]));
                case OR:
                    return z3Context.mkOr(arguments.toArray(new BoolExpr[arguments.size()]));
                case IMPLIES:
                    return z3Context.mkImplies(arguments.get(0), arguments.get(1));
                case EQUIVALENCE:
                    return z3Context.mkEq(arguments.get(0), arguments.get(1));
                case NOT:
                    return z3Context.mkNot(arguments.get(0));
                case TRUE:
                    return z3Context.mkTrue();
                case FALSE:
                    return z3Context.mkFalse();
                default:
                    throw new AssertionError("Not implemented: " + node.getOperator());
            }
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
            final Expr[] identifiers = node.getDeclarationList().stream().map(declaration -> z3Context.mkConst(declaration.getName(), bTypeToZ3Sort(declaration.getType()))).toArray(Expr[]::new);
            final Expr predicate = visitPredicateNode(node.getPredicateNode(), opt);
            switch (node.getOperator()) {
                case EXISTENTIAL_QUANTIFICATION:
                    return z3Context.mkExists(identifiers, predicate, identifiers.length, null, null, null, null);
                case UNIVERSAL_QUANTIFICATION:
                    return z3Context.mkForall(identifiers, predicate, identifiers.length, null, null, null, null);
                default:
                    throw new AssertionError("Implement: " + node.getClass());
            }

        }

        @Override
        public Expr visitAnySubstitution(AnySubstitutionNode node, TranslationOptions opt) {
            throw new AssertionError("Not reachable");
        }
    }
}
