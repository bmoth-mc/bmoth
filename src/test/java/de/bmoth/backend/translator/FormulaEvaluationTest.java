package de.bmoth.backend.translator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntNum;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

import de.bmoth.backend.FormulaTranslator;

import java.util.ArrayList;
import java.util.List;

public class FormulaEvaluationTest {

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
    public void testAdditionFormula() throws Exception {
        String formula = "x = 2 + 3";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaTranslator.translatePredicate(formula, ctx);

        s.add(constraint);
        Status check = s.check();

        Expr x = ctx.mkIntConst("x");

        assertEquals(Status.SATISFIABLE, check);
        assertEquals(ctx.mkInt(5), s.getModel().eval(x, true));
    }

    @Test
    public void testSubtractionFormula() throws Exception {
        String formula = "x = 2 - 3";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaTranslator.translatePredicate(formula, ctx);

        s.add(constraint);
        Status check = s.check();

        Expr x = ctx.mkIntConst("x");

        assertEquals(Status.SATISFIABLE, check);
        assertEquals(ctx.mkInt(-1), s.getModel().eval(x, true));
    }

    @Test
    public void testEqualityFormula() throws Exception {
        String formula = "x = 5";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaTranslator.translatePredicate(formula, ctx);

        s.add(constraint);
        Status check = s.check();

        Expr x = ctx.mkIntConst("x");

        assertEquals(Status.SATISFIABLE, check);
        assertEquals(ctx.mkInt(5), s.getModel().eval(x, true));
    }

    @Test
    public void testInequalityFormula() throws Exception {
        String formula = "x /= 0";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaTranslator.translatePredicate(formula, ctx);

        s.add(constraint);
        Status check = s.check();

        Expr x = ctx.mkIntConst("x");

        assertEquals(Status.SATISFIABLE, check);
        assertNotEquals(ctx.mkInt(0), s.getModel().eval(x, true));
    }

    @Test
    public void testModuloFormula() throws Exception {
        String formula = "x = 3 mod 2";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaTranslator.translatePredicate(formula, ctx);

        s.add(constraint);
        Status check = s.check();

        Expr x = ctx.mkIntConst("x");

        assertEquals(Status.SATISFIABLE, check);
        assertEquals(ctx.mkInt(1), s.getModel().eval(x, true));
    }

    @Test
    public void testMultiplicationFormula() throws Exception {
        String formula = "x = 3 * 2";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaTranslator.translatePredicate(formula, ctx);

        s.add(constraint);
        Status check = s.check();

        Expr x = ctx.mkIntConst("x");

        assertEquals(Status.SATISFIABLE, check);
        assertEquals(ctx.mkInt(6), s.getModel().eval(x, true));
    }

    @Test
    public void testAddMulOrderFormula() throws Exception {
        /**
         * Tests order of multiplication and addition
         */
        String formula = "x = 4 + 3 * 2 * 2";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaTranslator.translatePredicate(formula, ctx);

        s.add(constraint);
        Status check = s.check();

        Expr x = ctx.mkIntConst("x");

        assertEquals(Status.SATISFIABLE, check);
        assertEquals(ctx.mkInt(16), s.getModel().eval(x, true));
    }

    @Test
    public void testDivisionFormula() throws Exception {
        String formula = "x = 8 / 2";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaTranslator.translatePredicate(formula, ctx);

        s.add(constraint);
        Status check = s.check();

        Expr x = ctx.mkIntConst("x");

        assertEquals(Status.SATISFIABLE, check);
        assertEquals(ctx.mkInt(4), s.getModel().eval(x, true));
    }

    @Test
    public void testPowerFormula() throws Exception {
        String formula = "x = 2 ** 8";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaTranslator.translatePredicate(formula, ctx);

        s.add(constraint);
        Status check = s.check();

        Expr x = ctx.mkIntConst("x");

        assertEquals(Status.SATISFIABLE, check);
        assertEquals(ctx.mkInt(256), s.getModel().eval(x, true));
    }
    
    @Test
    public void testLessThanFormula() throws Exception {
        String formula = "1 < 2";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaTranslator.translatePredicate(formula, ctx);

        s.add(constraint);
        Status check = s.check();

        assertEquals(Status.SATISFIABLE, check);
    }

    @Test
    public void testAllSolutions() throws Exception {
        String formula = "1 < x & x < 5";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaTranslator.translatePredicate(formula, ctx);

        s.add(constraint);

        Expr x = ctx.mkIntConst("x");

        // 1st try: brute force over 'all' satisfying solutions
        // credit goes to: http://stackoverflow.com/questions/13395391/z3-finding-all-satisfying-models#answer-13398853

        List<Number> solutions = new ArrayList<>();

        // as long as formula is satisfiable
        for (int i = 0; s.check() == Status.SATISFIABLE && i < 10; i++) {
            // get current evaluation for x
            IntNum currentX = (IntNum) s.getModel().eval(x, true);
            // and exclude it from formula
            s.add(ctx.mkNot(ctx.mkEq(x, currentX)));
            // store result
            solutions.add(currentX.getInt());
        }

        assertEquals("[2, 3, 4]", solutions.toString());
    }
}
