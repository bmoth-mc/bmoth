package de.bmoth.modelchecker.kind;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import de.bmoth.backend.SubstitutionOptions;
import de.bmoth.backend.TranslationOptions;
import de.bmoth.backend.z3.Z3SolverFactory;
import de.bmoth.modelchecker.ModelChecker;
import de.bmoth.modelchecker.State;
import de.bmoth.parser.ast.nodes.MachineNode;

public class KInductionModelChecker extends ModelChecker<KInductionModelCheckingResult> {

    private final int maxSteps;
    private final Solver baseSolver;
    private final Solver stepSolver;

    public KInductionModelChecker(MachineNode machine, int maxSteps) {
        super(machine);
        this.maxSteps = maxSteps;
        this.baseSolver = Z3SolverFactory.getZ3Solver(getContext());
        this.stepSolver = Z3SolverFactory.getZ3Solver(getContext());
    }

    @Override
    protected KInductionModelCheckingResult doModelCheck() {
        for (int k = 0; k < maxSteps; k++) {
            // get a clean baseSolver
            baseSolver.reset();

            // INIT(V0)
            baseSolver.add(init(0));

            // CONJUNCTION i from 1 to k T(Vi-1, Vi)
            for (int i = 1; i <= k; i++) {
                baseSolver.add(transition(i - 1, i));
            }

            // not INV(Vk)
            baseSolver.add(getContext().mkNot(invariant(k)));

            //TODO add missing CONJUNCTION i from 1 to k, j from i + 1 to k (Vi != Vj)

            Status check = baseSolver.check();
            if (check == Status.SATISFIABLE) {
                // counter example found!
                State counterExample = getStateFromModel(baseSolver.getModel(), k);
                return KInductionModelCheckingResult.createCounterExampleFound(counterExample, k);
            } else {
                stepSolver.reset();

                stepSolver.add();

                for (int i = 0; i <= k; i++) {
                    stepSolver.add(transition(i - 1, i));
                }
                for (int i = 0; i <= k; i++) {
                    stepSolver.add(invariant(i));
                }
                stepSolver.add(getContext().mkNot(invariant(k + 1)));

                Status checkStep = stepSolver.check();

                if (checkStep == Status.UNSATISFIABLE)
                    return KInductionModelCheckingResult.createVerfifiedViaInduction(k);
            }
        }

        // no counter example found after maxStep steps
        return KInductionModelCheckingResult.createExceededMaxSteps(maxSteps);
    }

    private BoolExpr init(int step) {
        return getMachineTranslator().getInitialValueConstraint(TranslationOptions.PRIMED_0);
    }

    private BoolExpr transition(int fromStep, int toStep) {
        return getMachineTranslator().getCombinedOperationConstraint(new SubstitutionOptions(new TranslationOptions(toStep), new TranslationOptions(fromStep)));
    }

    private BoolExpr invariant(int step) {
        return getMachineTranslator().getInvariantConstraint(new TranslationOptions(step));
    }

    private State getStateFromModel(Model model, int step) {
        return getStateFromModel(null, model, new TranslationOptions(step));
    }
}
