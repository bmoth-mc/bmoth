package de.bmoth.typechecker;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Test;
import static de.bmoth.typechecker.TestTypechecker.getFormulaTypes;

public class SequenceFormulaTest {

    @Test
    public void testSequenceEnumeration() throws Exception {
        String formula = "a = [1,b]";
        HashMap<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals("SEQUENCE(INTEGER)", formulaTypes.get("a"));
        assertEquals("INTEGER", formulaTypes.get("b"));
    }

    @Test
    public void testInsertTail() throws Exception {
        String formula = "a = [b] <- 1";
        HashMap<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals("SEQUENCE(INTEGER)", formulaTypes.get("a"));
        assertEquals("INTEGER", formulaTypes.get("b"));
    }

    @Test
    public void testInsertFront() throws Exception {
        String formula = "a = 1 -> [b]";
        HashMap<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals("SEQUENCE(INTEGER)", formulaTypes.get("a"));
        assertEquals("INTEGER", formulaTypes.get("b"));
    }

    @Test
    public void testFirst() throws Exception {
        String formula = "1 = first([a])";
        HashMap<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals("INTEGER", formulaTypes.get("a"));
    }

    @Test
    public void testLast() throws Exception {
        String formula = "a = last([1])";
        HashMap<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals("INTEGER", formulaTypes.get("a"));
    }

    @Test
    public void testFront() throws Exception {
        String formula = "a = front([1,b])";
        HashMap<String, String> formulaTypes = getFormulaTypes(formula);
        System.out.println(formulaTypes.get("a"));
        assertEquals("SEQUENCE(INTEGER)", formulaTypes.get("a"));
        assertEquals("INTEGER", formulaTypes.get("b"));
    }

    @Test
    public void testTail() throws Exception {
        String formula = "a = tail([1,b])";
        HashMap<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals("SEQUENCE(INTEGER)", formulaTypes.get("a"));
        assertEquals("INTEGER", formulaTypes.get("b"));
    }

    @Test
    public void testEmptySet() throws Exception {
        String formula = "[1] /=[]";
        getFormulaTypes(formula);
    }
    
}
