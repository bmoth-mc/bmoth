package de.bmoth.backend.translator;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Status;
import de.bmoth.TestUsingZ3;
import de.bmoth.backend.z3.FormulaToZ3Translator;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.microsoft.z3.Status.SATISFIABLE;
import static com.microsoft.z3.Status.UNSATISFIABLE;
import static org.junit.Assert.assertEquals;

public class BooleanFormulaEvaluationTest extends TestUsingZ3 {

    @Test
    public void testTrueFormula() throws Exception {
        String formula = "x = TRUE";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();

        Expr x = z3Context.mkBoolConst("x");

        assertEquals(SATISFIABLE, check);
        assertEquals(z3Context.mkTrue(), z3Solver.getModel().eval(x, true));
    }

    @Test
    public void testFalseFormula() throws Exception {
        String formula = "x = FALSE";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();

        Expr x = z3Context.mkBoolConst("x");

        assertEquals(SATISFIABLE, check);
        assertEquals(z3Context.mkFalse(), z3Solver.getModel().eval(x, true));
    }

    @Test
    public void testAndFormula() throws Exception {
        Map<String, Status> map = new HashMap<>();
        map.put("TRUE & TRUE", SATISFIABLE);
        map.put("TRUE & x", SATISFIABLE);
        map.put("TRUE & FALSE", UNSATISFIABLE);
        map.put("FALSE & TRUE", UNSATISFIABLE);
        map.put("FALSE & FALSE", UNSATISFIABLE);
        map.put("FALSE & x", UNSATISFIABLE);
        checkTruthTable(map);
    }

    @Test
    public void testOrFormula() throws Exception {
        Map<String, Status> map = new HashMap<>();
        map.put("TRUE or TRUE", SATISFIABLE);
        map.put("TRUE or FALSE", SATISFIABLE);
        map.put("FALSE or TRUE", SATISFIABLE);
        map.put("FALSE or FALSE", UNSATISFIABLE);
        checkTruthTable(map);

    }

    @Test
    public void testSimpleBooleanFormula() throws Exception {
        String formula = "x = TRUE & y = FALSE";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();

        Expr x = z3Context.mkBoolConst("x");
        Expr y = z3Context.mkBoolConst("y");

        assertEquals(Status.SATISFIABLE, check);
        assertEquals(z3Context.mkBool(true), z3Solver.getModel().eval(x, true));
        assertEquals(z3Context.mkBool(false), z3Solver.getModel().eval(y, false));

    }

    @Test
    public void testImplication() throws Exception {
        String formula = "1=1 => x";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();

        Expr x = z3Context.mkBoolConst("x");
        assertEquals(SATISFIABLE, check);
        assertEquals(z3Context.mkBool(true), z3Solver.getModel().eval(x, true));
    }

    @Test
    public void testImplication2() throws Exception {
        Map<String, Status> map = new HashMap<>();
        map.put("TRUE => TRUE", SATISFIABLE);
        map.put("TRUE => FALSE", UNSATISFIABLE);
        map.put("FALSE => TRUE", SATISFIABLE);
        map.put("FALSE => FALSE", SATISFIABLE);
        checkTruthTable(map);
    }

    @Test
    public void testEquivalence() throws Exception {
        Map<String, Status> map = new HashMap<>();
        map.put("TRUE <=> TRUE", SATISFIABLE);
        map.put("FALSE <=> FALSE", SATISFIABLE);
        map.put("TRUE <=> FALSE", UNSATISFIABLE);
        map.put("FALSE <=> TRUE", UNSATISFIABLE);
        checkTruthTable(map);
    }

    @Test
    public void testBoolCast() throws Exception {
        Map<String, Status> map = new HashMap<>();
        map.put("TRUE = bool(1<5)", SATISFIABLE);
        map.put("FALSE = bool(5<1)", SATISFIABLE);
        map.put("FALSE = bool(1<5)", UNSATISFIABLE);
        map.put("TRUE = bool(5<1)", UNSATISFIABLE);
        checkTruthTable(map);
    }

}
