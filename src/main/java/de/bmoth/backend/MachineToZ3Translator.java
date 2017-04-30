package de.bmoth.backend;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;

import de.bmoth.parser.ast.nodes.DeclarationNode;
import de.bmoth.parser.ast.nodes.MachineNode;

public class MachineToZ3Translator {
    private final MachineNode machineNode;
    private final Context z3Context;
    private final FormulaToZ3Translator z3Translator;

    public MachineToZ3Translator(MachineNode machineNode, Context ctx) {
        this.machineNode = machineNode;
        this.z3Context = ctx;
        this.z3Translator = new FormulaToZ3Translator(ctx);
    }

    public BoolExpr getInitialValueConstraint() {

        return null;
    }

    public Expr getVariableAsZ3Expr(String name) {
        for (DeclarationNode declNode : machineNode.getVariables()) {
            if (declNode.getName().equals(name)) {
                Expr variable = z3Context.mkConst(declNode.getName(), z3Translator.bTypeToZ3Sort(declNode.getType()));
            }
        }
        throw new AssertionError("Unknown variable: " + name);
    }
}
