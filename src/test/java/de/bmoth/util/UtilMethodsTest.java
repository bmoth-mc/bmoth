package de.bmoth.util;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import de.bmoth.backend.FormulaToZ3Translator;
import org.junit.Test;

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
        System.out.println(constraint);
        s.add(constraint);
        Status check = s.check();
        assertEquals(satisfiable, check);
    }

    public static void checkTruthTable(Map<String, Status> map, Context ctx, Solver s) {
        for (Map.Entry<String, Status> entry : map.entrySet()) {
            check(entry.getValue(), entry.getKey(), ctx, s);
            s.reset();
        }
    }

}
