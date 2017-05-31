package de.bmoth.backend.translator;

import com.microsoft.z3.Status;
import de.bmoth.TestUsingZ3;
import org.junit.Test;

public class SequenceFormulaTest extends TestUsingZ3 {
    @Test
    public void testEmptySequenceFormula() {
        check(Status.SATISFIABLE, "x = [] & x /= [1]");
        check(Status.UNSATISFIABLE, "[] = [1]");
    }

    @Test
    public void testSimpleSequenceExtensionFormula() {
        check(Status.SATISFIABLE, "[4,5] = [4,5]");
        check(Status.SATISFIABLE, "[4,5] = [x,5]");
        check(Status.UNSATISFIABLE, "[4,5] = [5,4]");
        check(Status.UNSATISFIABLE, "[4,5] = [4,5,x]");
    }

    @Test
    public void testFront() {
        check(Status.SATISFIABLE, "front([4,5]) = [4]");
        check(Status.UNSATISFIABLE, "front([4,5]) = [5]");
    }

    @Test
    public void testFirst() {
        check(Status.SATISFIABLE, "first([4,5]) = 4");
        check(Status.UNSATISFIABLE, "first([4,5]) = 5");
        check(Status.UNSATISFIABLE, "first([5]) = 4");
        check(Status.UNSATISFIABLE, "first([]) = x & x = 1");
    }

    @Test
    public void testLast() {
        check(Status.SATISFIABLE, "last([4,5]) = 5");
        check(Status.UNSATISFIABLE, "last([4,5]) = 4");
        check(Status.SATISFIABLE, "last([5]) = 5");
        check(Status.UNSATISFIABLE, "last([5]) = 4");
        check(Status.UNSATISFIABLE, "last([]) = x & x = 1");
    }

    @Test
    public void testFunctionCall() {
        check(Status.SATISFIABLE, "[4,5](1) = 4 & [4,5](2) = 5");
    }


}
