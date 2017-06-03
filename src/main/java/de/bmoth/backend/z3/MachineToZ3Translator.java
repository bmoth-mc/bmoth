package de.bmoth.backend.z3;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Sort;

import de.bmoth.backend.TranslationOptions;
import de.bmoth.parser.ast.SubstitutionVisitor;
import de.bmoth.parser.ast.nodes.*;

import java.util.*;

public class MachineToZ3Translator {
    private final MachineNode machineNode;
    private final Context z3Context;
    private BoolExpr initialisationConstraint = null;
    private BoolExpr invariantConstraint = null;
    private final HashMap<String, String> primedVariablesToVariablesMap;
    private final List<BoolExpr> operationConstraints;
    private final SubstitutionToZ3TranslatorVisitor visitor;

    public MachineToZ3Translator(MachineNode machineNode, Context ctx) {
        this.machineNode = machineNode;
        this.z3Context = ctx;
        this.visitor = new SubstitutionToZ3TranslatorVisitor();

        if (machineNode.getInitialisation() != null) {
            this.initialisationConstraint = visitor.visitSubstitutionNode(machineNode.getInitialisation(), null);
        }
        if (machineNode.getInvariant() != null) {
            this.invariantConstraint = FormulaToZ3Translator.translatePredicate(machineNode.getInvariant(), z3Context);
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
            BoolExpr temp = visitor.

                    visitSubstitutionNode(operationNode.getSubstitution(), null);
            // for unassigned variables add a dummy assignment, e.g. x' = x
            Set<DeclarationNode> set = new HashSet<>(this.getVariables());
            set.removeAll(operationNode.getSubstitution().getAssignedVariables());
            for (DeclarationNode node : set) {
                BoolExpr mkEq = z3Context.mkEq(getPrimedVariable(node), getVariableAsZ3Expression(node));
                temp = z3Context.mkAnd(temp, mkEq);
            }
            results.add(temp);
        }
        return results;
    }

    public List<DeclarationNode> getVariables() {
        return machineNode.getVariables();
    }

    public List<DeclarationNode> getConstants() {
        return machineNode.getConstants();
    }

    public Expr getVariableAsZ3Expression(DeclarationNode node) {
        Sort type = FormulaToZ3Translator.bTypeToZ3Sort(z3Context, node.getType());
        return z3Context.mkConst(node.getName(), type);
    }

    public Expr getVariable(DeclarationNode node) {
        Sort type = FormulaToZ3Translator.bTypeToZ3Sort(z3Context, node.getType());
        return z3Context.mkConst(node.getName(), type);
    }

    public Expr getPrimedVariable(DeclarationNode node) {
        String primedName = getPrimedName(node.getName());
        Sort type = FormulaToZ3Translator.bTypeToZ3Sort(z3Context, node.getType());
        return z3Context.mkConst(primedName, type);
    }

    public BoolExpr getInitialValueConstraint() {
        PredicateNode properties = machineNode.getProperties();
        BoolExpr prop = z3Context.mkTrue();
        if (properties != null) {
            prop = FormulaToZ3Translator.translatePredicate(machineNode.getProperties(), z3Context);

        }
        if (initialisationConstraint == null) {
            return prop;
        }
        return z3Context.mkAnd(initialisationConstraint, prop);
    }

    public BoolExpr getInvariantConstraint() {
        return invariantConstraint;
    }

    class SubstitutionToZ3TranslatorVisitor implements SubstitutionVisitor<BoolExpr, TranslationOptions> {

        @Override
        public BoolExpr visitAnySubstitution(AnySubstitutionNode node, TranslationOptions ops) {
            Expr[] parameters = new Expr[node.getParameters().size()];
            for (int i = 0; i < parameters.length; i++) {
                parameters[i] = getVariableAsZ3Expression(node.getParameters().get(i));
            }
            BoolExpr parameterConstraints = FormulaToZ3Translator.translatePredicate(node.getWherePredicate(),
                    z3Context);
            BoolExpr transition = visitSubstitutionNode(node.getThenSubstitution(), ops);
            BoolExpr existsBody = z3Context.mkAnd(parameterConstraints, transition);
            return z3Context.mkExists(parameters, existsBody, parameters.length, null, null, null, null);
        }

        @Override
        public BoolExpr visitSelectSubstitutionNode(SelectSubstitutionNode node, TranslationOptions ops) {
            BoolExpr condition = FormulaToZ3Translator.translatePredicate(node.getCondition(), z3Context);
            BoolExpr substitution = visitSubstitutionNode(node.getSubstitution(), ops);
            return z3Context.mkAnd(condition, substitution);
        }

        @Override
        public BoolExpr visitSingleAssignSubstitution(SingleAssignSubstitutionNode node, TranslationOptions ops) {
            String name = getPrimedName(node.getIdentifier().getName());
            return FormulaToZ3Translator.translateVariableEqualToExpr(name, node.getValue(), z3Context);
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
        public BoolExpr visitBecomesElementOfSubstitutionNode(BecomesElementOfSubstitutionNode node,
                TranslationOptions ops) {
            throw new AssertionError("Currently not supported");
        }

        @Override
        public BoolExpr visitBecomesSuchThatSubstitutionNode(BecomesSuchThatSubstitutionNode node,
                TranslationOptions ops) {
            throw new AssertionError("Currently not supported");
        }

    }

    private String getPrimedName(String name) {
        return name + "'";
    }

    public List<BoolExpr> getOperationConstraints() {
        return operationConstraints;
    }
}
