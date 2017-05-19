package de.bmoth.backend.translator;

import com.microsoft.z3.*;
import de.bmoth.app.PersonalPreferences;
import de.bmoth.backend.z3.FormulaToZ3Translator;
import de.bmoth.util.UtilMethodsTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static com.microsoft.z3.Status.SATISFIABLE;
import static com.microsoft.z3.Status.UNSATISFIABLE;
import static org.junit.Assert.assertEquals;

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
    public void testSimpleSetExtensionFormula() {
        String formula = "{1,2} = {2,3}";
        UtilMethodsTest.check(UNSATISFIABLE, formula, ctx, s);
    }

    @Test
    public void testEmptySet() {
        UtilMethodsTest.check(Status.SATISFIABLE, "1 /: {}", ctx, s);
        UtilMethodsTest.check(Status.UNSATISFIABLE, "{1} = {}", ctx, s);
        UtilMethodsTest.check(Status.UNSATISFIABLE, "{} = {1}", ctx, s);
    }

    @Test
    public void testSetExtensionFormulaWithSingleVarModel() {
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
    public void testSetExtensionFormulaWithSetVarModel() {
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
    public void testSetMembership() {
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
    public void testNATURAL() {
        UtilMethodsTest.check(SATISFIABLE, "0 : NATURAL", ctx, s);
        UtilMethodsTest.check(SATISFIABLE, "1 : NATURAL", ctx, s);
        UtilMethodsTest.check(SATISFIABLE, "1000000 : NATURAL", ctx, s);
        UtilMethodsTest.check(UNSATISFIABLE, "-1 : NATURAL", ctx, s);
        UtilMethodsTest.check(UNSATISFIABLE, "-10000 : NATURAL", ctx, s);
    }

    @Test
    public void testNAT() {
        PersonalPreferences.setIntPreference(PersonalPreferences.IntPreference.MAX_INT, "10");
        UtilMethodsTest.check(SATISFIABLE, "0 : NAT", ctx, s);
        UtilMethodsTest.check(SATISFIABLE, "1 : NAT", ctx, s);
        UtilMethodsTest.check(UNSATISFIABLE, "-1 : NAT", ctx, s);
        UtilMethodsTest.check(UNSATISFIABLE, "-10000 : NAT", ctx, s);
        UtilMethodsTest.check(SATISFIABLE, "10 : INT", ctx, s);
        UtilMethodsTest.check(UNSATISFIABLE, "11 : INT", ctx, s);
    }

    @Test
    public void testINTEGER() {
        UtilMethodsTest.check(SATISFIABLE, "0 : INTEGER", ctx, s);
        UtilMethodsTest.check(SATISFIABLE, "1 : INTEGER", ctx, s);
        UtilMethodsTest.check(SATISFIABLE, "-1 : INTEGER", ctx, s);
    }

    @Test
    public void testINT() {
        PersonalPreferences.setIntPreference(PersonalPreferences.IntPreference.MAX_INT, "10");
        PersonalPreferences.setIntPreference(PersonalPreferences.IntPreference.MIN_INT, "5");
        //UtilMethodsTest.check(SATISFIABLE, "0 : INT", ctx, s);
        //UtilMethodsTest.check(SATISFIABLE, "1 : INT", ctx, s);
        //UtilMethodsTest.check(SATISFIABLE, "-1 : INT", ctx, s);
        UtilMethodsTest.check(SATISFIABLE, "10 : INT", ctx, s);
        UtilMethodsTest.check(UNSATISFIABLE, "11 : INT", ctx, s);
        UtilMethodsTest.check(SATISFIABLE, "6 : INT", ctx, s);
        UtilMethodsTest.check(UNSATISFIABLE, "4 : INT", ctx, s);
    }

    @Test
    public void testSetComprehension1() {
        String formula = "{x | x : {1} } = {1} ";
        UtilMethodsTest.check(SATISFIABLE, formula, ctx, s);
    }

    @Test
    public void testSetComprehension2() {
        String formula = "{x,y | x : {1,2,3} & y = 2} = {1|->2, 2|->2, 3|->2} ";
        UtilMethodsTest.check(SATISFIABLE, formula, ctx, s);
    }

    @Test
    public void testSetComprehension3() {
        String formula = "{x | x : {1} } = {x | x > 0 & x < 2} ";
        UtilMethodsTest.check(SATISFIABLE, formula, ctx, s);
    }

    @Test
    public void testSubset() {
        String formula = "{1} <: {1,2}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
        s.add(constraint);
        Status check = s.check();
        assertEquals(Status.SATISFIABLE, check);
    }

    @Test
    public void testSubset2() {
        String formula = "{1} <: {3,2}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
        s.add(constraint);
        Status check = s.check();
        assertEquals(Status.UNSATISFIABLE, check);
    }

    @Test
    public void testStrictSubset() {
        String formula = "{1} <<: {1,2}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
        s.add(constraint);
        Status check = s.check();
        assertEquals(Status.SATISFIABLE, check);
    }

    @Test
    public void testStrictSubset2() {
        String formula = "{1} <<: {1}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
        s.add(constraint);
        Status check = s.check();
        assertEquals(Status.UNSATISFIABLE, check);
    }

    @Test
    public void testStrictSubset3() {
        String formula = "{1} <<: {1,1}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
        s.add(constraint);
        Status check = s.check();
        assertEquals(Status.UNSATISFIABLE, check);
    }

    @Test
    public void testnoSubset() {
        String formula = "{1} /<: {1}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
        s.add(constraint);
        Status check = s.check();
        assertEquals(Status.UNSATISFIABLE, check);
    }

    @Test
    public void testnoSubset2() {
        String formula = "{1} /<: {2,3}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
        s.add(constraint);
        Status check = s.check();
        assertEquals(Status.SATISFIABLE, check);
    }

    @Test
    public void testnoSubset3() {
        String formula = "{1} /<: {1,2}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
        s.add(constraint);
        Status check = s.check();
        assertEquals(Status.UNSATISFIABLE, check);
    }

    @Test
    public void testNoProperSubset() {
        String formula = "{1} /<<: {1}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
        s.add(constraint);
        Status check = s.check();
        assertEquals(Status.SATISFIABLE, check);
    }

    @Test
    public void testNoProperSubset2() {
        String formula = "{1} /<<: {1,2}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
        s.add(constraint);
        Status check = s.check();
        assertEquals(Status.UNSATISFIABLE, check);
    }

    @Test
    public void testNoProperSubset3() {
        String formula = "{1} /<<: {2,3}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
        s.add(constraint);
        Status check = s.check();
        assertEquals(Status.SATISFIABLE, check);
    }

    @Test
    public void testNotBelonging() {
        String formula = "1 /: {2,3}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
        s.add(constraint);
        Status check = s.check();
        assertEquals(Status.SATISFIABLE, check);
    }

    @Test
    public void testNotBelonging2() {
        String formula = "{1} /: {{1}}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
        s.add(constraint);
        Status check = s.check();
        assertEquals(Status.UNSATISFIABLE, check);
    }

    @Test
    public void testBelonging() {
        String formula = "1 : {1}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
        s.add(constraint);
        Status check = s.check();
        assertEquals(Status.SATISFIABLE, check);
    }

    @Test
    public void testBelonging2() {
        String formula = "x: {1}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
        s.add(constraint);
        Status check = s.check();
        assertEquals(Status.SATISFIABLE, check);
    }

    @Test
    public void testBelonging3() {
        String formula = "1 : {2,3}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
        s.add(constraint);
        Status check = s.check();
        assertEquals(Status.UNSATISFIABLE, check);
    }

    @Test
    public void testIntersection() {
        String formula = "{1,2} /\\ {2,3} = {2}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
        s.add(constraint);
        Status check = s.check();
        assertEquals(Status.SATISFIABLE, check);
    }

    @Test
    public void testIntersection2() {
        String formula = "{1} /\\ {2,3} = {2}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
        s.add(constraint);
        Status check = s.check();
        assertEquals(Status.UNSATISFIABLE, check);
    }

    @Test
    public void testUnion() {
        String formula = "{1,2} \\/ {2,3} = {2}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
        s.add(constraint);
        Status check = s.check();
        assertEquals(Status.UNSATISFIABLE, check);
    }

    @Test
    public void testUnion2() {
        String formula = "{1,2} \\/ {2,3} = {1,2,3}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
        s.add(constraint);
        Status check = s.check();
        assertEquals(Status.SATISFIABLE, check);
    }


    @Test
    public void testDifference() {
        String formula = "{1,2}\\{2,3} = {1}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
        s.add(constraint);
        Status check = s.check();
        assertEquals(Status.SATISFIABLE, check);
    }

    @Test
    public void testDifference2() {
        String formula = "{1,2}\\{3} = {1}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
        s.add(constraint);
        Status check = s.check();
        assertEquals(Status.UNSATISFIABLE, check);
    }

    @Test
    public void testAdditionFormula() {
        String formula = "x : 2 .. 3";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
        s.add(constraint);
        Status check = s.check();

        assertEquals(Status.SATISFIABLE, check);
    }

    @Test
    public void testFailAdditionFormula() {
        String formula = "x : 2 .. 3 & x > 3";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
        s.add(constraint);
        Status check = s.check();

        assertEquals(Status.UNSATISFIABLE, check);
    }


    @Test
    public void testCartesianProduct() {
        String formula = "{1}*{2}={(1,2)}";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
        s.add(constraint);
        Status check = s.check();
        assertEquals(Status.SATISFIABLE, check);
    }

    @Test
    public void testCartesianProductUnsat() {
        String formula = "{1}*{2}={(1,3)}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, ctx);
        s.add(constraint);
        Status check = s.check();
        assertEquals(Status.UNSATISFIABLE, check);
    }

    @Ignore
    @Test
    public void testGeneralizedUnion() {
        // TODO z3 is currently not able to handle sets of sets and reports the
        // status UNKNOWN
        String formula = "union({{1},{2},{3}}) = {1,2,3} ";
        UtilMethodsTest.check(SATISFIABLE, formula, ctx, s);
    }

    @Ignore
    @Test
    public void testCard() {
        String formula = "card({1,2,3,4}) = 3";
        UtilMethodsTest.check(SATISFIABLE, formula, ctx, s);
    }

}
