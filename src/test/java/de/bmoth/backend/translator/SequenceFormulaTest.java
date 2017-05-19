package de.bmoth.backend.translator;

import com.microsoft.z3.Status;
import de.bmoth.TestUsingZ3;
import de.bmoth.util.UtilMethodsTest;
import org.junit.Test;

public class SequenceFormulaTest extends TestUsingZ3 {
    @Test
    public void testEmptySequenceFormula() throws Exception {
        UtilMethodsTest.check(Status.SATISFIABLE, "x = [] & x /= [1]", z3Context, z3Solver);
        UtilMethodsTest.check(Status.UNSATISFIABLE, "[] = [1]", z3Context, z3Solver);
    }

    @Test
    public void testSimpleSequenceExtensionFormula() throws Exception {
        UtilMethodsTest.check(Status.SATISFIABLE, "[4,5] = [4,5]", z3Context, z3Solver);
        UtilMethodsTest.check(Status.SATISFIABLE, "[4,5] = [x,5]", z3Context, z3Solver);
        UtilMethodsTest.check(Status.UNSATISFIABLE, "[4,5] = [5,4]", z3Context, z3Solver);
        UtilMethodsTest.check(Status.UNSATISFIABLE, "[4,5] = [4,5,x]", z3Context, z3Solver);
    }

    @Test
    public void testFront() throws Exception {
        UtilMethodsTest.check(Status.SATISFIABLE, "front([4,5]) = [4]", z3Context, z3Solver);
        UtilMethodsTest.check(Status.UNSATISFIABLE, "front([4,5]) = [5]", z3Context, z3Solver);
    }

    @Test
    public void testFirst() throws Exception {
        UtilMethodsTest.check(Status.SATISFIABLE, "first([4,5]) = 4", z3Context, z3Solver);
        UtilMethodsTest.check(Status.UNSATISFIABLE, "first([4,5]) = 5", z3Context, z3Solver);
        UtilMethodsTest.check(Status.UNSATISFIABLE, "first([5]) = 4", z3Context, z3Solver);
        UtilMethodsTest.check(Status.UNSATISFIABLE, "first([]) = x & x = 1", z3Context, z3Solver);
    }

    @Test
    public void testLast() throws Exception {
        UtilMethodsTest.check(Status.SATISFIABLE, "last([4,5]) = 5", z3Context, z3Solver);
        UtilMethodsTest.check(Status.UNSATISFIABLE, "last([4,5]) = 4", z3Context, z3Solver);
        UtilMethodsTest.check(Status.SATISFIABLE, "last([5]) = 5", z3Context, z3Solver);
        UtilMethodsTest.check(Status.UNSATISFIABLE, "last([5]) = 4", z3Context, z3Solver);
        UtilMethodsTest.check(Status.UNSATISFIABLE, "last([]) = x & x = 1", z3Context, z3Solver);
    }

    @Test
    public void testFunctionCall() throws Exception {
        UtilMethodsTest.check(Status.SATISFIABLE, "[4,5](1) = 4 & [4,5](2) = 5", z3Context, z3Solver);
    }


}
