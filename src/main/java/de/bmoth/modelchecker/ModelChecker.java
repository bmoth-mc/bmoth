package de.bmoth.modelchecker;

import com.microsoft.z3.Context;
import com.microsoft.z3.Model;
import de.bmoth.backend.Abortable;
import de.bmoth.backend.TranslationOptions;
import de.bmoth.backend.z3.MachineToZ3Translator;
import de.bmoth.parser.ast.nodes.MachineNode;


public abstract class ModelChecker implements Abortable {
    private Context ctx;
    private MachineToZ3Translator machineTranslator;
    private volatile boolean isAborted;

    protected ModelChecker(MachineNode machine) {
        this.ctx = new Context();
        this.machineTranslator = new MachineToZ3Translator(machine, ctx);
    }

    public final ModelCheckingResult check() {
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

    protected abstract ModelCheckingResult doModelCheck();

    protected State getStateFromModel(Model model, TranslationOptions ops) {
        return new State(getMachineTranslator().getVarMapFromModel(model, ops));
    }
}
