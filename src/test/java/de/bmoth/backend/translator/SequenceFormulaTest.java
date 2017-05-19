package de.bmoth.backend.translator;

import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import de.bmoth.util.UtilMethodsTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
    public void testEmptySequenceFormula() throws Exception {
        UtilMethodsTest.check(Status.SATISFIABLE, "x = [] & x /= [1]", ctx, s);
        UtilMethodsTest.check(Status.UNSATISFIABLE, "[] = [1]", ctx, s);
    }

    @Test
    public void testSimpleSequenceExtensionFormula() throws Exception {
        UtilMethodsTest.check(Status.SATISFIABLE, "[4,5] = [4,5]", ctx, s);
        UtilMethodsTest.check(Status.SATISFIABLE, "[4,5] = [x,5]", ctx, s);
        UtilMethodsTest.check(Status.UNSATISFIABLE, "[4,5] = [5,4]", ctx, s);
        UtilMethodsTest.check(Status.UNSATISFIABLE, "[4,5] = [4,5,x]", ctx, s);
    }

    @Test
    public void testFront() throws Exception {
        UtilMethodsTest.check(Status.SATISFIABLE, "front([4,5]) = [4]", ctx, s);
        UtilMethodsTest.check(Status.UNSATISFIABLE, "front([4,5]) = [5]", ctx, s);
    }

    @Test
    public void testFirst() throws Exception {
        UtilMethodsTest.check(Status.SATISFIABLE, "first([4,5]) = 4", ctx, s);
        UtilMethodsTest.check(Status.UNSATISFIABLE, "first([4,5]) = 5", ctx, s);
        UtilMethodsTest.check(Status.UNSATISFIABLE, "first([5]) = 4", ctx, s);
        UtilMethodsTest.check(Status.UNSATISFIABLE, "first([]) = x & x = 1", ctx, s);
    }

    @Test
    public void testLast() throws Exception {
        UtilMethodsTest.check(Status.SATISFIABLE, "last([4,5]) = 5", ctx, s);
        UtilMethodsTest.check(Status.UNSATISFIABLE, "last([4,5]) = 4", ctx, s);
        UtilMethodsTest.check(Status.SATISFIABLE, "last([5]) = 5", ctx, s);
        UtilMethodsTest.check(Status.UNSATISFIABLE, "last([5]) = 4", ctx, s);
        UtilMethodsTest.check(Status.UNSATISFIABLE, "last([]) = x & x = 1", ctx, s);
    }

    @Test
    public void testFunctionCall() throws Exception {
        UtilMethodsTest.check(Status.SATISFIABLE, "[4,5](1) = 4 & [4,5](2) = 5", ctx, s);
    }


}
