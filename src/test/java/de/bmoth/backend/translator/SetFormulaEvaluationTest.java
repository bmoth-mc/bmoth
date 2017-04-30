package de.bmoth.backend.translator;

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
import static com.microsoft.z3.Status.*;

import de.bmoth.backend.FormulaToZ3Translator;

public class SetFormulaEvaluationTest {

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
    public void testSimpleSetExtensionFormula() throws Exception {
        String formula = "{1,2} = {2,3}";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);

        s.add(constraint);
        Status check = s.check();

        assertEquals(Status.UNSATISFIABLE, check);
    }

    @Test
    public void testSetExtensionFormulaWithSingleVarModel() throws Exception {
        String formula = "{1,2} = {2,x}";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);

        s.add(constraint);
        Status check = s.check();

        Expr x = ctx.mkIntConst("x");

        assertEquals(Status.SATISFIABLE, check);
        assertEquals(ctx.mkInt(1), s.getModel().eval(x, true));
    }

    @Test
    public void testSetExtensionFormulaWithSetVarModel() throws Exception {
        String formula = "{1,2} = x";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);

        s.add(constraint);
        Status check = s.check();

        Expr x = ctx.mkArrayConst("x", ctx.mkIntSort(), ctx.mkBoolSort());

        assertEquals(Status.SATISFIABLE, check);
        assertEquals("(store (store ((as const (Array Int Bool)) false) 1 true) 2 true)",
                s.getModel().eval(x, true).toString());
    }

    @Test
    public void testSetMembership() throws Exception {
        String formula = "x : {3}";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);

        s.add(constraint);
        Status check = s.check();

        Expr x = ctx.mkIntConst("x");

        assertEquals(Status.SATISFIABLE, check);
        assertEquals("3", s.getModel().eval(x, true).toString());
    }

    @Test
    public void testNATURAL() throws Exception {
        Map<String, Status> map = new HashMap<>();
        map.put("0 : NATURAL", SATISFIABLE);
        map.put("1 : NATURAL", SATISFIABLE);
        map.put("1000000 : NATURAL", SATISFIABLE);
        map.put("-1 : NATURAL", UNSATISFIABLE);
        map.put("-10000 : NATURAL", UNSATISFIABLE);

        check(map);
    }

    private void check(Map<String, Status> map) {
        for (Entry<String, Status> entry : map.entrySet()) {
            System.out.println(entry.getKey());
            BoolExpr constraint = FormulaToZ3Translator.translatePredicate(entry.getKey(), ctx);
            System.out.println(constraint);
            s.add(constraint);
            Status check = s.check();
            assertEquals(entry.getValue(), check);
            s.reset();
        }
    }

}
