package de.bmoth.util;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import de.bmoth.backend.z3.FormulaToZ3Translator;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class UtilMethodsTest {

    @Test
    public void testSort() throws Exception {
        Map<String, Set<String>> dependencies = new HashMap<>();
        dependencies.put("a", new HashSet<>());
        dependencies.put("b", new HashSet<>(Arrays.asList("a")));
        dependencies.put("c", new HashSet<>(Arrays.asList("a", "d")));
        dependencies.put("d", new HashSet<>(Arrays.asList("b")));
        List<String> sorted = Utils.sortByTopologicalOrder(dependencies);
        assertEquals(Arrays.asList("a", "b", "d", "c"), sorted);
    }

    public static void check(Status satisfiable, String formula, Context ctx, Solver s) {
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
        // create scope just for current constraint
        s.push();
        s.add(constraint);
        Status check = s.check();
        // clean solver stack
        s.pop();
        assertEquals(satisfiable, check);
    }

    public static void checkLaw(String law, Context ctx, Solver s) {
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(law, ctx);
        s.push();
        s.add(ctx.mkNot(constraint));
        Status check = s.check();
        s.pop();
        assertEquals(Status.UNSATISFIABLE, check);
    }

    public static void checkTruthTable(Map<String, Status> map, Context ctx, Solver s) {
        for (Map.Entry<String, Status> entry : map.entrySet()) {
            check(entry.getValue(), entry.getKey(), ctx, s);
            s.reset();
        }
    }

}
