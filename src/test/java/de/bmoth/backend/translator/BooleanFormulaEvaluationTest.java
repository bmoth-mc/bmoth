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

	@Test
	public void testTrueFormula() throws Exception {
		String formula = "x = TRUE";
		// getting the translated z3 representation of the formula
		BoolExpr constraint = FormulaTranslator.translatePredicate(formula, ctx);

		s.add(constraint);
		Status check = s.check();

		Expr x = ctx.mkBoolConst("x");

		assertEquals(Status.SATISFIABLE, check);
		assertEquals(ctx.mkTrue(), s.getModel().eval(x, true));
	}

	@Test
	public void testFalseFormula() throws Exception {
		String formula = "x = FALSE";
		// getting the translated z3 representation of the formula
		BoolExpr constraint = FormulaTranslator.translatePredicate(formula, ctx);

		s.add(constraint);
		Status check = s.check();

		Expr x = ctx.mkBoolConst("x");

		assertEquals(Status.SATISFIABLE, check);
		assertEquals(ctx.mkFalse(), s.getModel().eval(x, true));
	}

	@Test
	public void testAndFormula() throws Exception {
		String formula = "x & y";
		// getting the translated z3 representation of the formula
		BoolExpr constraint = FormulaTranslator.translatePredicate(formula, ctx);

		s.add(constraint);
		Status check = s.check();

		Expr x = ctx.mkBoolConst("x");
		Expr y = ctx.mkBoolConst("y");

		assertEquals(Status.SATISFIABLE, check);
		assertEquals(ctx.mkTrue(), s.getModel().eval(x, true));
		assertEquals(ctx.mkTrue(), s.getModel().eval(y, true));
	}

	@Test
	public void testSimpleBooleanFormula() throws Exception {
		String formula = "x = TRUE & y = FALSE";
		// getting the translated z3 representation of the formula
		BoolExpr constraint = FormulaTranslator.translatePredicate(formula, ctx);

		s.add(constraint);
		Status check = s.check();

		Expr x = ctx.mkBoolConst("x");
		Expr y = ctx.mkBoolConst("y");

		assertEquals(Status.SATISFIABLE, check);
		assertEquals(ctx.mkBool(true), s.getModel().eval(x, true));
		assertEquals(ctx.mkBool(false), s.getModel().eval(y, false));

	}

	@Test
	public void testImplication() throws Exception {
		//Note, rebuild the parser ("gradle clean build") if this test fails.
		
		String formula = "1=1 => x";
		// getting the translated z3 representation of the formula
		BoolExpr constraint = FormulaTranslator.translatePredicate(formula, ctx);
		s.add(constraint);
		Status check = s.check();

		Expr x = ctx.mkBoolConst("x");
		assertEquals(Status.SATISFIABLE, check);
		assertEquals(ctx.mkBool(true), s.getModel().eval(x, true));
	}
}
