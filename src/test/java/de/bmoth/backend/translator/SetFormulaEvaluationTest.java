package de.bmoth.backend.translator;

import static org.junit.Assert.assertEquals;

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
import de.bmoth.util.UtilMethodsTest;

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
        UtilMethodsTest.check(UNSATISFIABLE, formula, ctx, s);
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
        UtilMethodsTest.check(SATISFIABLE, "0 : NATURAL", ctx, s);
        UtilMethodsTest.check(SATISFIABLE, "1 : NATURAL", ctx, s);
        UtilMethodsTest.check(SATISFIABLE, "1000000 : NATURAL", ctx, s);
        UtilMethodsTest.check(UNSATISFIABLE, "-1 : NATURAL", ctx, s);
        UtilMethodsTest.check(UNSATISFIABLE, "-10000 : NATURAL", ctx, s);
    }

    @Test
    public void testSetComprehension1() throws Exception {
        String formula = "{x | x : {1} } = {1} ";
        UtilMethodsTest.check(SATISFIABLE, formula, ctx, s);
    }

    @Test
    public void testSetComprehension2() throws Exception {
        String formula = "{x,y | x : {1,2,3} & y = 2} = {1|->2, 2|->2, 3|->2} ";
        UtilMethodsTest.check(SATISFIABLE, formula, ctx, s);
    }

    @Test
    public void testSetComprehension3() throws Exception {
        String formula = "{x | x : {1} } = {x | x > 0 & x < 2} ";
        UtilMethodsTest.check(SATISFIABLE, formula, ctx, s);
    }

    @Ignore
    @Test
    public void testGeneralizedUnion() throws Exception {
        // TODO z3 is currently not able to handle sets of sets and reports the
        // status UNKNOWN
        String formula = "union({{1},{2},{3}}) = {1,2,3} ";
        UtilMethodsTest.check(SATISFIABLE, formula, ctx, s);
    }


    @Ignore
    @Test
    public void testCard() throws Exception {
        String formula = "card({1,2,3,4}) = 3";
        UtilMethodsTest.check(SATISFIABLE, formula, ctx, s);
    }

}
