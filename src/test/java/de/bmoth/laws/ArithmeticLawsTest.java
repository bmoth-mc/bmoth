package de.bmoth.laws;

import de.bmoth.TestUsingZ3;
import de.bmoth.util.UtilMethodsTest;
import org.junit.Test;

public class ArithmeticLawsTest extends TestUsingZ3 {
    @Test
    public void testArithmeticLaws() {
        //need :INTEGER here because * could be the cartesian product
        UtilMethodsTest.checkLaw("x:INTEGER & y:INTEGER & x*y = y*x", z3Context, z3Solver);
        UtilMethodsTest.checkLaw("x*(y+z) = x*y + x*z", z3Context, z3Solver);
    }
}
