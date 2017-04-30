package de.bmoth.modelchecker;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Sort;

import de.bmoth.backend.MachineToZ3Translator;
import de.bmoth.backend.FormulaToZ3Translator;
import de.bmoth.parser.ast.nodes.*;

import java.util.*;

/**
 * Created by krings on 28.04.17.
 */
public class ModelChecker {
    public static boolean doModelCheck(MachineNode machine) {
        Context ctx = new Context();
        MachineToZ3Translator machineTranslator = new MachineToZ3Translator(machine, ctx);

        Set<State> visited = new HashSet<>();
        Stack<State> queue = new Stack<>();

        BoolExpr initialValueConstraint = machineTranslator.getInitialValueConstraint();

        while (!queue.isEmpty()) {
            State current = queue.pop();

            // check invariant

            // compute successors
            // add to queue if not in visited

        }

        return false;// TODO
    }
}
