package de.bmoth.backend.translator;

import com.microsoft.z3.*;
import de.bmoth.backend.FormulaToZ3Translator;
import de.bmoth.backend.SolutionFinder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SolutionFinderTest {

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
    public void testSolutionFinder() throws Exception {
        String formula = "0 < a & a < 6 & 0 < b & b < 6 & ( 2 * b < a or 2 * b = a )";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);

        s.add(constraint);
        assertEquals(Status.SATISFIABLE, s.check());

        SolutionFinder finder = new SolutionFinder(constraint, s, ctx);
        Set<BoolExpr> solutions = finder.findSolutions(20);

        assertEquals(6, solutions.size());
        for (BoolExpr solution : solutions) {
            switch (solution.toString()) {
                case "(and (= a 2) (= b 1))":
                case "(and (= a 3) (= b 1))":
                case "(and (= a 4) (= b 1))":
                case "(and (= a 4) (= b 2))":
                case "(and (= a 5) (= b 1))":
                case "(and (= a 5) (= b 2))":
                    break;
                default:
                    fail(solution.toString() + " is not part of found solutions");
            }
        }
    }

    @Test
    public void testSolutionFinder2() throws Exception {
        String formula = "1 < x & x < 5";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);

        s.add(constraint);
        assertEquals(Status.SATISFIABLE, s.check());

        SolutionFinder finder = new SolutionFinder(constraint, s, ctx);
        Set<BoolExpr> solutions = finder.findSolutions(20);

        assertEquals(3, solutions.size());
        for (BoolExpr solution : solutions) {
            switch (solution.toString()) {
                case "(= x 2)":
                case "(= x 3)":
                case "(= x 4)":
                    break;
                default:
                    fail(solution.toString() + " is not part of found solutions");
            }
        }
    }

    @Test
    public void testSolutionFinder3() throws Exception {
        String formula = "0 < x & x < 5 & 1 < y & y < 6 & y < x";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);

        s.add(constraint);
        assertEquals(Status.SATISFIABLE, s.check());

        SolutionFinder finder = new SolutionFinder(constraint, s, ctx);
        Set<BoolExpr> solutions = finder.findSolutions(20);

        assertEquals(3, solutions.size());
        for (BoolExpr solution : solutions) {
            switch (solution.toString()) {
                case "(and (= y 2) (= x 3))":
                case "(and (= y 2) (= x 4))":
                case "(and (= y 3) (= x 4))":
                    break;
                default:
                    fail(solution.toString() + " is not part of found solutions");
            }
        }
    }

    @Test
    public void testAllSolutions() throws Exception {
        String formula = "1 < x & x < 5";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);

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
