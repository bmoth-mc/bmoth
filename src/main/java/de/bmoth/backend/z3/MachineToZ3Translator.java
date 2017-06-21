package de.bmoth.backend.z3;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Sort;

import de.bmoth.backend.TranslationOptions;
import de.bmoth.parser.ast.nodes.*;
import de.bmoth.parser.ast.visitors.SubstitutionVisitor;

import java.util.*;

public class MachineToZ3Translator {
    private final MachineNode machineNode;
    private final Context z3Context;
    private BoolExpr initialisationConstraint = null;
    private BoolExpr invariantConstraint = null;
    private final HashMap<String, String> primedVariablesToVariablesMap;
    private final List<BoolExpr> operationConstraints;
    private final SubstitutionToZ3TranslatorVisitor visitor;
    private final Z3TypeInference z3TypeInference;

    public MachineToZ3Translator(MachineNode machineNode, Context ctx) {
        this.machineNode = machineNode;
        this.z3Context = ctx;
        this.visitor = new SubstitutionToZ3TranslatorVisitor();
        this.z3TypeInference = new Z3TypeInference();
        z3TypeInference.visitMachineNode(machineNode);

        BoolExpr initialization = null;
        BoolExpr properties = null;

        if (machineNode.getInitialisation() != null) {
            initialization = visitor.visitSubstitutionNode(machineNode.getInitialisation(), null);
        }
        if (machineNode.getProperties() != null) {
            properties = FormulaToZ3Translator.translatePredicate(machineNode.getProperties(), z3Context, z3TypeInference);
        }

        if (initialization != null && properties != null) {
            this.initialisationConstraint = z3Context.mkAnd(initialization, properties);
        } else if (initialization != null) {
            this.initialisationConstraint = initialization;
        } else {
            this.initialisationConstraint = properties;
        }

        if (machineNode.getInvariant() != null) {
            this.invariantConstraint = FormulaToZ3Translator.translatePredicate(machineNode.getInvariant(), z3Context,
                    z3TypeInference);
        } else {
            this.invariantConstraint = z3Context.mkTrue();
        }

        this.operationConstraints = visitOperations(machineNode.getOperations());

        primedVariablesToVariablesMap = new HashMap<>();
        for (DeclarationNode node : machineNode.getVariables()) {
            primedVariablesToVariablesMap.put(getPrimedName(node.getName()), node.getName());
        }
    }

    private List<BoolExpr> visitOperations(List<OperationNode> operations) {
        List<BoolExpr> results = new ArrayList<>(operations.size());
        for (OperationNode operationNode : this.machineNode.getOperations()) {
            BoolExpr temp = visitor.visitSubstitutionNode(operationNode.getSubstitution(), null);
            // for unassigned variables add a dummy assignment, e.g. x' = x
            Set<DeclarationNode> set = new HashSet<>(this.getVariables());
            set.removeAll(operationNode.getSubstitution().getAssignedVariables());
            List<BoolExpr> dummyAssignments = createDummyAssignment(set);
            dummyAssignments.add(0, temp);
            BoolExpr[] array = dummyAssignments.toArray(new BoolExpr[dummyAssignments.size()]);
            results.add(z3Context.mkAnd(array));
        }
        return results;
    }

    protected List<BoolExpr> createDummyAssignment(Set<DeclarationNode> unassignedVariables) {
        List<BoolExpr> list = new ArrayList<>();
        for (DeclarationNode node : unassignedVariables) {
            BoolExpr mkEq = z3Context.mkEq(getPrimedVariable(node), getVariableAsZ3Expression(node));
            list.add(mkEq);
        }
        return list;
    }

    public List<DeclarationNode> getVariables() {
        return machineNode.getVariables();
    }

    public List<DeclarationNode> getConstants() {
        return machineNode.getConstants();
    }

    public Expr getVariableAsZ3Expression(DeclarationNode node) {
        Sort type = z3TypeInference.getZ3Sort(node, z3Context);
        return z3Context.mkConst(node.getName(), type);
    }

    public Expr getVariable(DeclarationNode node) {
        Sort type = z3TypeInference.getZ3Sort(node, z3Context);
        return z3Context.mkConst(node.getName(), type);
    }

    public Expr getPrimedVariable(DeclarationNode node) {
        String primedName = getPrimedName(node.getName());
        Sort type = z3TypeInference.getZ3Sort(node, z3Context);
        return z3Context.mkConst(primedName, type);
    }

    public BoolExpr getInitialValueConstraint() {
        return initialisationConstraint;
    }

    public BoolExpr getInvariantConstraint() {
        return invariantConstraint;
    }

    class SubstitutionToZ3TranslatorVisitor implements SubstitutionVisitor<BoolExpr, TranslationOptions> {

        private static final String CURRENTLY_NOT_SUPPORTED = "Currently not supported";

        @Override
        public BoolExpr visitAnySubstitution(AnySubstitutionNode node, TranslationOptions ops) {
            Expr[] parameters = new Expr[node.getParameters().size()];
            for (int i = 0; i < parameters.length; i++) {
                parameters[i] = getVariableAsZ3Expression(node.getParameters().get(i));
            }
            BoolExpr parameterConstraints = FormulaToZ3Translator.translatePredicate(node.getWherePredicate(),
                    z3Context, z3TypeInference);
            BoolExpr transition = visitSubstitutionNode(node.getThenSubstitution(), ops);
            BoolExpr existsBody = z3Context.mkAnd(parameterConstraints, transition);
            return z3Context.mkExists(parameters, existsBody, parameters.length, null, null, null, null);
        }

        @Override
        public BoolExpr visitSelectSubstitutionNode(SelectSubstitutionNode node, TranslationOptions ops) {
            if (node.getConditions().size() > 1 || node.getElseSubstitution() != null) {
                throw new AssertionError(CURRENTLY_NOT_SUPPORTED);
            }
            BoolExpr condition = FormulaToZ3Translator.translatePredicate(node.getConditions().get(0), z3Context,
                    z3TypeInference);
            BoolExpr substitution = visitSubstitutionNode(node.getSubstitutions().get(0), ops);
            return z3Context.mkAnd(condition, substitution);
        }

        @Override
        public BoolExpr visitSingleAssignSubstitution(SingleAssignSubstitutionNode node, TranslationOptions ops) {
            String name = getPrimedName(node.getIdentifier().getName());
            return FormulaToZ3Translator.translateVariableEqualToExpr(name, node.getValue(), z3Context,
                    z3TypeInference);
        }

        @Override
        public BoolExpr visitParallelSubstitutionNode(ParallelSubstitutionNode node, TranslationOptions ops) {
            List<SubstitutionNode> substitutions = node.getSubstitutions();
            BoolExpr boolExpr = null;
            for (SubstitutionNode substitutionNode : substitutions) {
                BoolExpr temp = visitSubstitutionNode(substitutionNode, ops);
                if (boolExpr == null) {
                    boolExpr = temp;
                } else {
                    boolExpr = z3Context.mkAnd(boolExpr, temp);
                }
            }
            return boolExpr;
        }

        @Override
        public BoolExpr visitConditionSubstitutionNode(ConditionSubstitutionNode node, TranslationOptions ops) {
            // PRE and ASSERT
            BoolExpr condition = FormulaToZ3Translator.translatePredicate(node.getCondition(), z3Context,
                    z3TypeInference);
            BoolExpr substitution = visitSubstitutionNode(node.getSubstitution(), ops);
            return z3Context.mkAnd(condition, substitution);
        }

        @Override
        public BoolExpr visitBecomesElementOfSubstitutionNode(BecomesElementOfSubstitutionNode node,
                TranslationOptions ops) {
            if (node.getIdentifiers().size() > 1) {
                throw new AssertionError(CURRENTLY_NOT_SUPPORTED);
            }
            IdentifierExprNode identifierExprNode = node.getIdentifiers().get(0);
            String name = getPrimedName(identifierExprNode.getName());
            return FormulaToZ3Translator.translateVariableElementOfSetExpr(name,
                    identifierExprNode.getDeclarationNode(), node.getExpression(), z3Context, new TranslationOptions(),
                    z3TypeInference);
        }

        @Override
        public BoolExpr visitBecomesSuchThatSubstitutionNode(BecomesSuchThatSubstitutionNode node,
                TranslationOptions ops) {
            throw new AssertionError(CURRENTLY_NOT_SUPPORTED);
        }

        @Override
        public BoolExpr visitIfSubstitutionNode(IfSubstitutionNode node, TranslationOptions ops) {
            if (node.getConditions().size() > 1) {
                // ELSIF THEN ...
                throw new AssertionError(CURRENTLY_NOT_SUPPORTED);
            }
            BoolExpr condition = FormulaToZ3Translator.translatePredicate(node.getConditions().get(0), z3Context,
                    z3TypeInference);
            BoolExpr substitution = visitSubstitutionNode(node.getSubstitutions().get(0), ops);
            List<BoolExpr> ifThenList = new ArrayList<>();
            ifThenList.add(condition);
            ifThenList.add(substitution);
            Set<DeclarationNode> set = new HashSet<>(node.getAssignedVariables());
            set.removeAll(node.getSubstitutions().get(0).getAssignedVariables());
            ifThenList.addAll(createDummyAssignment(set));
            BoolExpr ifThen = z3Context.mkAnd(ifThenList.toArray(new BoolExpr[ifThenList.size()]));

            BoolExpr elseExpr = null;
            List<BoolExpr> elseList = new ArrayList<>();
            elseList.add(z3Context.mkNot(condition));
            if (null == node.getElseSubstitution()) {
                elseList.addAll(createDummyAssignment(node.getAssignedVariables()));
            } else {
                elseList.add(visitSubstitutionNode(node.getElseSubstitution(), ops));
                Set<DeclarationNode> elseDummies = new HashSet<>(node.getAssignedVariables());
                elseDummies.removeAll(node.getElseSubstitution().getAssignedVariables());
                elseList.addAll(createDummyAssignment(elseDummies));
            }
            elseExpr = z3Context.mkAnd(elseList.toArray(new BoolExpr[elseList.size()]));
            return z3Context.mkOr(ifThen, elseExpr);
        }

        @Override
        public BoolExpr visitSkipSubstitutionNode(SkipSubstitutionNode node, TranslationOptions expected) {
            return z3Context.mkBool(true);
        }

    }

    private String getPrimedName(String name) {
        return name + "'";
    }

    public List<BoolExpr> getOperationConstraints() {
        return operationConstraints;
    }
}
