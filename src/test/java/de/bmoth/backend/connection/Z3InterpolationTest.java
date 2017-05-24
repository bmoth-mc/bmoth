package de.bmoth.backend.connection;

import com.microsoft.z3.*;
import com.microsoft.z3.InterpolationContext.ComputeInterpolantResult;
import de.bmoth.TestUsingZ3;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Z3InterpolationTest extends TestUsingZ3 {
    private InterpolationContext ctx;
    private Solver s;

    @Override
    @Before
    public void setup() {
        ctx = InterpolationContext.mkContext();
        s = ctx.mkSolver();
    }

    @Override
    @After
    public void cleanup() {
        ctx.close();
    }

    @Test
    public void testSimpleInterpolation() {
        IntExpr x = ctx.mkIntConst("x");
        BoolExpr xGtZero = ctx.mkGt(x, ctx.mkInt(0));
        BoolExpr xLtZero = ctx.mkLt(x, ctx.mkInt(0));

        BoolExpr xGtZeroInterpolant = ctx.MkInterpolant(xGtZero);

        s.add(xGtZero);
        s.add(xLtZero);

        Status check = s.check();
        assertEquals(Status.UNSATISFIABLE, check);

        ComputeInterpolantResult ir = ctx.ComputeInterpolant(ctx.mkAnd(xGtZeroInterpolant, xLtZero), ctx.mkParams());
        assertEquals(1, ir.interp.length);
        assertEquals("(not (<= x 0))", ir.interp[0].toString());
    }
}
