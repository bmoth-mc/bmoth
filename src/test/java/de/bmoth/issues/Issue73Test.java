package de.bmoth.issues;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Model;
import de.bmoth.TestUsingZ3;
import de.bmoth.backend.z3.FormulaToZ3Translator;
import de.bmoth.backend.z3.SolutionFinder;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class Issue73Test extends TestUsingZ3 {
    @Test
    @Ignore
    public void testSatPredicateWithoutModel() throws IOException {
        String formula = "1 < 2";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);

        SolutionFinder finder = new SolutionFinder(constraint, z3Solver, z3Context);
        Set<Model> solutions = finder.findSolutions(20);
        assertEquals(1, solutions.size());
    }
}
