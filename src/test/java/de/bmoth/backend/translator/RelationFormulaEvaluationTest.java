package de.bmoth.backend.translator;

import de.bmoth.TestUsingZ3;
import org.junit.Test;

import static com.microsoft.z3.Status.SATISFIABLE;
import static com.microsoft.z3.Status.UNSATISFIABLE;

public class RelationFormulaEvaluationTest extends TestUsingZ3 {
    @Test
    public void testRelationalInverse() {
        check(SATISFIABLE, "p = {3|->5, 3|->9, 6|->3, 9|->2} & q = p~");
        check(UNSATISFIABLE, "p = {3|->5} & q = {5|->2} & q=p~");
        check(SATISFIABLE, "p = {3|->5} & q = {5|->3} & q=p~");

    }

    @Test
    public void testDomain() {
        check(SATISFIABLE, "p = {3|->5, 3|->9} & x = dom(p)");
        check(SATISFIABLE, "p = {3|->5, 3|->9} & {5,9} = dom(p)");
        check(UNSATISFIABLE, "p = {3|->5} & q = {3} & q=dom(p)");
    }

    @Test
    public void testRange() {
        check(SATISFIABLE, "p = {3|->5, 3|->9} & x = ran(p)");
        check(SATISFIABLE, "p = {3|->5, 3|->9} & {3} = ran(p)");
        check(UNSATISFIABLE, "p = {3|->5} & q = {5} & q=ran(p)");
    }


}
