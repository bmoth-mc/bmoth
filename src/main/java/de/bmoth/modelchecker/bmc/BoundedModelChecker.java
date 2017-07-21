package de.bmoth.modelchecker.bmc;

import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import de.bmoth.backend.z3.Z3SolverFactory;
import de.bmoth.modelchecker.ModelCheckingResult;
import de.bmoth.modelchecker.State;
import de.bmoth.modelchecker.SymbolicModelChecker;
import de.bmoth.parser.ast.nodes.MachineNode;

public class BoundedModelChecker extends SymbolicModelChecker {
    private final Solver solver;

    public BoundedModelChecker(MachineNode machine, int maxSteps) {
        super(machine, maxSteps);
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
}
