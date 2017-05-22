package de.bmoth.backend.translator;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Status;
import de.bmoth.TestUsingZ3;
import de.bmoth.backend.z3.FormulaToZ3Translator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class QuantifiedFormulaEvaluationTest extends TestUsingZ3 {
    @Test
    public void testExistentialFormula() {
        String formula = "#(x).(x=2)";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);

        z3Solver.add(constraint);
        Status check = z3Solver.check();

        assertEquals(Status.SATISFIABLE, check);
    }

    @Test
    public void testUniversalFormula() {
        String formula = "!(x).(x=TRUE or x=FALSE)";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);

        z3Solver.add(constraint);
        Status check = z3Solver.check();

        assertEquals(Status.SATISFIABLE, check);
    }

    @Test
    public void testFailExistentialFormula() {
        String formula = "#(x).(x=2 & x=5)";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);

        z3Solver.add(constraint);
        Status check = z3Solver.check();

        assertEquals(Status.UNSATISFIABLE, check);
    }

    @Test
    public void testFailUniversalFormula() {
        String formula = "!(x).(x=5)";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);

        z3Solver.add(constraint);
        Status check = z3Solver.check();

        assertEquals(Status.UNSATISFIABLE, check);
    }

    @Test
    public void testFailUniversalExistentialFormula() {
        String formula = "#(y).(y:NATURAL & !(x).(x=y))";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);

        z3Solver.add(constraint);
        Status check = z3Solver.check();

        assertEquals(Status.UNSATISFIABLE, check);
    }

    @Test
    public void testUniversalExistentialFormula() {
        String formula = "#(y).(y:NATURAL & !(x).(x*y=y))";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);

        z3Solver.add(constraint);
        Status check = z3Solver.check();

        assertEquals(Status.SATISFIABLE, check);
    }

}
