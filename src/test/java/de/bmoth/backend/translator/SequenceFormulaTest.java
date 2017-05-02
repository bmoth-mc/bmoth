package de.bmoth.backend.translator;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

import de.bmoth.backend.FormulaToZ3Translator;

public class SequenceFormulaTest {

    private Context ctx;
    private Solver s;

    @Before
    public void setup() {
        ctx = new Context();
        s = ctx.mkSolver();
    }

    @After
    public void cleanup() {
        ctx.close();
    }

    @Test
    public void testSimpleSequenceExtensionFormula() throws Exception {
        check(Status.SATISFIABLE, "[4,5] = [4,5]");
        check(Status.SATISFIABLE, "[4,5] = [x,5]");
        
        check(Status.UNSATISFIABLE, "[4,5] = [5,4]");
        check(Status.UNSATISFIABLE, "[4,5] = [4,5,x]");
    }

    @Test
    public void testSimpleSequenceExtensionFormula2() throws Exception {
        check(Status.SATISFIABLE, "first([4,5]) = 4");
        check(Status.UNSATISFIABLE, "first([5]) = 4");
        check(Status.UNSATISFIABLE, "first([]) = x & x = 1 ");

    }

    private void check(Status satisfiable, String formula) {
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
        System.out.println(constraint);
        s.add(constraint);
        Status check = s.check();
        assertEquals(satisfiable, check);
    }

}
