package de.bmoth.modelchecker;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import de.bmoth.backend.SubstitutionOptions;
import de.bmoth.backend.TranslationOptions;
import de.bmoth.backend.z3.Z3SolverFactory;
import de.bmoth.parser.ast.nodes.MachineNode;

public abstract class SymbolicModelChecker extends ModelChecker {
    protected final int maxSteps;
    protected final Solver baseSolver;

    public SymbolicModelChecker(MachineNode machine, int maxSteps) {
        super(machine);
        this.baseSolver = Z3SolverFactory.getZ3Solver(getContext());
        this.maxSteps = maxSteps;
    }

    protected BoolExpr init() {
        return getMachineTranslator().getInitialValueConstraint(TranslationOptions.PRIMED_0);
    }

    protected BoolExpr transition(int fromStep, int toStep) {
        return getMachineTranslator().getCombinedOperationConstraint(new SubstitutionOptions(new TranslationOptions(toStep), new TranslationOptions(fromStep)));
    }

    protected BoolExpr invariant(int step) {
        return getMachineTranslator().getInvariantConstraint(new TranslationOptions(step));
    }

    protected BoolExpr negatedInvariant(int step) {
        return getMachineTranslator().getNegatedInvariantConstraint(new TranslationOptions(step));
    }

    protected BoolExpr distinctVectors(int to) {
        return getMachineTranslator().getDistinctVars(0, to);
    }

    protected State getStateFromModel(Model model, int step) {
        return getStateFromModel(model, new TranslationOptions(step));
    }
}
