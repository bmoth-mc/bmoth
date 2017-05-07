package de.bmoth.backend.translator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import com.microsoft.z3.*;
import de.bmoth.backend.SolutionFinder;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.bmoth.backend.FormulaToZ3Translator;
import de.bmoth.util.UtilMethodsTest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
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
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
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
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
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
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
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
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
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
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
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
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
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
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
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
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
        s.add(constraint);
        Status check = s.check();

        Expr x = ctx.mkIntConst("x");

        assertEquals(Status.SATISFIABLE, check);
        assertEquals(ctx.mkInt(256), s.getModel().eval(x, true));
    }

    @Test
    public void testLessThanFormula() throws Exception {
        String formula = "1 < 2";
        UtilMethodsTest.check(Status.SATISFIABLE, formula, ctx, s);
    }

    @Test
    public void testGreaterThanFormula() throws Exception {
        String formula = "2 > 1";
        UtilMethodsTest.check(Status.SATISFIABLE, formula, ctx, s);
    }

    @Test
    public void testLessEqualFormula() throws Exception {
        String formula = "x <= 4 & x > 0";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
        s.add(constraint);

        SolutionFinder finder = new SolutionFinder(constraint, s, ctx);
        Set<Model> solutions = finder.findSolutions(20);

        assertEquals(4, solutions.size());

        for (Model solution : solutions) {
            String solutionAsString = z3ModelToString(solution);
            switch (solutionAsString) {
                case "{x=1}":
                case "{x=2}":
                case "{x=3}":
                case "{x=4}":
                    break;
                default:
                    fail(solutionAsString + " is not part of found solutions");
            }
        }
    }

    @Test
    public void testGreaterEqualFormula() throws Exception {
        String formula = "x >= 4 & x < 8";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
        s.add(constraint);

        SolutionFinder finder = new SolutionFinder(constraint, s, ctx);
        Set<Model> solutions = finder.findSolutions(20);

        assertEquals(4, solutions.size());

        for (Model solution : solutions) {
            String solutionAsString = z3ModelToString(solution);
            switch (solutionAsString) {
                case "{x=4}":
                case "{x=5}":
                case "{x=6}":
                case "{x=7}":
                    break;
                default:
                    fail(solutionAsString + " is not part of found solutions");
            }
        }
    }

    static String z3ModelToString(Model m) {
        Map<String,String> values = new HashMap<>();
        for(FuncDecl constant : m.getConstDecls()) {
            String value = m.eval(constant.apply(),true).toString();
            values.put(constant.apply().toString(),value);
        }
        return values.toString();
    }
}
