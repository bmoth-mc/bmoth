package de.bmoth.backend;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Sort;
import de.bmoth.parser.ast.nodes.*;

import java.util.*;

public class MachineToZ3Translator {
    private final MachineNode machineNode;
    private final Context z3Context;
    private BoolExpr initialisationConstraint = null;
    private BoolExpr invariantConstraint = null;
    private final HashMap<String, String> primedVariablesToVariablesMap;
    private final List<BoolExpr> operationConstraints;

    public MachineToZ3Translator(MachineNode machineNode, Context ctx) {
        this.machineNode = machineNode;
        this.z3Context = ctx;

        if (machineNode.getInitialisation() != null) {
            this.initialisationConstraint = visitSubstitution(machineNode.getInitialisation());
        }
        if (machineNode.getInvariant() != null) {
            this.invariantConstraint = (BoolExpr) FormulaToZ3Translator.translatePredicate(machineNode.getInvariant(),
                z3Context);
        } else {
            this.invariantConstraint = z3Context.mkTrue();
        }
        
        this.operationConstraints = visitOperations(machineNode.getOperations());

        {
            primedVariablesToVariablesMap = new HashMap<>();
            for (DeclarationNode node : machineNode.getVariables()) {
                primedVariablesToVariablesMap.put(getPrimedName(node.getName()), node.getName());
            }
        }

    }

    private List<BoolExpr> visitOperations(List<OperationNode> operations) {
        List<BoolExpr> results = new ArrayList<>(operations.size());
        for (OperationNode operationNode : this.machineNode.getOperations()) {
            BoolExpr temp = visitSubstitution(operationNode.getSubstitution());
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
        Expr expr = z3Context.mkConst(node.getName(), type);
        return expr;
    }

    public Expr getPrimedVariable(DeclarationNode node) {
        String primedName = getPrimedName(node.getName());
        Sort type = FormulaToZ3Translator.bTypeToZ3Sort(z3Context, node.getType());
        Expr expr = z3Context.mkConst(primedName, type);
        return expr;
    }

    public BoolExpr getInitialValueConstraint() {
        PredicateNode properties = machineNode.getProperties();
        BoolExpr prop = z3Context.mkTrue();
        if (properties != null) {
            prop = FormulaToZ3Translator.translatePredicate(machineNode.getProperties(), z3Context,
                new TranslationOptions(1));

        }
        if (initialisationConstraint == null) {
            return prop;
        }
        return z3Context.mkAnd(initialisationConstraint, prop);
    }

    public BoolExpr getInvariantConstraint() {
        return invariantConstraint;
    }

    private BoolExpr visitSubstitution(SubstitutionNode node) {
        if (node instanceof SingleAssignSubstitutionNode) {
            return visitSingleAssignSubstitution((SingleAssignSubstitutionNode) node);
        } else if (node instanceof ParallelSubstitutionNode) {
            return visitParallelSubstitution((ParallelSubstitutionNode) node);
        } else if (node instanceof AnySubstitutionNode) {
            return visitAnySubstitution((AnySubstitutionNode) node);
        } else if (node instanceof SelectSubstitutionNode) {
            return visitSelectSubstitutionNode((SelectSubstitutionNode) node);
        }
        throw new AssertionError("Not implemented" + node.getClass());
    }

    private BoolExpr visitSelectSubstitutionNode(SelectSubstitutionNode node) {
        BoolExpr condition = (BoolExpr) FormulaToZ3Translator.translatePredicate(node.getCondition(), z3Context);
        BoolExpr substitution = visitSubstitution(node.getSubstitution());
        return z3Context.mkAnd(condition, substitution);
    }

    private BoolExpr visitAnySubstitution(AnySubstitutionNode node) {
        throw new AssertionError("Not implemented: " + node.getClass());// TODO
    }

    private BoolExpr visitParallelSubstitution(ParallelSubstitutionNode node) {
        List<SubstitutionNode> substitutions = node.getSubstitutions();
        BoolExpr boolExpr = null;
        for (SubstitutionNode substitutionNode : substitutions) {
            BoolExpr temp = visitSubstitution(substitutionNode);
            if (boolExpr == null) {
                boolExpr = temp;
            } else {
                boolExpr = z3Context.mkAnd(boolExpr, temp);
            }
        }
        return boolExpr;
    }

    private BoolExpr visitSingleAssignSubstitution(SingleAssignSubstitutionNode node) {
        Sort bTypeToZ3Sort = FormulaToZ3Translator.bTypeToZ3Sort(z3Context, node.getIdentifier().getType());
        Expr value = FormulaToZ3Translator.translateExpr(node.getValue(), z3Context);
        String name = getPrimedName(node.getIdentifier().getName());
        Expr variable = z3Context.mkConst(name, bTypeToZ3Sort);
        return this.z3Context.mkEq(variable, value);
    }

    private String getPrimedName(String name) {
        return name + "'";
    }

    public List<BoolExpr> getOperationConstraints() {
        return operationConstraints;
    }
}
