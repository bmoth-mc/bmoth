package de.bmoth.modelchecker.kind;

import com.microsoft.z3.*;
import de.bmoth.backend.TranslationOptions;
import de.bmoth.backend.z3.Z3SolverFactory;
import de.bmoth.modelchecker.ModelChecker;
import de.bmoth.modelchecker.State;
import de.bmoth.parser.ast.nodes.DeclarationNode;
import de.bmoth.parser.ast.nodes.MachineNode;

import java.util.*;

public class KinductionModelChecker extends ModelChecker<KinductionModelCheckingResult> {

    private final int maxSteps;
    private final Solver solver;
    private final Expr[] originalVars;
    private final Expr[] primedVars;
    private final Map<Expr, String> primedVarToOriginalName;

    public KinductionModelChecker(MachineNode machine, int maxSteps) {
        super(machine);
        this.maxSteps = maxSteps;
        this.solver = Z3SolverFactory.getZ3Solver(getContext());
        this.originalVars = getMachineTranslator().getVariables().stream().map(var -> getMachineTranslator().getVariable(var)).toArray(Expr[]::new);
        this.primedVarToOriginalName = new HashMap<>();
        this.primedVars = getMachineTranslator().getVariables().stream().map(var -> {
            Expr primedVar = getMachineTranslator().getPrimedVariable(var, TranslationOptions.PRIMED_0);
            primedVarToOriginalName.put(primedVar, var.getName());
            return primedVar;
        }).toArray(Expr[]::new);
    }

    @Override
    protected KinductionModelCheckingResult doModelCheck() {
        for (int k = 0; k < maxSteps; k++) {
            // get a clean solver
            solver.reset();

            // INIT(V0)
            solver.add(init(0));

            // CONJUNCTION i from 1 to k T(Vi-1, Vi)
            for (int i = 1; i <= k; i++) {
                solver.add(transition(i - 1, i));
            }

            // not INV(Vk)
            solver.add(getContext().mkNot(invariant(k)));

            //TODO add missing CONJUNCTION i from 1 to k, j from i + 1 to k (Vi != Vj)

            Status check = solver.check();
            if (check == Status.SATISFIABLE) {
                // counter example found!
                State counterExample = getStateFromModel(solver.getModel(), k);
                return KinductionModelCheckingResult.createCounterExampleFound(counterExample, k);
            } else {


                for (int i = 0; i <= k; i++) {
                    solver.add(transition(i - 1, i));
                }
                for (int i = 0; i <= k; i++) {
                    solver.add(invariant(i));
                }
                solver.add(getContext().mkNot(invariant(k+1)));
            }
        }

        // no counter example found after maxStep steps
        return KinductionModelCheckingResult.createExceededMaxSteps(maxSteps);
    }

    private BoolExpr init(int step) {
        return (BoolExpr) transformToStep(getMachineTranslator().getInitialValueConstraint(), step, primedVars);
    }

    private BoolExpr transition(int fromStep, int toStep) {
        BoolExpr[] transitions = getMachineTranslator().getOperationConstraints().stream().map(
            op -> transformToStep(transformToStep(op, fromStep, originalVars), toStep, primedVars)
        ).toArray(BoolExpr[]::new);

        switch (transitions.length) {
            case 0:
                return getContext().mkTrue();
            case 1:
                return transitions[0];
            default:
                return getContext().mkOr(transitions);
        }
    }

    private BoolExpr invariant(int step) {
        return (BoolExpr) transformToStep(getMachineTranslator().getInvariantConstraint(), step, originalVars);
    }

    private Expr transformToStep(Expr original, int step, Expr[] variables) {
        Expr[] substitutions = Arrays.stream(variables).map(
            var -> {
                String name = primedVarToOriginalName.containsKey(var) ? primedVarToOriginalName.get(var) : var.getFuncDecl().getName().toString();
                return getContext().mkConst(
                    name + step + "'", var.getSort());
            }
        ).toArray(Expr[]::new);
        return original.substitute(variables, substitutions);
    }

    private State getStateFromModel(Model model, int step) {
        HashMap<String, Expr> map = new HashMap<>();
        for (DeclarationNode declNode : getMachineTranslator().getVariables()) {
            Expr expr = transformToStep(getMachineTranslator().getVariable(declNode), step, originalVars);
            Expr value = model.eval(expr, true);
            map.put(declNode.getName(), value);
        }
        for (DeclarationNode declarationNode : getMachineTranslator().getConstants()) {
            Expr expr = getMachineTranslator().getVariable(declarationNode);
            Expr value = model.eval(expr, true);
            map.put(declarationNode.getName(), value);
        }
        return new State(null, map);
    }
}
