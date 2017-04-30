package de.bmoth.modelchecker;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Sort;
import de.bmoth.backend.Z3Translator;
import de.bmoth.parser.ast.nodes.*;

import java.util.*;

/**
 * Created by krings on 28.04.17.
 */
public class ModelChecker {
    public static boolean doModelCheck(MachineNode machine) {
        Context ctx = new Context();
        Z3Translator translator = new Z3Translator(ctx);

        Set<State> visited = new HashSet<>();
        Stack<State> queue = new Stack<>();

        SingleAssignSubstitution initialization = (SingleAssignSubstitution) machine.getInitialisation();

        BoolExpr initialValueConstraint = translator.translateSingleAssignSubstitution(initialization);

        Expr theIdentifier = ctx.mkConst(initialization.getIdentifier().getName(),
                translator.bTypeToZ3Sort(initialization.getIdentifier().getType()));
        
        while (!queue.isEmpty()) {
            State current = queue.pop();

            // check invariant

            // compute successors
            // add to queue if not in visited

        }

        return false;// TODO
    }
}
