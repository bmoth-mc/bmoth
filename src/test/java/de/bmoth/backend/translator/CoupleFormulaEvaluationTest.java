package de.bmoth.backend.translator;

import com.microsoft.z3.Status;
import de.bmoth.TestUsingZ3;
import de.bmoth.util.UtilMethodsTest;
import org.junit.Test;

public class CoupleFormulaEvaluationTest extends TestUsingZ3 {
    @Test
    public void testIntegerCoupleFormula() throws Exception {
        String formula = "x = (1 |-> 2) & y = (2 |-> 3) & x = y";
        UtilMethodsTest.check(Status.UNSATISFIABLE, formula, z3Context, z3Solver);
    }
}
