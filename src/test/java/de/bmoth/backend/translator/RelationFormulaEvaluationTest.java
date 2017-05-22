package de.bmoth.backend.translator;

import de.bmoth.TestUsingZ3;
import de.bmoth.util.UtilMethodsTest;
import org.junit.Test;

import static com.microsoft.z3.Status.SATISFIABLE;
import static com.microsoft.z3.Status.UNSATISFIABLE;

public class RelationFormulaEvaluationTest extends TestUsingZ3 {
    @Test
    public void testRelationalInverse() {
        UtilMethodsTest.check(SATISFIABLE, "p = {3|->5, 3|->9, 6|->3, 9|->2} & q = p~", z3Context, z3Solver);
        UtilMethodsTest.check(UNSATISFIABLE, "p = {3|->5} & q = {5|->2} & q=p~", z3Context, z3Solver);
    }


}
