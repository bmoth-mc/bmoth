package de.bmoth.typechecker;

import org.junit.Test;

import java.util.HashMap;

import static de.bmoth.typechecker.TestTypechecker.getFormulaTypes;
import static org.junit.Assert.assertEquals;

public class SequenceFormulaTest {

    @Test
    public void testSequenceEnumeration() {
        String formula = "a = [1,b]";
        HashMap<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals("SEQUENCE(INTEGER)", formulaTypes.get("a"));
        assertEquals("INTEGER", formulaTypes.get("b"));
    }

    @Test
    public void testInsertTail() {
        String formula = "a = [b] <- 1";
        HashMap<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals("SEQUENCE(INTEGER)", formulaTypes.get("a"));
        assertEquals("INTEGER", formulaTypes.get("b"));
    }

    @Test
    public void testInsertFront() {
        String formula = "a = 1 -> [b]";
        HashMap<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals("SEQUENCE(INTEGER)", formulaTypes.get("a"));
        assertEquals("INTEGER", formulaTypes.get("b"));
    }

    @Test
    public void testFirst() {
        String formula = "1 = first([a])";
        HashMap<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals("INTEGER", formulaTypes.get("a"));
    }

    @Test
    public void testLast() {
        String formula = "a = last([1])";
        HashMap<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals("INTEGER", formulaTypes.get("a"));
    }

    @Test
    public void testFront() {
        String formula = "a = front([1,b])";
        HashMap<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals("SEQUENCE(INTEGER)", formulaTypes.get("a"));
        assertEquals("INTEGER", formulaTypes.get("b"));
    }

    @Test
    public void testTail() {
        String formula = "a = tail([1,b])";
        HashMap<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals("SEQUENCE(INTEGER)", formulaTypes.get("a"));
        assertEquals("INTEGER", formulaTypes.get("b"));
    }

    @Test
    public void testEmptySet() {
        String formula = "[1] /=[]";
        getFormulaTypes(formula);
    }

    @Test
    public void testConc() {
        String formula = "[1,2] = [a] ^ b";
        HashMap<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals("INTEGER", formulaTypes.get("a"));
        assertEquals("SEQUENCE(INTEGER)", formulaTypes.get("b"));
    }

    @Test
    public void testSeq() {
        String formula = "[1] : seq(a)";
        HashMap<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals("POW(INTEGER)", formulaTypes.get("a"));
    }

    @Test
    public void testSeq1() {
        String formula = "[a] : seq(INTEGER)";
        HashMap<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals("INTEGER", formulaTypes.get("a"));
    }

    @Test
    public void testiSeq() {
        String formula = "a : seq(INTEGER)";
        HashMap<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals("SEQUENCE(INTEGER)", formulaTypes.get("a"));
    }

    @Test
    public void testiSeq1() {
        String formula = "[1] : seq({a})";
        HashMap<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals("INTEGER", formulaTypes.get("a"));
    }

    @Test
    public void testFunctionCall() {
        String formula = "[4,5,6](2) = a";
        HashMap<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals("INTEGER", formulaTypes.get("a"));
    }

}
