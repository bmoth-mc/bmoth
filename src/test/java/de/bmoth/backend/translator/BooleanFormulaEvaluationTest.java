package de.bmoth.backend.translator;

import static com.microsoft.z3.Status.SATISFIABLE;
import static com.microsoft.z3.Status.UNSATISFIABLE;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

import de.bmoth.backend.Z3Translator;

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
        BoolExpr constraint = Z3Translator.translatePredicate(formula, ctx);

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
        BoolExpr constraint = Z3Translator.translatePredicate(formula, ctx);

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
        BoolExpr constraint = Z3Translator.translatePredicate(formula, ctx);

        s.add(constraint);
        Status check = s.check();

        Expr x = ctx.mkBoolConst("x");
        Expr y = ctx.mkBoolConst("y");

        assertEquals(Status.SATISFIABLE, check);
        assertEquals(ctx.mkTrue(), s.getModel().eval(x, true));
        assertEquals(ctx.mkTrue(), s.getModel().eval(y, true));
    }

    @Test
    public void testOrFormula() throws Exception {
        String formula = "FALSE or x";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = Z3Translator.translatePredicate(formula, ctx);

        s.add(constraint);
        Status check = s.check();

        Expr x = ctx.mkBoolConst("x");

        assertEquals(Status.SATISFIABLE, check);
        assertEquals(ctx.mkTrue(), s.getModel().eval(x, true));
    }

    @Test
    public void testSimpleBooleanFormula() throws Exception {
        String formula = "x = TRUE & y = FALSE";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = Z3Translator.translatePredicate(formula, ctx);

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
        // Note, rebuild the parser ("gradle clean build") if this test fails.

        String formula = "1=1 => x";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = Z3Translator.translatePredicate(formula, ctx);
        s.add(constraint);
        Status check = s.check();

        Expr x = ctx.mkBoolConst("x");
        assertEquals(Status.SATISFIABLE, check);
        assertEquals(ctx.mkBool(true), s.getModel().eval(x, true));
    }

    @Test
    public void testEquivalence() throws Exception {
        Map<String, Status> map = new HashMap<>();
        map.put("TRUE <=> TRUE", SATISFIABLE);
        map.put("FALSE <=> FALSE", SATISFIABLE);

        map.put("TRUE <=> FALSE", UNSATISFIABLE);
        map.put("FALSE <=> TRUE", UNSATISFIABLE);

        check(map);
    }

    private void check(Map<String, Status> map) {
        for (Entry<String, Status> entry : map.entrySet()) {
            System.out.println(entry.getKey());
            BoolExpr constraint = Z3Translator.translatePredicate(entry.getKey(), ctx);
            System.out.println(constraint);
            s.add(constraint);
            Status check = s.check();
            assertEquals(entry.getValue(), check);
            s.reset();
        }
    }

}
