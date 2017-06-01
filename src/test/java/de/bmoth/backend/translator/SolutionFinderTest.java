package de.bmoth.backend.translator;

import com.microsoft.z3.*;
import de.bmoth.TestUsingZ3;
import de.bmoth.backend.z3.FormulaToZ3Translator;
import de.bmoth.backend.z3.SolutionFinder;
import de.bmoth.preferences.BMothPreferences;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SolutionFinderTest extends TestUsingZ3 {

    private SolutionFinder finder;

    @Before
    @Override
    public void setup() {
        super.setup();
        finder = new SolutionFinder(z3Solver, z3Context);

    }


    @Test
    public void testSolutionFinder1() {
        String formula = "a : NATURAL & a < 1";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        Set<Model> solutions = finder.findSolutions(constraint, 20);
        assertEquals(1, solutions.size());
    }

    @Test
    public void testSolutionFinderNATUpperFail() {
        String maxInt = String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INT));
        String formula = new StringBuilder().append("a : NAT & a > ").append(maxInt).toString();
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        Set<Model> solutions = finder.findSolutions(constraint, 20);
        assertEquals(0, solutions.size());
    }

    @Test
    public void testSolutionFinderNATUpper() {
        String oneBelowMaxInt = String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INT) - 1);
        String formula = new StringBuilder().append("a : NAT & a > ").append(oneBelowMaxInt).toString();
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        Set<Model> solutions = finder.findSolutions(constraint, 20);
        assertEquals(1, solutions.size());
    }

    @Test
    public void testSolutionFinderNAT1UpperFail() {
        String formula = "a : NATURAL1 & a < 1";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        Set<Model> solutions = finder.findSolutions(constraint, 20);
        assertEquals(0, solutions.size());
    }

    @Test
    public void testSolutionFinderNAT1Upper() {
        String formula = "a : NATURAL1 & a < 2";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        Set<Model> solutions = finder.findSolutions(constraint, 20);
        assertEquals(1, solutions.size());
    }


    @Test
    public void testExistsSolutionFinder() {
        String formula = "#x.(x : {1,2} & a = x)";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        Set<Model> solutions = finder.findSolutions(constraint, 20);
        assertEquals(2, solutions.size());
    }

    @Test
    public void testExistsSolutionFinder2() {
        String formula = "#a,b,c.(c = TRUE & a : {1,2} & b : {1,2} & a /= b & x = a+b)";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        Set<Model> solutions = finder.findSolutions(constraint, 20);
        //all existentially quantified variables are part of the model
        //{c!0=true, a!2=2, b!1=1, x=3}
        //{c!0=true, a!2=1, b!1=2, x=3}
        assertEquals(2, solutions.size());
    }

    @Test
    public void testSolutionFinder() {
        String formula = "0 < a & a < 6 & 0 < b & b < 6 & ( 2 * b < a or 2 * b = a )";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);

        z3Solver.add(constraint);
        assertEquals(Status.SATISFIABLE, z3Solver.check());

        Set<Model> solutions = finder.findSolutions(constraint, 20);

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
    public void testSolutionFinder2() {
        String formula = "1 < x & x < 5";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);

        z3Solver.add(constraint);
        assertEquals(Status.SATISFIABLE, z3Solver.check());

        Set<Model> solutions = finder.findSolutions(constraint, 20);

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
    public void testSolutionFinder3() {
        String formula = "0 < x & x < 5 & 1 < y & y < 6 & y < x";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);

        z3Solver.add(constraint);
        assertEquals(Status.SATISFIABLE, z3Solver.check());

        Set<Model> solutions = finder.findSolutions(constraint, 20);

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
    public void testAllSolutions() {
        String formula = "1 < x & x < 5";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);

        z3Solver.add(constraint);
        Expr x = z3Context.mkIntConst("x");

        // 1st try: brute force over 'all' satisfying solutions
        // credit goes to:
        // http://stackoverflow.com/questions/13395391/z3-finding-all-satisfying-models#answer-13398853

        List<Number> solutions = new ArrayList<>();

        // as long as formula is satisfiable
        for (int i = 0; z3Solver.check() == Status.SATISFIABLE && i < 10; i++) {
            // get current evaluation for x
            IntNum currentX = (IntNum) z3Solver.getModel().eval(x, true);
            // and exclude it from formula
            z3Solver.add(z3Context.mkNot(z3Context.mkEq(x, currentX)));
            // store result
            solutions.add(currentX.getInt());
        }

        assertEquals("[2, 3, 4]", solutions.toString());
    }

    @Test
    public void testSolutionFinder4() {
        String formula = "a > 0";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);

        Set<Model> solutions = finder.findSolutions(constraint, 20);
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
