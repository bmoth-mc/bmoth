package de.bmoth.backend.translator;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Status;
import de.bmoth.TestUsingZ3;
import de.bmoth.app.Preferences;
import de.bmoth.backend.z3.FormulaToZ3Translator;
import de.bmoth.util.UtilMethodsTest;
import org.junit.Ignore;
import org.junit.Test;

import static com.microsoft.z3.Status.SATISFIABLE;
import static com.microsoft.z3.Status.UNSATISFIABLE;
import static org.junit.Assert.assertEquals;

public class SetFormulaEvaluationTest extends TestUsingZ3 {
    @Test
    public void testSimpleSetExtensionFormula() {
        String formula = "{1,2} = {2,3}";
        UtilMethodsTest.check(UNSATISFIABLE, formula, z3Context, z3Solver);
    }

    @Test
    public void testEmptySet() {
        UtilMethodsTest.check(Status.SATISFIABLE, "1 /: {}", z3Context, z3Solver);
        UtilMethodsTest.check(Status.UNSATISFIABLE, "{1} = {}", z3Context, z3Solver);
        UtilMethodsTest.check(Status.UNSATISFIABLE, "{} = {1}", z3Context, z3Solver);
    }

    @Test
    public void testSetExtensionFormulaWithSingleVarModel() {
        String formula = "{1,2} = {2,x}";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();

        Expr x = z3Context.mkIntConst("x");

        assertEquals(Status.SATISFIABLE, check);
        assertEquals(z3Context.mkInt(1), z3Solver.getModel().eval(x, true));
    }

    @Test
    public void testSetExtensionFormulaWithSetVarModel() {
        String formula = "{1,2} = x";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();

        Expr x = z3Context.mkArrayConst("x", z3Context.mkIntSort(), z3Context.mkBoolSort());

        assertEquals(Status.SATISFIABLE, check);
        assertEquals("(store (store ((as const (Array Int Bool)) false) 1 true) 2 true)",
            z3Solver.getModel().eval(x, true).toString());
    }

    @Test
    public void testSetMembership() {
        String formula = "x : {3}";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();

        Expr x = z3Context.mkIntConst("x");

        assertEquals(Status.SATISFIABLE, check);
        assertEquals("3", z3Solver.getModel().eval(x, true).toString());
    }

    @Test
    public void testNATURAL() {
        UtilMethodsTest.check(SATISFIABLE, "0 : NATURAL", z3Context, z3Solver);
        UtilMethodsTest.check(SATISFIABLE, "1 : NATURAL", z3Context, z3Solver);
        UtilMethodsTest.check(SATISFIABLE, "1000000 : NATURAL", z3Context, z3Solver);
        UtilMethodsTest.check(UNSATISFIABLE, "-1 : NATURAL", z3Context, z3Solver);
        UtilMethodsTest.check(UNSATISFIABLE, "-10000 : NATURAL", z3Context, z3Solver);
    }

    @Test
    public void testNAT() {
        Preferences.setIntPreference(Preferences.IntPreference.MAX_INT, "10");
        UtilMethodsTest.check(SATISFIABLE, "0 : NAT", z3Context, z3Solver);
        UtilMethodsTest.check(SATISFIABLE, "1 : NAT", z3Context, z3Solver);
        UtilMethodsTest.check(UNSATISFIABLE, "-1 : NAT", z3Context, z3Solver);
        UtilMethodsTest.check(UNSATISFIABLE, "-10000 : NAT", z3Context, z3Solver);
        UtilMethodsTest.check(SATISFIABLE, "10 : INT", z3Context, z3Solver);
        UtilMethodsTest.check(UNSATISFIABLE, "11 : INT", z3Context, z3Solver);
    }

    @Test
    public void testINTEGER() {
        UtilMethodsTest.check(SATISFIABLE, "0 : INTEGER", z3Context, z3Solver);
        UtilMethodsTest.check(SATISFIABLE, "1 : INTEGER", z3Context, z3Solver);
        UtilMethodsTest.check(SATISFIABLE, "-1 : INTEGER", z3Context, z3Solver);
    }

    @Test
    public void testINT() {
        Preferences.setIntPreference(Preferences.IntPreference.MAX_INT, "10");
        Preferences.setIntPreference(Preferences.IntPreference.MIN_INT, "-1");
        UtilMethodsTest.check(SATISFIABLE, "0 : INT", z3Context, z3Solver);
        UtilMethodsTest.check(SATISFIABLE, "1 : INT", z3Context, z3Solver);
        UtilMethodsTest.check(SATISFIABLE, "-1 : INT", z3Context, z3Solver);
        UtilMethodsTest.check(SATISFIABLE, "10 : INT", z3Context, z3Solver);
        UtilMethodsTest.check(UNSATISFIABLE, "11 : INT", z3Context, z3Solver);
        UtilMethodsTest.check(UNSATISFIABLE, "-3 : INT", z3Context, z3Solver);
        UtilMethodsTest.check(SATISFIABLE, "6 : INT", z3Context, z3Solver);
        UtilMethodsTest.check(SATISFIABLE, "4 : INT", z3Context, z3Solver);
    }

    @Test
    public void testSetComprehension1() {
        String formula = "{x | x : {1} } = {1} ";
        UtilMethodsTest.check(SATISFIABLE, formula, z3Context, z3Solver);
    }

    @Test
    public void testSetComprehension2() {
        String formula = "{x,y | x : {1,2,3} & y = 2} = {1|->2, 2|->2, 3|->2} ";
        UtilMethodsTest.check(SATISFIABLE, formula, z3Context, z3Solver);
    }

    @Test
    public void testSetComprehension3() {
        String formula = "{x | x : {1} } = {x | x > 0 & x < 2} ";
        UtilMethodsTest.check(SATISFIABLE, formula, z3Context, z3Solver);
    }

    @Test
    public void testSubset() {
        String formula = "{1} <: {1,2}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();
        assertEquals(Status.SATISFIABLE, check);
    }

    @Test
    public void testSubset2() {
        String formula = "{1} <: {3,2}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();
        assertEquals(Status.UNSATISFIABLE, check);
    }

    @Test
    public void testStrictSubset() {
        String formula = "{1} <<: {1,2}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();
        assertEquals(Status.SATISFIABLE, check);
    }

    @Test
    public void testStrictSubset2() {
        String formula = "{1} <<: {1}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();
        assertEquals(Status.UNSATISFIABLE, check);
    }

    @Test
    public void testStrictSubset3() {
        String formula = "{1} <<: {1,1}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();
        assertEquals(Status.UNSATISFIABLE, check);
    }

    @Test
    public void testnoSubset() {
        String formula = "{1} /<: {1}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();
        assertEquals(Status.UNSATISFIABLE, check);
    }

    @Test
    public void testnoSubset2() {
        String formula = "{1} /<: {2,3}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();
        assertEquals(Status.SATISFIABLE, check);
    }

    @Test
    public void testnoSubset3() {
        String formula = "{1} /<: {1,2}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();
        assertEquals(Status.UNSATISFIABLE, check);
    }

    @Test
    public void testNoProperSubset() {
        String formula = "{1} /<<: {1}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();
        assertEquals(Status.SATISFIABLE, check);
    }

    @Test
    public void testNoProperSubset2() {
        String formula = "{1} /<<: {1,2}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();
        assertEquals(Status.UNSATISFIABLE, check);
    }

    @Test
    public void testNoProperSubset3() {
        String formula = "{1} /<<: {2,3}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();
        assertEquals(Status.SATISFIABLE, check);
    }

    @Test
    public void testNotBelonging() {
        String formula = "1 /: {2,3}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();
        assertEquals(Status.SATISFIABLE, check);
    }

    @Test
    public void testNotBelonging2() {
        String formula = "{1} /: {{1}}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();
        assertEquals(Status.UNSATISFIABLE, check);
    }

    @Test
    public void testBelonging() {
        String formula = "1 : {1}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();
        assertEquals(Status.SATISFIABLE, check);
    }

    @Test
    public void testBelonging2() {
        String formula = "x: {1}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();
        assertEquals(Status.SATISFIABLE, check);
    }

    @Test
    public void testBelonging3() {
        String formula = "1 : {2,3}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();
        assertEquals(Status.UNSATISFIABLE, check);
    }

    @Test
    public void testIntersection() {
        String formula = "{1,2} /\\ {2,3} = {2}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();
        assertEquals(Status.SATISFIABLE, check);
    }

    @Test
    public void testIntersection2() {
        String formula = "{1} /\\ {2,3} = {2}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();
        assertEquals(Status.UNSATISFIABLE, check);
    }

    @Test
    public void testUnion() {
        String formula = "{1,2} \\/ {2,3} = {2}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();
        assertEquals(Status.UNSATISFIABLE, check);
    }

    @Test
    public void testUnion2() {
        String formula = "{1,2} \\/ {2,3} = {1,2,3}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();
        assertEquals(Status.SATISFIABLE, check);
    }


    @Test
    public void testDifference() {
        String formula = "{1,2}\\{2,3} = {1}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();
        assertEquals(Status.SATISFIABLE, check);
    }

    @Test
    public void testDifference2() {
        String formula = "{1,2}\\{3} = {1}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();
        assertEquals(Status.UNSATISFIABLE, check);
    }

    @Test
    public void testAdditionFormula() {
        String formula = "x : 2 .. 3";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();

        assertEquals(Status.SATISFIABLE, check);
    }

    @Test
    public void testFailAdditionFormula() {
        String formula = "x : 2 .. 3 & x > 3";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();

        assertEquals(Status.UNSATISFIABLE, check);
    }


    @Test
    public void testCartesianProduct() {
        String formula = "{1}*{2}={(1,2)}";
        // getting the translated z3 representation of the formula
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();
        assertEquals(Status.SATISFIABLE, check);
    }

    @Test
    public void testCartesianProductUnsat() {
        String formula = "{1}*{2}={(1,3)}";
        BoolExpr constraint = FormulaToZ3Translator.translatePredicate(formula, z3Context);
        z3Solver.add(constraint);
        Status check = z3Solver.check();
        assertEquals(Status.UNSATISFIABLE, check);
    }

    @Ignore
    @Test
    public void testGeneralizedUnion() {
        // TODO z3 is currently not able to handle sets of sets and reports the
        // status UNKNOWN
        String formula = "union({{1},{2},{3}}) = {1,2,3} ";
        UtilMethodsTest.check(SATISFIABLE, formula, z3Context, z3Solver);
    }

    @Ignore
    @Test
    public void testCard() {
        String formula = "card({1,2,3,4}) = 3";
        UtilMethodsTest.check(SATISFIABLE, formula, z3Context, z3Solver);
    }

}
