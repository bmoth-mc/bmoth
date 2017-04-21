package de.bmoth.backend.translator;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

public class Z3ConnectionTest {
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
	public void testSimpleCallToZ3() {
		// a = b & a = 5
		Expr a = ctx.mkIntConst("a"), b = ctx.mkIntConst("b");
		BoolExpr constraint = ctx.mkAnd(ctx.mkEq(a, b), ctx.mkEq(a, ctx.mkInt(5)));
		s.add(constraint);
		Status check = s.check();

		assertEquals(Status.SATISFIABLE, check);
		assertEquals(ctx.mkInt(5), s.getModel().eval(a, true));
		assertEquals(ctx.mkInt(5), s.getModel().eval(b, true));
	}

	@Test
	public void testQuantifiedFormula() {
		Expr a = ctx.mkIntConst("a"), b = ctx.mkIntConst("b");
		BoolExpr constraint = ctx.mkAnd(ctx.mkEq(a, b), ctx.mkEq(a, ctx.mkInt(5)), ctx.mkEq(b, ctx.mkInt(7)));
		constraint = ctx.mkExists(new Expr[] { a, b }, constraint, 1, null, null, null, null);

		s.add(constraint);
		Status check = s.check();

		assertEquals(Status.UNSATISFIABLE, check);
	}

}
