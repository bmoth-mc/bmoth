package de.bmoth.laws;

import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import de.bmoth.util.UtilMethodsTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ArithmeticLaws {
    private Context ctx;
    private Solver s;

    @Before
    public void setup() {
        ctx = new Context();
        s = ctx.mkSolver();
    }

    @After
    public void cleanup() {
        ctx.close();
    }

    @Test
    public void testArithmeticLaws() throws Exception {
        //need :INTEGER here because * could be the cartesian product
        UtilMethodsTest.checkLaw("x:INTEGER & y:INTEGER & x*y = y*x", ctx, s);
        UtilMethodsTest.checkLaw("x*(y+z) = x*y + x*z", ctx, s);
    }
}
