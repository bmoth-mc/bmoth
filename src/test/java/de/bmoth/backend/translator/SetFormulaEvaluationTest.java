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

import de.bmoth.backend.FormulaTranslator;

public class SetFormulaEvaluationTest {

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
	public void testSimpleSetExtensionFormula() throws Exception {
		String formula = "{1,2} = {2,3}";
		// getting the translated z3 representation of the formula
		BoolExpr constraint = FormulaTranslator.translatePredicate(formula, ctx);

		s.add(constraint);
		Status check = s.check();

		assertEquals(Status.UNSATISFIABLE, check);
	}

	@Test
	public void testSetExtensionFormulaWithSingleVarModel() throws Exception {
		String formula = "{1,2} = {2,x}";
		// getting the translated z3 representation of the formula
		BoolExpr constraint = FormulaTranslator.translatePredicate(formula, ctx);

		s.add(constraint);
		Status check = s.check();

		Expr x = ctx.mkIntConst("x");

		assertEquals(Status.SATISFIABLE, check);
		assertEquals(ctx.mkInt(1), s.getModel().eval(x, true));
	}

	@Test
	public void testSetExtensionFormulaWithSetVarModel() throws Exception {
		String formula = "{1,2} = x";
		// getting the translated z3 representation of the formula
		BoolExpr constraint = FormulaTranslator.translatePredicate(formula, ctx);

		s.add(constraint);
		Status check = s.check();

		Expr x = ctx.mkArrayConst("x", ctx.mkIntSort(), ctx.mkBoolSort());

		assertEquals(Status.SATISFIABLE, check);
		assertEquals("(store (store ((as const (Array Int Bool)) false) 1 true) 2 true)",
				s.getModel().eval(x, true).toString());
	}

}
