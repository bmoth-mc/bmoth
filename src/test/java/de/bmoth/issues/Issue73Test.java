package de.bmoth.issues;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Model;
import de.bmoth.TestUsingZ3;
import de.bmoth.backend.z3.SolutionFinder;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class Issue73Test extends TestUsingZ3 {
    @Test
    public void testSatPredicateWithoutModel() throws IOException {
        String formula = "1 < 2";
        BoolExpr constraint = translatePredicate(formula, z3Context);

        SolutionFinder finder = new SolutionFinder(z3Solver, z3Context);
        Set<Model> solutions = finder.findSolutions(constraint, 20);
        assertEquals(0, solutions.size());
    }
}
