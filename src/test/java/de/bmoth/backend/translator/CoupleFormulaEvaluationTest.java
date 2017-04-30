package de.bmoth.backend.translator;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

import de.bmoth.backend.Z3Translator;

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
		// getting the translated z3 representation of the formula
		BoolExpr constraint = Z3Translator.translatePredicate(formula, ctx);

		s.add(constraint);
		Status check = s.check();

		assertEquals(Status.UNSATISFIABLE, check);
	}

}
