package de.bmoth.modelchecker.kind;

import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import de.bmoth.backend.z3.Z3SolverFactory;
import de.bmoth.modelchecker.ModelCheckingResult;
import de.bmoth.modelchecker.State;
import de.bmoth.modelchecker.SymbolicModelChecker;
import de.bmoth.parser.ast.nodes.MachineNode;

import static de.bmoth.modelchecker.ModelCheckingResult.*;

public class KInductionModelChecker extends SymbolicModelChecker {

    private final Solver stepSolver;

    public KInductionModelChecker(MachineNode machine, int maxSteps) {
        super(machine, maxSteps);
        this.stepSolver = Z3SolverFactory.getZ3Solver(getContext());
    }

    @Override
    protected ModelCheckingResult doModelCheck() {
        for (int k = 0; k < maxSteps; k++) {
            // get a clean baseSolver
            baseSolver.reset();

            // INIT(V0)
            baseSolver.add(init());

            // CONJUNCTION i from 1 to k T(Vi-1, Vi)
            for (int i = 1; i <= k; i++) {
                baseSolver.add(transition(i - 1, i));
            }

            // not INV(Vk)
            baseSolver.add(getContext().mkNot(invariant(k)));

            Status check = baseSolver.check();
            if (check == Status.SATISFIABLE) {
                // counter example found!
                State counterExample = getStateFromModel(baseSolver.getModel(), k);
                return createCounterExampleFound(k, counterExample);
            } else {
                stepSolver.reset();

                stepSolver.add();
                // CONJUNCTION i from 1 to k, j from i + 1 to k (Vi != Vj)
                stepSolver.add(distinctVectors(k));

                for (int i = 0; i <= k; i++) {
                    stepSolver.add(transition(i - 1, i));
                }
                for (int i = 0; i <= k; i++) {
                    stepSolver.add(invariant(i));
                }
                stepSolver.add(getContext().mkNot(invariant(k + 1)));

                Status checkStep = stepSolver.check();

                if (checkStep == Status.UNSATISFIABLE)
                    // TODO think about state space root!
                    return createVerified(k, null);
            }
        }

        // no counter example found after maxStep steps
        return createExceededMaxSteps(maxSteps);
    }
}
