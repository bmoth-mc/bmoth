package de.bmoth.backend.translator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

import de.bmoth.util.UtilMethodsTest;

public class CoupleFormulaEvaluationTest {

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
	public void testIntegerCoupleFormula() throws Exception {
		String formula = "x = (1 |-> 2) & y = (2 |-> 3) & x = y";
		UtilMethodsTest.check(Status.UNSATISFIABLE, formula, ctx, s);
	}

}
