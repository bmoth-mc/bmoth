package de.bmoth.backend.z3;

import com.microsoft.z3.*;
import de.bmoth.backend.TranslationOptions;
import de.bmoth.backend.z3.Z3TypeInference.*;
import de.bmoth.parser.Parser;
import de.bmoth.parser.ParserException;
import de.bmoth.parser.ast.nodes.*;
import de.bmoth.parser.ast.nodes.ExpressionOperatorNode.ExpressionOperator;
import de.bmoth.parser.ast.nodes.FormulaNode.FormulaType;
import de.bmoth.parser.ast.visitors.FormulaVisitor;
import de.bmoth.preferences.BMothPreferences;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static de.bmoth.backend.TranslationOptions.UNPRIMED;
import static de.bmoth.parser.ast.nodes.ExpressionOperatorNode.ExpressionOperator.MAX;
import static de.bmoth.parser.ast.nodes.ExpressionOperatorNode.ExpressionOperator.MIN;

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

    private List<DeclarationNode> implicitDeclarations;
    private FormulaNode formulaNode;
    private final Z3TypeInference z3TypeInference;

    private FuncDecl pow = null;

    private ArrayExpr setNatural = null;
    private ArrayExpr setNatural1 = null;
    private ArrayExpr setInt = null;
    private ArrayExpr setNat = null;
    private ArrayExpr setNat1 = null;

    // used to generate unique identifiers
    private static int tempVariablesCounter = 0;

    public List<DeclarationNode> getImplicitDeclarations() {
        return this.implicitDeclarations;
    }

    public List<Expr> getImplicitVariablesAsZ3Expression() {
        List<Expr> list = new ArrayList<>();
        for (DeclarationNode decl : implicitDeclarations) {
            Expr mkConst = this.z3Context.mkConst(decl.getName(), z3TypeInference.getZ3Sort(decl, z3Context));
            list.add(mkConst);
        }
        return list;
    }

    private static String createFreshTemporaryVariable() {
        tempVariablesCounter++;
        return "$t_" + tempVariablesCounter;
    }

    private FormulaToZ3Translator(Context z3Context, String formula) throws ParserException {
        this.z3Context = z3Context;
        formulaNode = Parser.getFormulaAsSemanticAst(formula);
        this.implicitDeclarations = formulaNode.getImplicitDeclarations();
        z3TypeInference = new Z3TypeInference();
        z3TypeInference.visitPredicateNode((PredicateNode) formulaNode.getFormula());
    }

    private FormulaToZ3Translator(Context z3Context, Z3TypeInference z3TypeInference) {
        this.z3Context = z3Context;
        this.z3TypeInference = z3TypeInference;
    }

    static BoolExpr translateVariableEqualToExpr(String name, ExprNode value, Context z3Context,
                                                 Z3TypeInference z3TypeInference) {
        ExprNode exprNode = AstTransformationsForZ3.transformExprNode(value);
        return translateVariableEqualToExpr(name, exprNode, z3Context, UNPRIMED, z3TypeInference);
    }

    private static BoolExpr translateVariableEqualToExpr(String name, ExprNode value, Context z3Context,
                                                         TranslationOptions opt, Z3TypeInference z3TypeInference) {
        FormulaToZ3Translator formulaToZ3Translator = new FormulaToZ3Translator(z3Context, z3TypeInference);
        FormulaToZ3TranslatorVisitor visitor = formulaToZ3Translator.new FormulaToZ3TranslatorVisitor();
        Expr z3Value = visitor.visitExprNode(value, opt);
        Expr variable = z3Context.mkConst(name, z3Value.getSort());
        BoolExpr mkEq = z3Context.mkEq(variable, z3Value);

        // adding all additional constraints to result
        BoolExpr accumulated = formulaToZ3Translator.getAccumulatedConstraints(z3Context);
        if (accumulated != null) {
            return z3Context.mkAnd(mkEq, accumulated);
        } else {
            return mkEq;
        }
    }

    static BoolExpr translateVariableElementOfSetExpr(String name, DeclarationNode variable, ExprNode setValue,
                                                      Context z3Context, TranslationOptions opt, Z3TypeInference z3TypeInference) {
        FormulaToZ3Translator formulaToZ3Translator = new FormulaToZ3Translator(z3Context, z3TypeInference);
        FormulaToZ3TranslatorVisitor visitor = formulaToZ3Translator.new FormulaToZ3TranslatorVisitor();
        ArrayExpr z3Value = (ArrayExpr) visitor.visitExprNode(setValue, opt);
        Expr variableExpr = z3Context.mkConst(name,
            formulaToZ3Translator.z3TypeInference.getZ3Sort(variable, z3Context));
        BoolExpr mkEq = z3Context.mkSetMembership(variableExpr, z3Value);

        BoolExpr accumulated = formulaToZ3Translator.getAccumulatedConstraints(z3Context);
        if (accumulated != null) {
            return z3Context.mkAnd(mkEq, accumulated);
        } else {
            return mkEq;
        }
    }

    private BoolExpr getAccumulatedConstraints(Context z3Context) {
        switch (constraintList.size()) {
            case 0:
                return null;
            case 1:
                return constraintList.get(0);
            default:
                return z3Context.mkAnd(constraintList.toArray(new BoolExpr[0]));
        }
    }

    public static BoolExpr translatePredicate(String formula, Context z3Context) throws ParserException {
        FormulaToZ3Translator formulaToZ3Translator = new FormulaToZ3Translator(z3Context, formula);

        if (formulaToZ3Translator.formulaNode.getFormulaType() != FormulaType.PREDICATE_FORMULA) {
            throw new IllegalArgumentException("Expected predicate.");
        }
        PredicateNode predNode = AstTransformationsForZ3
            .transformPredicate((PredicateNode) formulaToZ3Translator.formulaNode.getFormula());

        FormulaToZ3TranslatorVisitor visitor = formulaToZ3Translator.new FormulaToZ3TranslatorVisitor();
        Expr constraint = visitor.visitPredicateNode(predNode, UNPRIMED);
        if (!(constraint instanceof BoolExpr)) {
            throw new IllegalStateException("Invalid translation. Expected BoolExpr but found " + constraint.getClass());
        }
        // adding all additional constraints to result
        BoolExpr accumulated = formulaToZ3Translator.getAccumulatedConstraints(z3Context);
        if (accumulated != null) {
            return z3Context.mkAnd((BoolExpr) constraint, accumulated);
        } else {
            return (BoolExpr) constraint;
        }
    }

    public static BoolExpr translatePredicate(PredicateNode pred, Context z3Context, Z3TypeInference z3TypeInference) {
        PredicateNode predNode = AstTransformationsForZ3.transformPredicate(pred);
        return translatePredicate(predNode, z3Context, UNPRIMED, z3TypeInference);
    }

    public static BoolExpr translatePredicate(PredicateNode pred, Context z3Context, TranslationOptions opt,
                                              Z3TypeInference z3TypeInference) {
        PredicateNode predNode = AstTransformationsForZ3.transformPredicate(pred);
        FormulaToZ3Translator formulaToZ3Translator = new FormulaToZ3Translator(z3Context, z3TypeInference);
        FormulaToZ3TranslatorVisitor formulaToZ3TranslatorVisitor = formulaToZ3Translator.new FormulaToZ3TranslatorVisitor();

        BoolExpr boolExpr = (BoolExpr) formulaToZ3TranslatorVisitor.visitPredicateNode(predNode, opt);
        // adding all additional constraints to result
        BoolExpr accumulated = formulaToZ3Translator.getAccumulatedConstraints(z3Context);
        if (accumulated != null) {
            return z3Context.mkAnd(boolExpr, accumulated);
        } else {
            return boolExpr;
        }
    }

    private Sort getZ3Sort(TypedNode node) {
        return z3TypeInference.getZ3Sort(node, z3Context);
    }

    private Z3Type getZ3Type(TypedNode node) {
        return z3TypeInference.getZ3TypeOfNode(node);
    }

    private Sort getZ3Sort(Z3Type z3Type) {
        return z3TypeInference.getZ3Sort(z3Type, z3Context);
    }

    class FormulaToZ3TranslatorVisitor implements FormulaVisitor<Expr, TranslationOptions> {

        class OperatorNotImplementedError extends AssertionError {
            private static final long serialVersionUID = 4872994563768693737L;

            OperatorNotImplementedError(OperatorNode<?> node) {
                super("Not implemented: " + node.getOperator());
            }

        }

        @Override
        public Expr visitIdentifierExprNode(IdentifierExprNode node, TranslationOptions ops) {
            return z3Context.mkConst(node.getName(), getZ3Sort(node.getDeclarationNode()));
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
            final List<Expr> arguments = node.getExpressionNodes().stream().map(it -> visitExprNode(it, ops))
                .collect(Collectors.toList());
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
                    return z3Context.mkAnd(z3Context.mkNot(z3Context.mkEq(arguments.get(0), arguments.get(1))),
                        z3Context.mkSetSubset((ArrayExpr) arguments.get(0), (ArrayExpr) arguments.get(1)));
                case NON_INCLUSION:
                    return z3Context
                        .mkNot(z3Context.mkSetSubset((ArrayExpr) arguments.get(0), (ArrayExpr) arguments.get(1)));
                case STRICT_NON_INCLUSION:
                    return z3Context
                        .mkNot(z3Context.mkAnd(z3Context.mkNot(z3Context.mkEq(arguments.get(0), arguments.get(1))),
                            z3Context.mkSetSubset((ArrayExpr) arguments.get(0), (ArrayExpr) arguments.get(1))));
                default:
                    throw new OperatorNotImplementedError(node);
            }
        }

        @Override
        public Expr visitExprOperatorNode(ExpressionOperatorNode node, TranslationOptions ops) {
            final List<Expr> arguments = node.getExpressionNodes().stream().map(it -> visitExprNode(it, ops))
                .collect(Collectors.toList());
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
                case INTERVAL:
                    ArrayExpr interval = (ArrayExpr) z3Context.mkConst(createFreshTemporaryVariable(), getZ3Sort(node));
                    constraintList
                        .add(prepareSetQuantifier(interval, (IntExpr) arguments.get(0), (IntExpr) arguments.get(1)));
                    return interval;
                case INTEGER:
                    return z3Context.mkFullSet(z3Context.mkIntSort());
                case NATURAL1:
                    if (setNatural1 == null) {
                        // node.getType() = POW(INTEGER)
                        setNatural1 = (ArrayExpr) z3Context.mkConst("NATURAL1", getZ3Sort(node));
                        constraintList.add(prepareSetQuantifier(setNatural1, z3Context.mkInt(1), null));
                    }
                    return setNatural1;
                case NATURAL:
                    if (setNatural == null) {
                        // node.getType() = POW(INTEGER)
                        setNatural = (ArrayExpr) z3Context.mkConst("NATURAL", getZ3Sort(node));
                        constraintList.add(prepareSetQuantifier(setNatural, z3Context.mkInt(0), null));
                    }
                    return setNatural;
                case INT:
                    if (setInt == null) {
                        int maxInt = BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INT);
                        int minInt = BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MIN_INT);
                        // node.getType() = POW(INTEGER)
                        setInt = (ArrayExpr) z3Context.mkConst(ExpressionOperator.INT.toString(), getZ3Sort(node));
                        constraintList.add(prepareSetQuantifier(setInt, z3Context.mkInt(minInt), z3Context.mkInt(maxInt)));
                    }
                    return setInt;
                case NAT:
                    if (setNat == null) {
                        int maxInt = BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INT);
                        // node.getType() = POW(INTEGER)
                        setNat = (ArrayExpr) z3Context.mkConst(ExpressionOperator.NAT.toString(), getZ3Sort(node));
                        constraintList.add(prepareSetQuantifier(setNat, z3Context.mkInt(0), z3Context.mkInt(maxInt)));
                    }
                    return setNat;
                case NAT1:
                    if (setNat1 == null) {
                        int maxInt = BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INT);
                        // node.getType() = POW(INTEGER)
                        setNat1 = (ArrayExpr) z3Context.mkConst(ExpressionOperator.NAT1.toString(), getZ3Sort(node));
                        constraintList.add(prepareSetQuantifier(setNat1, z3Context.mkInt(1), z3Context.mkInt(maxInt)));
                    }
                    return setNat1;
                case FALSE:
                    return z3Context.mkFalse();
                case TRUE:
                    return z3Context.mkTrue();
                case BOOL:
                    return z3Context.mkFullSet(z3Context.mkBoolSort());
                case UNION:
                    return z3Context.mkSetUnion(arguments.stream().map(it -> (ArrayExpr) it).toArray(ArrayExpr[]::new));
                case COUPLE: {
                    TupleSort bTypeToZ3Sort = (TupleSort) getZ3Sort(node);
                    return bTypeToZ3Sort.mkDecl().apply(arguments.get(0), arguments.get(1));
                }
                case INTERSECTION:
                    return z3Context.mkSetIntersection((ArrayExpr) arguments.get(0), (ArrayExpr) arguments.get(1));
                case DOMAIN: {
                    ArrayExpr argument = (ArrayExpr) visitExprNode(node.getExpressionNodes().get(0), ops);

                    Z3SetType setOfTuples = (Z3SetType) getZ3Type(node.getExpressionNodes().get(0));
                    Z3CoupleType coupleType = (Z3CoupleType) setOfTuples.getSubtype();
                    TupleSort tupleSort = (TupleSort) getZ3Sort(coupleType);

                    Expr dom = z3Context.mkConst(createFreshTemporaryVariable(),
                        z3TypeInference.getZ3Sort(z3TypeInference.new Z3SetType(coupleType.getRightType()), z3Context));
                    Expr domMember = z3Context.mkConst(createFreshTemporaryVariable(),
                        getZ3Sort(coupleType.getRightType()));
                    Expr ranMember = z3Context.mkConst(createFreshTemporaryVariable(), getZ3Sort(coupleType.getLeftType()));

                    BoolExpr domMemberInDom = z3Context.mkSetMembership(domMember, (ArrayExpr) dom);
                    BoolExpr ranAndDomInArgument = z3Context.mkSetMembership(tupleSort.mkDecl().apply(ranMember, domMember),
                        argument);

                    Quantifier existsRan = z3Context.mkExists(new Expr[]{ranMember}, ranAndDomInArgument, 1, null, null,
                        null, null);
                    Quantifier q = z3Context.mkForall(new Expr[]{domMember}, z3Context.mkEq(existsRan, domMemberInDom),
                        1, null, null, null, null);
                    constraintList.add(q);
                    return dom;
                }
                case RANGE: {
                    ArrayExpr argument = (ArrayExpr) visitExprNode(node.getExpressionNodes().get(0), ops);

                    Z3SetType setOfTuples = (Z3SetType) getZ3Type(node.getExpressionNodes().get(0));
                    Z3CoupleType coupleType = (Z3CoupleType) setOfTuples.getSubtype();
                    TupleSort tupleSort = (TupleSort) getZ3Sort(coupleType);

                    Expr ran = z3Context.mkConst(createFreshTemporaryVariable(),
                        getZ3Sort(z3TypeInference.new Z3SetType(coupleType.getLeftType())));
                    Expr domMember = z3Context.mkConst(createFreshTemporaryVariable(),
                        getZ3Sort(coupleType.getRightType()));
                    Expr ranMember = z3Context.mkConst(createFreshTemporaryVariable(), getZ3Sort(coupleType.getLeftType()));

                    BoolExpr ranMemberInRan = z3Context.mkSetMembership(ranMember, (ArrayExpr) ran);
                    BoolExpr ranAndDomInArgument = z3Context.mkSetMembership(tupleSort.mkDecl().apply(ranMember, domMember),
                        argument);

                    Quantifier existsDom = z3Context.mkExists(new Expr[]{domMember}, ranAndDomInArgument, 1, null, null,
                        null, null);
                    Quantifier q = z3Context.mkForall(new Expr[]{ranMember}, z3Context.mkEq(existsDom, ranMemberInRan),
                        1, null, null, null, null);
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
                    TupleSort mkTupleSort = (TupleSort) getZ3Sort(node);
                    return mkTupleSort.mkDecl().apply(array, z3Context.mkSub(size, z3Context.mkInt(1)));
                }
                case TAIL:
                    break;
                case CONC:
                    break;
                case EMPTY_SET: // not missing! it is equal to an empty enumeration
                    // below
                case SET_ENUMERATION: {
                    Z3SetType setType = (Z3SetType) getZ3Type(node);
                    Z3Type subType = setType.getSubtype();
                    ArrayExpr z3Set = z3Context.mkEmptySet(getZ3Sort(subType));
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
                case GENERALIZED_UNION:
                    return translateGeneralizedUnion(node, arguments);
                case EMPTY_SEQUENCE: // handled by SEQ_enumeration with empty
                    // arguments
                case SEQ_ENUMERATION:
                    return translateSeqEnumeration(node, arguments);
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
                case CARD:
                    break;
                case INSERT_FRONT:
                case INSERT_TAIL:
                case OVERWRITE_RELATION:
                    break;
                case INVERSE_RELATION: {
                    Z3SetType setType = (Z3SetType) getZ3Type(node);
                    Z3CoupleType couple = (Z3CoupleType) setType.getSubtype();
                    Z3CoupleType revCouple = z3TypeInference.new Z3CoupleType(couple.getRightType(), couple.getLeftType());

                    TupleSort subSort = (TupleSort) getZ3Sort(couple);
                    TupleSort revSort = (TupleSort) getZ3Sort(revCouple);

                    Expr tempLeft = z3Context.mkConst(createFreshTemporaryVariable(), getZ3Sort(couple.getLeftType()));
                    Expr tempRight = z3Context.mkConst(createFreshTemporaryVariable(), getZ3Sort(couple.getRightType()));
                    ArrayExpr tempConstant = (ArrayExpr) z3Context.mkConst(createFreshTemporaryVariable(),
                        getZ3Sort(z3TypeInference.new Z3SetType(revCouple)));

                    BoolExpr lrInExpr = z3Context.mkSetMembership(subSort.mkDecl().apply(tempLeft, tempRight),
                        (ArrayExpr) arguments.get(0));
                    BoolExpr rlInTempExpr = z3Context.mkSetMembership(revSort.mkDecl().apply(tempRight, tempLeft),
                        tempConstant);

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

                    Z3SetType setType = (Z3SetType) getZ3Type(node);
                    Z3CoupleType coupleType = (Z3CoupleType) setType.getSubtype();

                    TupleSort bTypeToZ3Sort = (TupleSort) getZ3Sort(coupleType);

                    ArithExpr leftExpr = (ArithExpr) z3Context.mkConst(createFreshTemporaryVariable(),
                        getZ3Sort(coupleType.getLeftType()));
                    ArithExpr rightExpr = (ArithExpr) z3Context.mkConst(createFreshTemporaryVariable(),
                        getZ3Sort(coupleType.getRightType()));

                    ArrayExpr tempConstant = (ArrayExpr) z3Context.mkConst(createFreshTemporaryVariable(), getZ3Sort(node));
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
                case MAXINT:
                    return z3Context.mkInt(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INT));
                case MININT:
                    return z3Context.mkInt(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MIN_INT));
                case MIN:
                    return translateMinAndMax(MIN, node, arguments);
                case MAX:
                    return translateMinAndMax(MAX, node, arguments);
                default:
                    break;
            }
            throw new OperatorNotImplementedError(node);
        }

        private Expr translateMinAndMax(ExpressionOperator minOrMax, ExpressionOperatorNode node, List<Expr> arguments) {
            ArithExpr replacingConstant = (ArithExpr) z3Context.mkConst(createFreshTemporaryVariable(), getZ3Sort(node));
            ArithExpr member = (ArithExpr) z3Context.mkConst(createFreshTemporaryVariable(), getZ3Sort(node));

            BoolExpr memberInSet = z3Context.mkSetMembership(member, (ArrayExpr) arguments.get(0));
            BoolExpr sort;
            if (minOrMax == MAX) {
                sort = z3Context.mkLe(member, replacingConstant);
            } else {
                sort = z3Context.mkGe(member, replacingConstant);
            }
            BoolExpr implication = z3Context.mkImplies(memberInSet, sort);

            Quantifier allSorted = z3Context.mkForall(new Expr[]{member}, implication, 1, null, null, null, null);
            Quantifier equalToOne = z3Context.mkExists(new Expr[]{member}, z3Context.mkEq(member, replacingConstant), 1, null, null, null, null);

            constraintList.add(allSorted);
            constraintList.add(equalToOne);
            return replacingConstant;
        }

        private Expr translateGeneralizedUnion(ExpressionOperatorNode node, List<Expr> arguments) {
            // union(S)
            // return Res
            // !(e).(e : Res <=> #(s).(s : S & e : s)
            Z3SetType setType = (Z3SetType) getZ3Type(node);

            Expr setOfSets = arguments.get(0);
            Expr res = z3Context.mkConst(createFreshTemporaryVariable(), z3TypeInference.getZ3Sort(setType, z3Context));
            Expr setVar = z3Context.mkConst(createFreshTemporaryVariable(), getZ3Sort(setType));
            Expr elementVar = z3Context.mkConst(createFreshTemporaryVariable(), getZ3Sort(setType.getSubtype()));

            BoolExpr eIsInRes = z3Context.mkSetMembership(elementVar, (ArrayExpr) res);
            BoolExpr sIsInS = z3Context.mkSetMembership(setVar, (ArrayExpr) setOfSets);
            BoolExpr eIsIns = z3Context.mkSetMembership(elementVar, (ArrayExpr) setVar);
            Quantifier exists = z3Context.mkExists(new Expr[]{setVar}, z3Context.mkAnd(sIsInS, eIsIns), 1, null,
                null, null, null);
            Quantifier q = z3Context.mkForall(new Expr[]{elementVar}, z3Context.mkEq(eIsInRes, exists), 1, null,
                null, null, null);
            constraintList.add(q);
            return res;
        }

        private Expr translateSeqEnumeration(ExpressionOperatorNode node, List<Expr> arguments) {
            Sort intType = z3Context.getIntSort();
            Z3SequenceType setType = (Z3SequenceType) getZ3Type(node);
            Sort rangeType = getZ3Sort(setType.getSubtype());
            ArrayExpr a = z3Context.mkArrayConst(createFreshTemporaryVariable(), intType, rangeType);
            int index = 1;
            for (Expr value : arguments) {
                a = z3Context.mkStore(a, z3Context.mkInt(index++), value);
            }
            TupleSort mkTupleSort = (TupleSort) getZ3Sort(node);
            return mkTupleSort.mkDecl().apply(a, z3Context.mkInt(arguments.size()));
        }

        private Quantifier prepareSetQuantifier(ArrayExpr set, IntExpr min, IntExpr max) {
            ArithExpr x = (ArithExpr) z3Context.mkConst(createFreshTemporaryVariable(), z3Context.getIntSort());
            ArithExpr[] bound = new ArithExpr[]{x};

            // x : set
            BoolExpr membership = z3Context.mkSetMembership(x, set);

            BoolExpr body;
            if (min != null && max != null) {
                body = z3Context.mkEq(z3Context.mkAnd(z3Context.mkGe(x, min), z3Context.mkLe(x, max)), membership);
            } else if (min != null) {
                body = z3Context.mkEq(z3Context.mkGe(x, min), membership);
            } else if (max != null) {
                body = z3Context.mkEq(z3Context.mkLe(x, max), membership);
            } else {
                body = membership;
            }

            return z3Context.mkForall(bound, body, bound.length, null, null, null, null);
        }

        private FuncDecl initPowerOf() {
            // create function declaration
            FuncDecl powerOf = z3Context.mkFuncDecl(ExpressionOperator.POWER_OF.toString(),
                new Sort[]{z3Context.mkIntSort(), z3Context.mkIntSort()}, z3Context.mkIntSort());

            // create arguments & bounds
            Expr a = z3Context.mkConst("a", z3Context.getIntSort());
            Expr b = z3Context.mkConst("b", z3Context.getIntSort());
            Expr[] bound = new Expr[]{a, b};

            // pow( a, b / 2 ) * pow( a, b / 2 )
            Expr expEven = z3Context.mkMul(
                (ArithExpr) powerOf.apply(a, z3Context.mkDiv((ArithExpr) b, z3Context.mkInt(2))),
                (ArithExpr) powerOf.apply(a, z3Context.mkDiv((ArithExpr) b, z3Context.mkInt(2))));
            // a * pow( a, b - 1 )
            Expr expOdd = z3Context.mkMul((ArithExpr) a,
                (ArithExpr) powerOf.apply(a, z3Context.mkSub((ArithExpr) b, z3Context.mkInt(1))));

            // b % 2 == 0 ? expEven : expOdd
            Expr expEvenOdd = z3Context.mkITE(
                z3Context.mkEq(z3Context.mkInt(0), z3Context.mkMod((IntExpr) b, z3Context.mkInt(2))), expEven,
                expOdd);

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
            return z3Context.mkInt(node.getValue().longValueExact());
        }

        @Override
        public Expr visitPredicateOperatorNode(PredicateOperatorNode node, TranslationOptions ops) {
            final List<BoolExpr> arguments = node.getPredicateArguments().stream()
                .map(it -> (BoolExpr) visitPredicateNode(it, ops)).collect(Collectors.toList());
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
                    throw new OperatorNotImplementedError(node);
            }
        }

        @Override
        public Expr visitQuantifiedExpressionNode(QuantifiedExpressionNode node, TranslationOptions opt) {
            throw new AssertionError("Implement: " + node.getClass());
        }

        @Override
        public Expr visitSetComprehensionNode(SetComprehensionNode node, TranslationOptions opt) {
            // {e| P}
            // return T
            // !(e).(e : T <=> P )
            Expr comprehensionPredicate = visitPredicateNode(node.getPredicateNode(), opt);
            Expr elementInComprehension = z3Context.mkConst(createFreshTemporaryVariable(), getZ3Sort(node));

            Expr[] boundVariables = new Expr[node.getDeclarationList().size()];
            for (int i = 0; i < boundVariables.length; i++) {
                DeclarationNode decl = node.getDeclarationList().get(i);
                Expr e = z3Context.mkConst(decl.getName(), getZ3Sort(decl));
                boundVariables[i] = e;
            }
            Expr tuple = null;
            if (boundVariables.length > 1) {
                Z3SetType setType = (Z3SetType) getZ3Type(node);
                TupleSort tupleSort = (TupleSort) getZ3Sort(setType.getSubtype());
                tuple = tupleSort.mkDecl().apply(boundVariables);
            } else {
                tuple = boundVariables[0];
            }

            BoolExpr a = z3Context.mkSetMembership(tuple, (ArrayExpr) elementInComprehension);
            // a <=> P
            BoolExpr body = z3Context.mkEq(a, comprehensionPredicate);
            Quantifier q = z3Context.mkForall(boundVariables, body, boundVariables.length, null, null, null, null);
            constraintList.add(q);
            return elementInComprehension;
        }

        @Override
        public Expr visitQuantifiedPredicateNode(QuantifiedPredicateNode node, TranslationOptions opt) {
            final Expr[] identifiers = node.getDeclarationList().stream()
                .map(declaration -> z3Context.mkConst(declaration.getName(), getZ3Sort(declaration)))
                .toArray(Expr[]::new);
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
        public Expr visitEnumerationSetNode(EnumerationSetNode node, TranslationOptions ops) {
            Z3SetType setType = (Z3SetType) getZ3Type(node);
            Z3Type subtype = setType.getSubtype();
            EnumSort enumSort = (EnumSort) getZ3Sort(subtype);
            return z3Context.mkFullSet(enumSort);
        }

        @Override
        public Expr visitDeferredSetNode(DeferredSetNode node, TranslationOptions ops) {
            Z3SetType setType = (Z3SetType) getZ3Type(node);
            Z3Type subtype = setType.getSubtype();
            Sort defSetSort = getZ3Sort(subtype);
            return z3Context.mkFullSet(defSetSort);
        }

        @Override
        public Expr visitEnumeratedSetElementNode(EnumeratedSetElementNode node, TranslationOptions ops) {
            Z3Type z3Type = getZ3Type(node);
            Z3EnumeratedSetType enumType = (Z3EnumeratedSetType) z3Type;
            String name = node.getName();
            EnumSort enumSort = (EnumSort) getZ3Sort(node);
            return enumSort.getConsts()[enumType.getElements().indexOf(name)];
        }

    }

}
