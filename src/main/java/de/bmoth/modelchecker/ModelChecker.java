package de.bmoth.modelchecker;

import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Model;
import de.bmoth.backend.Abortable;
import de.bmoth.backend.z3.MachineToZ3Translator;
import de.bmoth.modelchecker.esmc.State;
import de.bmoth.parser.ast.nodes.DeclarationNode;
import de.bmoth.parser.ast.nodes.MachineNode;

import java.util.HashMap;

public abstract class ModelChecker<R> implements Abortable {
    private Context ctx;
    private MachineToZ3Translator machineTranslator;
    private volatile boolean isAborted;

    protected ModelChecker(MachineNode machine) {
        this.ctx = new Context();
        this.machineTranslator = new MachineToZ3Translator(machine, ctx);
    }

    public final R check() {
        isAborted = false;
        return doModelCheck();
    }

    @Override
    public void abort() {
        isAborted = true;
    }

    protected boolean isAborted() {
        return isAborted;
    }

    protected Context getContext() {
        return ctx;
    }

    protected MachineToZ3Translator getMachineTranslator() {
        return machineTranslator;
    }

    protected abstract R doModelCheck();

    protected State getStateFromModel(Model model) {
        return getStateFromModel(null, model);
    }

    protected State getStateFromModel(State predecessor, Model model) {
        HashMap<String, Expr> map = new HashMap<>();
        for (DeclarationNode declNode : machineTranslator.getVariables()) {
            Expr expr = machineTranslator.getPrimedVariable(declNode);
            Expr value = model.eval(expr, true);
            map.put(declNode.getName(), value);
        }
        for (DeclarationNode declarationNode : machineTranslator.getConstants()) {
            Expr expr = machineTranslator.getVariable(declarationNode);
            Expr value = model.eval(expr, true);
            map.put(declarationNode.getName(), value);
        }

        return new State(predecessor, map);
    }
}
