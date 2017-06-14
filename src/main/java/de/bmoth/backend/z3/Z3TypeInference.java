package de.bmoth.backend.z3;

import com.microsoft.z3.Context;
import com.microsoft.z3.Sort;
import com.microsoft.z3.Symbol;

import de.bmoth.parser.ast.nodes.*;
import de.bmoth.parser.ast.nodes.ltl.LTLBPredicateNode;
import de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode;
import de.bmoth.parser.ast.nodes.ltl.LTLKeywordNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.types.*;
import de.bmoth.parser.ast.visitors.AbstractVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Z3TypeInference {
    Map<TypedNode, Z3Type> types = new HashMap<>();
    Map<DeclarationNode, Z3Type> declarationNodesTypes = new HashMap<>();

    public void visitMachineNode(MachineNode machineNode) {
        Visitor visitor = new Visitor();
        if (machineNode.getProperties() != null) {
            visitor.visitPredicateNode(machineNode.getProperties(), null);
        }

        if (machineNode.getInitialisation() != null) {
            visitor.visitSubstitutionNode(machineNode.getInitialisation(), null);
        }

        machineNode.getOperations().forEach(op -> visitor.visitSubstitutionNode(op.getSubstitution(), null));

        if (machineNode.getInvariant() != null) {
            visitor.visitPredicateNode(machineNode.getInvariant(), null);
        }

    }

    public void visitPredicateNode(PredicateNode node) {
        Visitor visitor = new Visitor();
        visitor.visitPredicateNode(node, null);
    }

    interface Z3Type {

    }

    enum Basic {
        INTEGER, BOOL
    }

    class Z3BasicType implements Z3Type {
        Basic kind;

        public Z3BasicType(Basic kind) {
            this.kind = kind;
        }
    }

    class Z3SequenceType implements Z3Type {
        Z3Type subtype;

        Z3SequenceType(Z3Type type) {
            subtype = type;
        }

        public Z3Type getSubtype() {
            return this.subtype;
        }
    }

    class Z3SetType implements Z3Type {
        Z3Type subtype;

        Z3SetType(Z3Type type) {
            subtype = type;
        }

        public Z3Type getSubtype() {
            return this.subtype;
        }
    }

    class Z3DeferredType implements Z3Type {
        String name;

        Z3DeferredType(String name) {
            this.name = name;
        }

        public String getSetName() {
            return name;
        }
    }

    class Z3EnumeratedSetType implements Z3Type {
        String name;
        List<String> elements;

        Z3EnumeratedSetType(String name, List<String> elements) {
            this.name = name;
            this.elements = elements;
        }

        public String getSetName() {
            return name;
        }

        public List<String> getElements() {
            return elements;
        }
    }

    class Z3CoupleType implements Z3Type {
        Z3Type left;
        Z3Type right;

        Z3CoupleType(Z3Type left, Z3Type right) {
            this.left = left;
            this.right = right;
        }

        public Z3Type getLeftType() {
            return this.left;
        }

        public Z3Type getRightType() {
            return this.right;
        }
    }

    public Sort getZ3Sort(TypedNode node, Context z3Context) {
        if (declarationNodesTypes.containsKey(node)) {
            Z3Type z3Type = declarationNodesTypes.get(node);
            return getZ3Sort(z3Type, z3Context);
        } else if (types.containsKey(node)) {
            Z3Type z3Type = types.get(node);
            return getZ3Sort(z3Type, z3Context);
        } else {
            return getZ3Sort(convertBTypeToZ3Type(node.getType()), z3Context);
        }
    }

    public Sort getZ3Sort(BType bType, Context z3Context) {
        return getZ3Sort(convertBTypeToZ3Type(bType), z3Context);
    }

    public Sort getZ3Sort(Z3Type t, Context z3Context) {
        if (t instanceof Z3BasicType) {
            switch (((Z3BasicType) t).kind) {
            case INTEGER:
                return z3Context.getIntSort();
            case BOOL:
                return z3Context.getBoolSort();
            default:
                break;
            }

        } else if (t instanceof Z3SetType) {
            Sort subSort = getZ3Sort(((Z3SetType) t).subtype, z3Context);
            return z3Context.mkSetSort(subSort);
        } else if (t instanceof Z3CoupleType) {
            Z3CoupleType couple = (Z3CoupleType) t;
            Sort left = getZ3Sort(couple.left, z3Context);
            Sort right = getZ3Sort(couple.right, z3Context);
            Sort[] subSorts = new Sort[2];
            subSorts[0] = left;
            subSorts[1] = right;
            return z3Context.mkTupleSort(z3Context.mkSymbol("couple"),
                    new Symbol[] { z3Context.mkSymbol("left"), z3Context.mkSymbol("right") }, subSorts);
        } else if (t instanceof Z3SequenceType) {
            Sort subSort = getZ3Sort(((Z3SequenceType) t).subtype, z3Context);
            Sort intType = z3Context.getIntSort();
            Sort[] subSorts = new Sort[2];
            subSorts[0] = z3Context.mkArraySort(intType, subSort);
            subSorts[1] = intType;
            return z3Context.mkTupleSort(z3Context.mkSymbol("sequence"),
                    new Symbol[] { z3Context.mkSymbol("array"), z3Context.mkSymbol("size") }, subSorts);
        } else if (t instanceof Z3DeferredType) {
            return z3Context.mkUninterpretedSort(((Z3DeferredType) t).name);
        } else if (t instanceof Z3EnumeratedSetType) {
            List<String> elements = ((Z3EnumeratedSetType) t).elements;
            String[] array = elements.toArray(new String[elements.size()]);
            return z3Context.mkEnumSort(((Z3EnumeratedSetType) t).name, array);
        }
        throw new AssertionError("Missing Type Conversion: " + t.getClass());
    }

    protected Z3Type updateDeclarationType(DeclarationNode node, Z3Type newZ3Type) {
        if (declarationNodesTypes.containsKey(node)) {
            Z3Type z3Type = declarationNodesTypes.get(node);
            Z3Type unification = unify(z3Type, newZ3Type);
            declarationNodesTypes.put(node, unification);
            return unification;
        } else {
            declarationNodesTypes.put(node, newZ3Type);
            return newZ3Type;
        }
    }

    private Z3Type unify(Z3Type z3Type, Z3Type newZ3Type) {
        if (z3Type instanceof Z3SequenceType && newZ3Type instanceof Z3SetType) {
            return newZ3Type;
        } else if (newZ3Type instanceof Z3SequenceType && z3Type instanceof Z3SetType) {
            return z3Type;
        } else
            return newZ3Type;
    }

    protected Z3Type setZ3Type(TypedNode node, Z3Type subType) {
        types.put(node, subType);
        return subType;
    }

    protected Z3Type getZ3TypeOfNode(TypedNode node) {
        if (types.containsKey(node)) {
            return types.get(node);
        } else {
            return convertBTypeToZ3Type(node.getType());
        }

    }

    protected Z3Type convertBTypeToZ3Type(BType bType) {
        if (bType instanceof IntegerType) {
            return new Z3BasicType(Basic.INTEGER);
        } else if (bType instanceof BoolType) {
            return new Z3BasicType(Basic.BOOL);
        } else if (bType instanceof de.bmoth.parser.ast.types.SetType) {
            BType subtype = ((de.bmoth.parser.ast.types.SetType) bType).getSubType();
            return new Z3SetType(convertBTypeToZ3Type(subtype));
        } else if (bType instanceof CoupleType) {
            de.bmoth.parser.ast.types.CoupleType couple = (de.bmoth.parser.ast.types.CoupleType) bType;
            return new Z3CoupleType(convertBTypeToZ3Type(couple.getLeft()), convertBTypeToZ3Type(couple.getRight()));
        } else if (bType instanceof EnumeratedSetElementType) {
            EnumeratedSetElementType userType = (EnumeratedSetElementType) bType;
            return new Z3EnumeratedSetType(userType.getSetName(), userType.getElements());
        } else if (bType instanceof DeferredSetElementType) {
            DeferredSetElementType deferred = (DeferredSetElementType) bType;
            return new Z3DeferredType(deferred.getSetName());
        }
        throw new AssertionError(bType);
    }

    class Visitor implements AbstractVisitor<Z3Type, Void> {

        @Override
        public Z3Type visitExprOperatorNode(ExpressionOperatorNode node, Void expected) {
            List<ExprNode> arguments = node.getExpressionNodes();
            arguments.forEach(e -> visitExprNode(e, expected));
            switch (node.getOperator()) {
            case FRONT:
            case TAIL:
            case CONCAT:
            case INSERT_TAIL:
            case RESTRICT_FRONT:
            case RESTRICT_TAIL:
                return setZ3Type(node, getZ3TypeOfNode(arguments.get(0)));
            case INSERT_FRONT:
                return setZ3Type(node, getZ3TypeOfNode(arguments.get(1)));
            case SEQ_ENUMERATION:
                return setZ3Type(node, new Z3SequenceType(getZ3TypeOfNode(arguments.get(0))));
            case EMPTY_SEQUENCE:
                de.bmoth.parser.ast.types.SetType setType = (de.bmoth.parser.ast.types.SetType) node.getType();
                de.bmoth.parser.ast.types.CoupleType c = (de.bmoth.parser.ast.types.CoupleType) setType.getSubType();
                return setZ3Type(node, new Z3SequenceType(convertBTypeToZ3Type(c.getRight())));
            default:
                return setZ3Type(node, convertBTypeToZ3Type(node.getType()));
            }
        }

        @Override
        public Z3Type visitIdentifierExprNode(IdentifierExprNode node, Void expected) {
            return setZ3Type(node, convertBTypeToZ3Type(node.getType()));
        }

        @Override
        public Z3Type visitCastPredicateExpressionNode(CastPredicateExpressionNode node, Void expected) {
            return setZ3Type(node, convertBTypeToZ3Type(node.getType()));
        }

        @Override
        public Z3Type visitNumberNode(NumberNode node, Void expected) {
            return setZ3Type(node, convertBTypeToZ3Type(node.getType()));
        }

        @Override
        public Z3Type visitQuantifiedExpressionNode(QuantifiedExpressionNode node, Void expected) {
            AbstractVisitor.super.visitPredicateNode(node.getPredicateNode(), expected);
            if (node.getExpressionNode() != null) {
                visitExprNode(node.getExpressionNode(), expected);
            }
            return setZ3Type(node, convertBTypeToZ3Type(node.getType()));
        }

        @Override
        public Z3Type visitIdentifierPredicateNode(IdentifierPredicateNode node, Void expected) {
            return setZ3Type(node, convertBTypeToZ3Type(node.getType()));
        }

        @Override
        public Z3Type visitPredicateOperatorNode(PredicateOperatorNode node, Void expected) {
            node.getPredicateArguments().forEach(e -> AbstractVisitor.super.visitPredicateNode(e, expected));
            return setZ3Type(node, convertBTypeToZ3Type(node.getType()));
        }

        @Override
        public Z3Type visitPredicateOperatorWithExprArgs(PredicateOperatorWithExprArgsNode node, Void expected) {
            List<ExprNode> arguments = node.getExpressionNodes();
            switch (node.getOperator()) {
            case EQUAL:
            case NOT_EQUAL:
                ExprNode left = arguments.get(0);
                ExprNode right = arguments.get(1);
                if (left instanceof IdentifierExprNode) {
                    return updateDeclarationType(((IdentifierExprNode) left).getDeclarationNode(),
                            visitExprNode(right, expected));
                } else if (right instanceof IdentifierExprNode) {
                    return updateDeclarationType(((IdentifierExprNode) right).getDeclarationNode(),
                            visitExprNode(left, expected));
                } else {
                    break;
                }
            default:
                break;
            }
            arguments.forEach(a -> visitExprNode(a, expected));
            return setZ3Type(node, convertBTypeToZ3Type(node.getType()));
        }

        @Override
        public Z3Type visitQuantifiedPredicateNode(QuantifiedPredicateNode node, Void expected) {
            AbstractVisitor.super.visitPredicateNode(node.getPredicateNode(), expected);
            return setZ3Type(node, convertBTypeToZ3Type(node.getType()));
        }

        @Override
        public Z3Type visitSkipSubstitutionNode(SkipSubstitutionNode node, Void expected) {
            return null;
        }

        @Override
        public Z3Type visitIfSubstitutionNode(IfSubstitutionNode node, Void expected) {
            node.getConditions().forEach(t -> AbstractVisitor.super.visitPredicateNode(t, expected));
            node.getSubstitutions().forEach(t -> visitSubstitutionNode(t, expected));
            if (node.getElseSubstitution() != null) {
                visitSubstitutionNode(node.getElseSubstitution(), expected);
            }
            return null;
        }

        @Override
        public Z3Type visitConditionSubstitutionNode(ConditionSubstitutionNode node, Void expected) {
            AbstractVisitor.super.visitPredicateNode(node.getCondition(), expected);
            visitSubstitutionNode(node.getSubstitution(), expected);
            return null;
        }

        @Override
        public Z3Type visitAnySubstitution(AnySubstitutionNode node, Void expected) {
            AbstractVisitor.super.visitPredicateNode(node.getWherePredicate(), expected);
            visitSubstitutionNode(node.getThenSubstitution(), expected);
            return null;
        }

        @Override
        public Z3Type visitSelectSubstitutionNode(SelectSubstitutionNode node, Void expected) {
            node.getConditions().forEach(t -> AbstractVisitor.super.visitPredicateNode(t, expected));
            node.getSubstitutions().forEach(t -> visitSubstitutionNode(t, expected));
            if (node.getElseSubstitution() != null) {
                visitSubstitutionNode(node.getElseSubstitution(), expected);
            }
            return null;
        }

        @Override
        public Z3Type visitSingleAssignSubstitution(SingleAssignSubstitutionNode node, Void expected) {
            IdentifierExprNode identifier = node.getIdentifier();
            DeclarationNode declarationNode = identifier.getDeclarationNode();
            Z3Type exprType = visitExprNode(node.getValue(), expected);
            return updateDeclarationType(declarationNode, exprType);
        }

        @Override
        public Z3Type visitParallelSubstitutionNode(ParallelSubstitutionNode node, Void expected) {
            node.getSubstitutions().forEach(s -> visitSubstitutionNode(s, null));
            return null;
        }

        @Override
        public Z3Type visitBecomesElementOfSubstitutionNode(BecomesElementOfSubstitutionNode node, Void expected) {
            Z3SetType setType = (Z3SetType) visitExprNode(node.getExpression(), expected);
            Z3Type type = setType.getSubtype();
            if (node.getIdentifiers().size() == 1) {
                updateDeclarationType(node.getIdentifiers().get(0).getDeclarationNode(), type);
            } else {
                List<Z3Type> typesList = new ArrayList<>();
                Z3CoupleType couple = (Z3CoupleType) type;
                while (node.getIdentifiers().size() - 1 > typesList.size()) {
                    typesList.add(0, couple.getRightType());
                    couple = (Z3CoupleType) couple.getLeftType();
                }
                typesList.add(0, couple);
                for (int i = 0; i < node.getIdentifiers().size(); i++) {
                    updateDeclarationType(node.getIdentifiers().get(i).getDeclarationNode(), typesList.get(i));
                }
            }

            return null;
        }

        @Override
        public Z3Type visitBecomesSuchThatSubstitutionNode(BecomesSuchThatSubstitutionNode node, Void expected) {
            AbstractVisitor.super.visitPredicateNode(node.getPredicate(), expected);
            return null;
        }

        @Override
        public Z3Type visitEnumerationSetNode(EnumerationSetNode node, Void expected) {
            return setZ3Type(node, convertBTypeToZ3Type(node.getType()));
        }

        @Override
        public Z3Type visitDeferredSetNode(DeferredSetNode node, Void expected) {
            return setZ3Type(node, convertBTypeToZ3Type(node.getType()));
        }

        @Override
        public Z3Type visitEnumeratedSetElementNode(EnumeratedSetElementNode node, Void expected) {
            return setZ3Type(node, convertBTypeToZ3Type(node.getType()));
        }

        @Override
        public Z3Type visitLTLPrefixOperatorNode(LTLPrefixOperatorNode node, Void expected) {
            throw new AssertionError();
        }

        @Override
        public Z3Type visitLTLKeywordNode(LTLKeywordNode node, Void expected) {
            throw new AssertionError();
        }

        @Override
        public Z3Type visitLTLInfixOperatorNode(LTLInfixOperatorNode node, Void expected) {
            throw new AssertionError();
        }

        @Override
        public Z3Type visitLTLBPredicateNode(LTLBPredicateNode node, Void expected) {
            throw new AssertionError();
        }

    }

}
