package de.bmoth.backend.translator;

import de.bmoth.util.UtilMethodsTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

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
        UtilMethodsTest.check(Status.SATISFIABLE, "[4,5] = [4,5]", ctx, s);
        UtilMethodsTest.check(Status.SATISFIABLE, "[4,5] = [x,5]", ctx, s);

        UtilMethodsTest.check(Status.UNSATISFIABLE, "[4,5] = [5,4]", ctx, s);
        UtilMethodsTest.check(Status.UNSATISFIABLE, "[4,5] = [4,5,x]", ctx, s);
    }

    @Test
    public void testFirst() throws Exception {
        UtilMethodsTest.check(Status.SATISFIABLE, "first([4,5]) = 4", ctx, s);
        UtilMethodsTest.check(Status.UNSATISFIABLE, "first([5]) = 4", ctx, s);
        UtilMethodsTest.check(Status.UNSATISFIABLE, "first([]) = x & x = 1", ctx, s);

    }

    @Test
    public void testFunctionCall() throws Exception {
        UtilMethodsTest.check(Status.SATISFIABLE, "[4,5](1) = 4 & [4,5](2) = 5", ctx, s);
    }


}
