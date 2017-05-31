package de.bmoth.backend.translator;

import com.microsoft.z3.*;
import de.bmoth.TestUsingZ3;
import de.bmoth.backend.z3.FormulaToZ3Translator;
import de.bmoth.backend.z3.SolutionFinder;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class FormulaEvaluationTest extends TestUsingZ3 {
    @Test
    public void testAdditionFormula() throws Exception {
        String formula = "x = 2 + 3";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();

        Expr x = z3Context.mkIntConst("x");

        assertEquals(Status.SATISFIABLE, check);
        assertEquals(z3Context.mkInt(5), z3Solver.getModel().eval(x, true));
    }

    @Test
    public void testSubtractionFormula() throws Exception {
        String formula = "x = 2 - 3";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();

        Expr x = z3Context.mkIntConst("x");

        assertEquals(Status.SATISFIABLE, check);
        assertEquals(z3Context.mkInt(-1), z3Solver.getModel().eval(x, true));
    }

    @Test
    public void testEqualityFormula() throws Exception {
        String formula = "x = 5";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();

        Expr x = z3Context.mkIntConst("x");

        assertEquals(Status.SATISFIABLE, check);
        assertEquals(z3Context.mkInt(5), z3Solver.getModel().eval(x, true));
    }

    @Test
    public void testInequalityFormula() throws Exception {
        String formula = "x /= 0";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();

        Expr x = z3Context.mkIntConst("x");

        assertEquals(Status.SATISFIABLE, check);
        assertNotEquals(z3Context.mkInt(0), z3Solver.getModel().eval(x, true));
    }

    @Test
    public void testModuloFormula() throws Exception {
        String formula = "x = 3 mod 2";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();

        Expr x = z3Context.mkIntConst("x");

        assertEquals(Status.SATISFIABLE, check);
        assertEquals(z3Context.mkInt(1), z3Solver.getModel().eval(x, true));
    }

    @Test
    public void testMultiplicationFormula() throws Exception {
        String formula = "x = 3 * 2";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();

        Expr x = z3Context.mkIntConst("x");

        assertEquals(Status.SATISFIABLE, check);
        assertEquals(z3Context.mkInt(6), z3Solver.getModel().eval(x, true));
    }

    @Test
    public void testAddMulOrderFormula() throws Exception {
        /**
         * Tests order of multiplication and addition
         */
        String formula = "x = 4 + 3 * 2 * 2";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();

        Expr x = z3Context.mkIntConst("x");

        assertEquals(Status.SATISFIABLE, check);
        assertEquals(z3Context.mkInt(16), z3Solver.getModel().eval(x, true));
    }

    @Test
    public void testDivisionFormula() throws Exception {
        String formula = "x = 8 / 2";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();

        Expr x = z3Context.mkIntConst("x");

        assertEquals(Status.SATISFIABLE, check);
        assertEquals(z3Context.mkInt(4), z3Solver.getModel().eval(x, true));
    }

    @Test
    public void testDivisionFormula2() throws Exception {
        String formula = "x = 1 / 0";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();

        assertEquals(Status.UNSATISFIABLE, check);
    }

    @Test
    public void testPowerFormula() throws Exception {
        String formula = "x = 2 ** 8";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();

        Expr x = z3Context.mkIntConst("x");

        assertEquals(Status.SATISFIABLE, check);
        assertEquals(z3Context.mkInt(256), z3Solver.getModel().eval(x, true));
    }

    @Test
    public void testLessThanFormula() throws Exception {
        String formula = "1 < 2";
        check(Status.SATISFIABLE, formula);
    }

    @Test
    public void testGreaterThanFormula() throws Exception {
        String formula = "2 > 1";
        check(Status.SATISFIABLE, formula);
    }

    @Test
    public void testLessEqualFormula() throws Exception {
        String formula = "x <= 4 & x > 0";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);

        SolutionFinder finder = new SolutionFinder(constraint, z3Solver, z3Context);
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
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);

        SolutionFinder finder = new SolutionFinder(constraint, z3Solver, z3Context);
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
        Map<String, String> values = new HashMap<>();
        for (FuncDecl constant : m.getConstDecls()) {
            String value = m.eval(constant.apply(), true).toString();
            values.put(constant.apply().toString(), value);
        }
        return values.toString();
    }
}
