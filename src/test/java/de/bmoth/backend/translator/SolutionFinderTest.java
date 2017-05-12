package de.bmoth.backend.translator;

import com.microsoft.z3.*;
import de.bmoth.backend.FormulaToZ3Translator;
import de.bmoth.backend.SolutionFinder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

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
    public void testSolutionFinder1() throws Exception {
        String formula = "a : NATURAL & a < 1";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
        SolutionFinder finder = new SolutionFinder(constraint, s, ctx);
        Set<Model> solutions = finder.findSolutions(20);
        assertEquals(1, solutions.size());
    }

    @Test
    public void testExistsSolutionFinder() throws Exception {
        String formula = "#x.(x : {1,2} & a = x)";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
        SolutionFinder finder = new SolutionFinder(constraint, s, ctx);
        Set<Model> solutions = finder.findSolutions(20);
        assertEquals(2, solutions.size());
    }

    @Test
    public void testExistsSolutionFinder2() throws Exception {
        String formula = "#a,b,c.(c = TRUE & a : {1,2} & b : {1,2} & a /= b & x = a+b)";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
        SolutionFinder finder = new SolutionFinder(constraint, s, ctx);
        Set<Model> solutions = finder.findSolutions(20);
        //all existentially quantified variables are part of the model
        //{c!0=true, a!2=2, b!1=1, x=3}
        //{c!0=true, a!2=1, b!1=2, x=3}
        assertEquals(2, solutions.size());
    }

    @Test
    public void testSolutionFinder() throws Exception {
        String formula = "0 < a & a < 6 & 0 < b & b < 6 & ( 2 * b < a or 2 * b = a )";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);

        s.add(constraint);
        assertEquals(Status.SATISFIABLE, s.check());

        SolutionFinder finder = new SolutionFinder(constraint, s, ctx);
        Set<Model> solutions = finder.findSolutions(20);

        assertEquals(6, solutions.size());

        for (Model solution : solutions) {
            String solutionAsString = z3ModelToString(solution);
            switch (solutionAsString) {
                case "{a=2, b=1}":
                case "{a=3, b=1}":
                case "{a=4, b=1}":
                case "{a=4, b=2}":
                case "{a=5, b=1}":
                case "{a=5, b=2}":
                case "{a=5, b=3}":
                    break;
                default:
                    fail(solutionAsString + " is not part of found solutions");
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
        Set<Model> solutions = finder.findSolutions(20);

        assertEquals(3, solutions.size());

        for (Model solution : solutions) {
            String solutionAsString = z3ModelToString(solution);
            switch (solutionAsString) {
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
    public void testSolutionFinder3() throws Exception {
        String formula = "0 < x & x < 5 & 1 < y & y < 6 & y < x";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);

        s.add(constraint);
        assertEquals(Status.SATISFIABLE, s.check());

        SolutionFinder finder = new SolutionFinder(constraint, s, ctx);
        Set<Model> solutions = finder.findSolutions(20);

        assertEquals(3, solutions.size());
        for (Model solution : solutions) {
            String solutionAsString = z3ModelToString(solution);
            switch (solutionAsString) {
                case "{x=3, y=2}":
                case "{x=4, y=2}":
                case "{x=4, y=3}":
                    break;
                default:
                    fail(solutionAsString + " is not part of found solutions");
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
        // credit goes to:
        // http://stackoverflow.com/questions/13395391/z3-finding-all-satisfying-models#answer-13398853

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

    @Test
    public void testSolutionFinder4() throws Exception {
        String formula = "a > 0";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);

        SolutionFinder finder = new SolutionFinder(constraint, s, ctx);
        Set<Model> solutions = finder.findSolutions(20);
        assertEquals(20, solutions.size());
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
