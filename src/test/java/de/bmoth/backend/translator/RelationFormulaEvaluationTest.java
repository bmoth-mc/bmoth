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
        UtilMethodsTest.check(SATISFIABLE, "p = {3|->5} & q = {5|->3} & q=p~", z3Context, z3Solver);

    }

    @Test
    public void testDomain() {
        UtilMethodsTest.check(SATISFIABLE, "p = {3|->5, 3|->9} & x = dom(p)", z3Context, z3Solver);
        UtilMethodsTest.check(SATISFIABLE, "p = {3|->5, 3|->9} & {5,9} = dom(p)", z3Context, z3Solver);
        UtilMethodsTest.check(UNSATISFIABLE, "p = {3|->5} & q = {3} & q=dom(p)", z3Context, z3Solver);
    }

    @Test
    public void testRange() {
        UtilMethodsTest.check(SATISFIABLE, "p = {3|->5, 3|->9} & x = ran(p)", z3Context, z3Solver);
        UtilMethodsTest.check(SATISFIABLE, "p = {3|->5, 3|->9} & {3} = ran(p)", z3Context, z3Solver);
        UtilMethodsTest.check(UNSATISFIABLE, "p = {3|->5} & q = {5} & q=ran(p)", z3Context, z3Solver);
    }


}
