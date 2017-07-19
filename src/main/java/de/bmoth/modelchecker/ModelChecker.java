package de.bmoth.modelchecker;

import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Model;
import de.bmoth.backend.Abortable;
import de.bmoth.backend.TranslationOptions;
import de.bmoth.backend.z3.FormulaToZ3Translator;
import de.bmoth.backend.z3.MachineToZ3Translator;
import de.bmoth.parser.ast.nodes.MachineNode;
import de.bmoth.parser.ast.nodes.PredicateNode;
import de.bmoth.parser.ast.nodes.ltl.BuechiAutomaton;
import de.bmoth.parser.ast.nodes.ltl.BuechiAutomatonNode;

import java.util.HashSet;
import java.util.Set;

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

    protected State getStateFromModel(State predecessor, Model model, TranslationOptions ops, BuechiAutomaton buechiAutomaton) {
        final Set<BuechiAutomatonNode> buechiNodes = new HashSet<>();
        final Set<BuechiAutomatonNode> candidates = new HashSet<>();
        if (predecessor == null) {
            candidates.addAll(buechiAutomaton.getInitialStates());
        } else {
            predecessor.getBuechiNodes().forEach(n -> candidates.addAll(n.getSuccessors()));
        }
        for (BuechiAutomatonNode node : candidates) {
            if (node.getLabels().isEmpty()) {
                buechiNodes.add(node);
            }
            for (PredicateNode label : node.getLabels()) {
                Expr eval = model.eval(FormulaToZ3Translator.translatePredicate(label, ctx, machineTranslator.getZ3TypeInference()), true);
                switch (eval.getBoolValue()) {
                    case Z3_L_FALSE:
                        break;
                    case Z3_L_UNDEF:
                        throw new UnsupportedOperationException("should not be undefined");
                    case Z3_L_TRUE:
                        buechiNodes.add(node);
                }
            }
        }

        return new State(predecessor, getMachineTranslator().getVarMapFromModel(model, ops), buechiNodes);
    }

    protected State getStateFromModel(State predecessor, Model model, TranslationOptions ops) {
        return new State(predecessor, getMachineTranslator().getVarMapFromModel(model, ops));
    }
}
