package de.bmoth.modelchecker.bmc;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import de.bmoth.backend.SubstitutionOptions;
import de.bmoth.backend.TranslationOptions;
import de.bmoth.backend.z3.Z3SolverFactory;
import de.bmoth.modelchecker.ModelChecker;
import de.bmoth.modelchecker.ModelCheckingResult;
import de.bmoth.modelchecker.State;
import de.bmoth.parser.ast.nodes.MachineNode;

public class BoundedModelChecker extends ModelChecker<ModelCheckingResult> {

    private final int maxSteps;
    private final Solver solver;

    public BoundedModelChecker(MachineNode machine, int maxSteps) {
        super(machine);
        this.maxSteps = maxSteps;
        this.solver = Z3SolverFactory.getZ3Solver(getContext());
    }

    @Override
    protected ModelCheckingResult doModelCheck() {
        for (int k = 0; k < maxSteps; k++) {
            // get a clean solver
            solver.reset();

            // INIT(V0)
            solver.add(init());

            // CONJUNCTION i from 1 to k T(Vi-1, Vi)
            for (int i = 1; i <= k; i++) {
                solver.add(transition(i - 1, i));
            }

            // not INV(Vk)
            solver.add(getContext().mkNot(invariant(k)));

            // CONJUNCTION i from 1 to k, j from i + 1 to k (Vi != Vj)
            solver.add(distinctVectors(k));

            Status check = solver.check();
            if (check == Status.SATISFIABLE) {
                // counter example found!
                State counterExample = getStateFromModel(solver.getModel(), k);
                return ModelCheckingResult.createCounterExampleFound(k, counterExample);
            }
        }

        // no counter example found after maxStep steps
        return ModelCheckingResult.createExceededMaxSteps(maxSteps);
    }

    private BoolExpr init() {
        // step is always 0 for init, so omitting var...
        return getMachineTranslator().getInitialValueConstraint(TranslationOptions.PRIMED_0);
    }

    private BoolExpr transition(int fromStep, int toStep) {
        return getMachineTranslator().getCombinedOperationConstraint(new SubstitutionOptions(new TranslationOptions(toStep), new TranslationOptions(fromStep)));
    }

    private BoolExpr invariant(int step) {
        return getMachineTranslator().getInvariantConstraint(new TranslationOptions(step));
    }

    private BoolExpr distinctVectors(int to) {
        return getMachineTranslator().getDistinctVars(0, to);
    }

    private State getStateFromModel(Model model, int step) {
        return getStateFromModel(null, model, new TranslationOptions(step));
    }
}
