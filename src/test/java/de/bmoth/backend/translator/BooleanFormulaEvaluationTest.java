package de.bmoth.backend.translator;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

import de.bmoth.backend.FormulaTranslator;

public class BooleanFormulaEvaluationTest {

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

	@Ignore
	@Test
	public void testSimpleBooleanFormula() throws Exception {
		String formula = "x = TRUE & y = FALSE";
		// getting the translated z3 representation of the formula
		BoolExpr constraint = FormulaTranslator.translatePredicate(formula, ctx);

		s.add(constraint);
		Status check = s.check();

		Expr x = ctx.mkIntConst("x");

		assertEquals(Status.SATISFIABLE, check);
		assertEquals(ctx.mkInt(5), s.getModel().eval(x, true));
	}
}
