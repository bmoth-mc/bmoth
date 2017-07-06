package de.bmoth.modelchecker.esmc;

import com.microsoft.z3.*;
import de.bmoth.backend.TranslationOptions;
import de.bmoth.backend.z3.MachineToZ3Translator;
import de.bmoth.backend.z3.SolutionFinder;
import de.bmoth.backend.z3.Z3SolverFactory;
import de.bmoth.modelchecker.ModelChecker;
import de.bmoth.modelchecker.ModelCheckingResult;
import de.bmoth.modelchecker.State;
import de.bmoth.parser.ast.nodes.MachineNode;
import de.bmoth.preferences.BMothPreferences;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static de.bmoth.modelchecker.ModelCheckingResult.*;

public class ExplicitStateModelChecker extends ModelChecker {

    private static final Logger LOGGER = Logger.getLogger(ExplicitStateModelChecker.class.getName());

    private Solver solver;
    private SolutionFinder finder;
    private List<SuccessorsCallable> callables;
    private MachineNode machineNode;
    //private HashMap<SolutionFinder, Context> opFinder;

    public ExplicitStateModelChecker(MachineNode machine) {
        super(machine);
        this.solver = Z3SolverFactory.getZ3Solver(getContext());
        this.finder = new SolutionFinder(solver, getContext());
        this.machineNode = machine;
    }

    public static ModelCheckingResult check(MachineNode machine) {
        ExplicitStateModelChecker modelChecker = new ExplicitStateModelChecker(machine);
        return modelChecker.check();
    }

    @Override
    public void abort() {
        super.abort();
        finder.abort();
        for(SuccessorsCallable callable : callables) {
            callable.getFinder().abort();
        }
    }

    @Override
    protected ModelCheckingResult doModelCheck() {
        final int maxInitialStates = BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INITIAL_STATE);
        final int maxTransitions = BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_TRANSITIONS);

        Set<State> visited = new HashSet<>();
        Queue<State> queue = new LinkedList<>();
        // prepare initial states
        BoolExpr initialValueConstraint = getMachineTranslator().getInitialValueConstraint();

        Set<Model> models = finder.findSolutions(initialValueConstraint, maxInitialStates);
        models.stream()
            .map(this::getStateFromModel)
            .forEach(queue::add);

        final BoolExpr invariant = getMachineTranslator().getInvariantConstraint();
        solver.add(invariant);

        // create joint operations constraint and permanently add to separate
        // solver
        /* TODO: don't create one big constraint
         * Problems:
         *  - How does the solvers work? (push, pop)
         *  - How does the finders work?
         *  - How does the computation of the successors works?
         */


        //final BoolExpr operationsConstraint = getMachineTranslator().getCombinedOperationConstraint();
        List<BoolExpr> operationConstraints = getMachineTranslator().getOperationConstraints();
        callables = new ArrayList<>(operationConstraints.size());
        for(BoolExpr operationConstraint : operationConstraints) {
            /*Context ctx = new Context();
            Solver operationSolver = Z3SolverFactory.getZ3Solver(ctx);
            SolutionFinder operationFinder = new SolutionFinder(operationSolver, ctx);
            operationSolver.add((BoolExpr) operationConstraint.translate(ctx));
            opFinder.put(operationFinder, ctx);*/
            callables.add(new SuccessorsCallable(operationConstraint, maxTransitions));
        }

        while (!isAborted() && !queue.isEmpty()) {
            solver.push();
            State current = queue.poll();
            visited.add(current);

            // apply current state - remains stored in solver for loop iteration
            BoolExpr stateConstraint = current.getStateConstraint(getContext());
            solver.add(stateConstraint);

            // check invariant & state
            Status check = solver.check();
            switch (check) {
                case UNKNOWN:
                    return createUnknown(visited.size(), solver.getReasonUnknown());
                case UNSATISFIABLE:
                    return createCounterExampleFound(visited.size(), current);
                case SATISFIABLE:
                default:
                    // continue
            }

            // compute successors on separate finder (parralel)
            //List<Callable<Void>> callables= new ArrayList<>(opFinder.size());
            ExecutorService execService = Executors.newCachedThreadPool();
            /*for (SolutionFinder opFinder : this.opFinder.keySet()) {
                Callable<Void> call = () -> {
                    Set<Model> successors = opFinder.findSolutions((BoolExpr)stateConstraint.translate(getContext(opFinder)), maxTransitions);

                    successors.stream()
                        .map(model -> getStateFromModel(current, model))
                        .filter(state -> !visited.contains(state) && !queue.contains(state))
                        .forEach(queue::add);
                    return null;
                };
                callables.add(call);
            }*/
            for (SuccessorsCallable callable : callables) {
                callable.prepareCall(current, stateConstraint);
            }
            try {
                Set<State> successors = new HashSet<>();
                List<Future<Set<State>>> futures = execService.invokeAll(callables);
                for (Future<Set<State>> future : futures) {
                    if (future.isDone()) {
                        successors.addAll(future.get());
                    }
                }
                successors.stream()
                    .filter(state -> !visited.contains(state) && !queue.contains(state))
                    .forEach(queue::add);
                execService.shutdown();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "The following exception was thrown:", e);
            }

            /*models = opFinder.findSolutions(stateConstraint, maxTransitions);
            models.stream()
                .map(model -> getStateFromModel(current, model))
                .filter(state -> !visited.contains(state) && !queue.contains(state))
                .forEach(queue::add);*/

            solver.pop();
        }

        if (isAborted()) {
            return createAborted(visited.size());
        } else {
            return createVerified(visited.size());
        }
    }

    private State getStateFromModel(Model model) {
        return getStateFromModel(null, model, TranslationOptions.PRIMED_0);
    }

    private State getStateFromModel(State predecessor, Model model) {
        return getStateFromModel(predecessor, model, TranslationOptions.PRIMED_0);
    }

    public class SuccessorsCallable implements Callable<Set<State>> {

        private Context context;
        private SolutionFinder opFinder;
        private BoolExpr stateConstraint;
        private final int maxTransitions;
        private State currentState;
        private MachineToZ3Translator machineToZ3Translator;

        SuccessorsCallable(BoolExpr operationConstraint, int maxTransitions) {
            this.context = new Context();
            Solver operationSolver = Z3SolverFactory.getZ3Solver(context);
            operationSolver.add((BoolExpr) operationConstraint.translate(context));
            this.opFinder = new SolutionFinder(operationSolver, context);
            this.maxTransitions = maxTransitions;
            this.machineToZ3Translator = new MachineToZ3Translator(machineNode, context);

        }

        void prepareCall(State currentState, BoolExpr stateConstraint) {
            this.stateConstraint = (BoolExpr) stateConstraint.translate(this.context);
            this.currentState = currentState;
        }

        @Override
        public Set<State> call() throws Exception {
            Set<Model> successorModels = this.opFinder.findSolutions(this.stateConstraint, this.maxTransitions);
            return successorModels.stream().map(model -> {
                State state = new State(currentState, machineToZ3Translator.getVarMapFromModel(model,TranslationOptions.PRIMED_0));
                state.translate(getContext());
                return state;
            }).collect(Collectors.toSet());
        }

        SolutionFinder getFinder() {
            return opFinder;
        }
    }
}
