package de.bmoth.backend;

import java.util.HashMap;
import java.util.List;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Sort;

import de.bmoth.parser.ast.nodes.AnySubstitutionNode;
import de.bmoth.parser.ast.nodes.DeclarationNode;
import de.bmoth.parser.ast.nodes.MachineNode;
import de.bmoth.parser.ast.nodes.ParallelSubstitutionNode;
import de.bmoth.parser.ast.nodes.SingleAssignSubstitutionNode;
import de.bmoth.parser.ast.nodes.SubstitutionNode;

public class MachineToZ3Translator {
    private final MachineNode machineNode;
    private final Context z3Context;
    private final FormulaToZ3Translator formulaTranslator;
    private final BoolExpr initialisationConstraint;
    private final BoolExpr invariantConstraint;
    private final HashMap<String, String> primedVariablesToVariablesMap;

    public MachineToZ3Translator(MachineNode machineNode, Context ctx) {
        this.machineNode = machineNode;
        this.z3Context = ctx;
        this.formulaTranslator = new FormulaToZ3Translator(ctx);
        this.initialisationConstraint = visitSubstitution(machineNode.getInitialisation());
        this.invariantConstraint = (BoolExpr) formulaTranslator.visitPredicateNode(machineNode.getInvariant(), null);

        {
            primedVariablesToVariablesMap = new HashMap<>();
            for (DeclarationNode node : machineNode.getVariables()) {
                primedVariablesToVariablesMap.put(getPrimedName(node.getName()), node.getName());
            }
        }

    }

    public List<DeclarationNode> getVariables() {
        return machineNode.getVariables();
    }

    public Expr getPrimedVariable(DeclarationNode node) {
        String primedName = getPrimedName(node.getName());
        Sort type = formulaTranslator.bTypeToZ3Sort(node.getType());
        Expr expr = z3Context.mkConst(primedName, type);
        return expr;
    }

    public BoolExpr getInitialValueConstraint() {
        return initialisationConstraint;
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
        }
        throw new AssertionError("Not implemented" + node.getClass());
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
        FormulaToZ3Translator translator = new FormulaToZ3Translator(this.z3Context);
        Sort bTypeToZ3Sort = translator.bTypeToZ3Sort(node.getIdentifier().getType());
        Expr value = translator.visitExprNode(node.getValue(), null);
        String name = getPrimedName(node.getIdentifier().getName());
        Expr variable = z3Context.mkConst(name, bTypeToZ3Sort);
        return this.z3Context.mkEq(variable, value);
    }

    private String getPrimedName(String name) {
        return name + "'";
    }

}
