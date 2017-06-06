package de.bmoth.typechecker;

import org.junit.Test;

import java.util.Map;

import static de.bmoth.typechecker.TestTypechecker.getFormulaTypes;
import static org.junit.Assert.assertEquals;

public class SequenceFormulaTest {

    private static final String INTEGER = "INTEGER";
    private static final String SEQ_INTEGER = "SEQUENCE(INTEGER)";

    @Test
    public void testSequenceEnumeration() {
        String formula = "a = [1,b]";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals("POW(INTEGER*INTEGER)", formulaTypes.get("a"));
        assertEquals("INTEGER", formulaTypes.get("b"));
    }

    @Test
    public void testInsertTail() {
        String formula = "a = [b] <- 1";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals("POW(INTEGER*INTEGER)", formulaTypes.get("a"));
        assertEquals("INTEGER", formulaTypes.get("b"));
    }

    @Test
    public void testInsertFront() {
        String formula = "a = 1 -> [b]";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals("POW(INTEGER*INTEGER)", formulaTypes.get("a"));
        assertEquals("INTEGER", formulaTypes.get("b"));
    }

    @Test
    public void testFirst() {
        String formula = "1 = first([a])";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals(INTEGER, formulaTypes.get("a"));
    }

    @Test
    public void testLast() {
        String formula = "a = last([1])";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals(INTEGER, formulaTypes.get("a"));
    }

    @Test
    public void testFront() {
        String formula = "a = front([1,b])";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals("POW(INTEGER*INTEGER)", formulaTypes.get("a"));
        assertEquals("INTEGER", formulaTypes.get("b"));
    }

    @Test
    public void testTail() {
        String formula = "a = tail([1,b])";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals("POW(INTEGER*INTEGER)", formulaTypes.get("a"));
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
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals("INTEGER", formulaTypes.get("a"));
        assertEquals("POW(INTEGER*INTEGER)", formulaTypes.get("b"));
    }

    @Test
    public void testSeq() {
        String formula = "[1] : seq(a)";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals("POW(INTEGER)", formulaTypes.get("a"));
    }

    @Test
    public void testSeq1() {
        String formula = "[a] : seq(INTEGER)";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals(INTEGER, formulaTypes.get("a"));
    }

    @Test
    public void testiSeq() {
        String formula = "a : seq(INTEGER)";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals("POW(INTEGER*INTEGER)", formulaTypes.get("a"));
    }

    @Test
    public void testiSeq1() {
        String formula = "[1] : seq({a})";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals(INTEGER, formulaTypes.get("a"));
    }

    @Test
    public void testFunctionCall() {
        String formula = "[4,5,6](2) = a";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals("INTEGER", formulaTypes.get("a"));
    }

}
