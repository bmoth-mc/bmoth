package de.bmoth.laws;

import de.bmoth.TestUsingZ3;
import org.junit.Test;

public class ArithmeticLawsTest extends TestUsingZ3 {
    @Test
    public void testArithmeticLaws() {
        //need :INTEGER here because * could be the cartesian product
        checkLaw("x:INTEGER & y:INTEGER & x*y = y*x");
        checkLaw("x*(y+z) = x*y + x*z");
    }
}
