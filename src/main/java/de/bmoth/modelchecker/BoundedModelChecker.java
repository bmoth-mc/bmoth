package de.bmoth.modelchecker;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import de.bmoth.backend.z3.Z3SolverFactory;
import de.bmoth.parser.ast.nodes.DeclarationNode;
import de.bmoth.parser.ast.nodes.MachineNode;

import java.util.ArrayList;
import java.util.List;

public class BoundedModelChecker extends ModelChecker<Boolean> {

    private final int maxSteps;
    private final Solver solver;

    public BoundedModelChecker(MachineNode machine, int maxSteps) {
        super(machine);
        this.maxSteps = maxSteps;
        this.solver = Z3SolverFactory.getZ3Solver(getContext());
    }

    @Override
    protected Boolean doModelCheck() {
        for (int k = 0; k < maxSteps; k++) {
            // get a clean solver
            solver.reset();

            // INIT(V0)
            solver.add(init(0));

            // UNION i from 1 to k T(Vi-1, Vi)
            for (int i = 1; i <= k; i++) {
                solver.add(transition(i - 1, i));
            }

            // not INV(Vk)
            solver.add(getContext().mkNot(invariant(k)));

            //TODO add missing UNION i from 1 to k, j from i + 1 to k (Vi != Vj)

            Status check = solver.check();
            if (check == Status.SATISFIABLE) {
                // counter example found!
                return false;
            }
        }

        // no counter example found after k steps
        return true;
    }

    private BoolExpr transformPrimedToStep(Expr original, int step) {
        List<Expr> originalVars = new ArrayList<>();
        List<Expr> substitutedVars = new ArrayList<>();
        getMachineTranslator().getVariables().forEach(node -> {
            Expr originalVar = getMachineTranslator().getPrimedVariable(node);
            originalVars.add(originalVar);
            substitutedVars.add(getContext().mkConst(node.getName() + step + "'", originalVar.getSort()));
        });

        return (BoolExpr) original.substitute(originalVars.toArray(new Expr[0]), substitutedVars.toArray(new Expr[0]));
    }

    private BoolExpr transformUnprimedToStep(Expr original, int step) {
        List<Expr> originalVars = new ArrayList<>();
        List<Expr> substitutedVars = new ArrayList<>();
        getMachineTranslator().getVariables().forEach(node -> {
            Expr originalVar = getMachineTranslator().getVariable(node);
            originalVars.add(originalVar);
            substitutedVars.add(getContext().mkConst(node.getName() + step + "'", originalVar.getSort()));
        });

        return (BoolExpr) original.substitute(originalVars.toArray(new Expr[0]), substitutedVars.toArray(new Expr[0]));
    }

    private BoolExpr init(int step) {
        return transformPrimedToStep(getMachineTranslator().getInitialValueConstraint(), step);
    }

    private BoolExpr transition(int fromStep, int toStep) {
        BoolExpr[] transitions = getMachineTranslator().getOperationConstraints().stream().map(op -> transformPrimedToStep(transformUnprimedToStep(op, fromStep), toStep)).toArray(BoolExpr[]::new);
        return getContext().mkOr(transitions);
    }

    private BoolExpr invariant(int step) {
        return transformUnprimedToStep(getMachineTranslator().getInvariantConstraint(), step);
    }
}
