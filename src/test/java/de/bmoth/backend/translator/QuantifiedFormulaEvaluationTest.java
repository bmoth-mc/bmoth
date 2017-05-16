package de.bmoth.backend.translator;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import de.bmoth.backend.FormulaToZ3Translator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class QuantifiedFormulaEvaluationTest {

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
    public void testExistentialFormula() throws Exception {
        String formula = "#(x).(x=2)";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);

        s.add(constraint);
        Status check = s.check();

        assertEquals(Status.SATISFIABLE, check);
    }

    @Test
    public void testUniversalFormula() throws Exception {
        String formula = "!(x).(x=TRUE or x=FALSE)";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);

        s.add(constraint);
        Status check = s.check();

        assertEquals(Status.SATISFIABLE, check);
    }

    @Test
    public void testFailExistentialFormula() throws Exception {
        String formula = "#(x).(x=2 & x=5)";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);

        s.add(constraint);
        Status check = s.check();

        assertEquals(Status.UNSATISFIABLE, check);
    }

    @Test
    public void testFailUniversalFormula() throws Exception {
        String formula = "!(x).(x=5)";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);

        s.add(constraint);
        Status check = s.check();

        assertEquals(Status.UNSATISFIABLE, check);
    }

    @Test
    public void testFailUniversalExistentialFormula() throws Exception {
        String formula = "#(y).(y:NATURAL & !(x).(x=y))";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);

        s.add(constraint);
        Status check = s.check();

        assertEquals(Status.UNSATISFIABLE, check);
    }

    @Test
    public void testUniversalExistentialFormula() throws Exception {
        String formula = "#(y).(!(x).(x*y=y))";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);

        s.add(constraint);
        Status check = s.check();

        assertEquals(Status.SATISFIABLE, check);
    }

}
