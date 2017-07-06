package de.bmoth.typechecker;

import org.junit.Test;

import java.util.Map;

import static de.bmoth.TestConstants.*;
import static de.bmoth.typechecker.TestTypechecker.getFormulaTypes;
import static org.junit.Assert.assertEquals;

public class RelationsTest {

    @Test
    public void testSetOfRelations() {
        String formula = "a <-> b = {{(1|->TRUE)}}";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals("POW(INTEGER)", formulaTypes.get("a"));
        assertEquals("POW(BOOL)", formulaTypes.get("b"));
    }
    
    @Test
    public void testFunctionCall() {
        String formula = "[4,5,6](2) = a";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals(INTEGER, formulaTypes.get("a"));
    }

    @Test
    public void testDirectProduct() {
        String formula = "a = {1 |-> TRUE} >< {b |-> 1}";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals("POW(INTEGER*(BOOL*INTEGER))", formulaTypes.get("a"));
        assertEquals(INTEGER, formulaTypes.get("b"));
    }

    @Test
    public void testOverwriteRelation() {
        String formula = "k = {x|->2} <+ {1|->y}";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals(POW_INTEGER_INTEGER, formulaTypes.get("k"));
        assertEquals(INTEGER, formulaTypes.get("x"));
        assertEquals(INTEGER, formulaTypes.get("y"));
    }

    @Test
    public void testDomainRestriction() {
        String formula = "k = x <| {1|->1}";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals(POW_INTEGER_INTEGER, formulaTypes.get("k"));
        assertEquals(POW_INTEGER, formulaTypes.get("x"));
    }

    @Test
    public void testDomainSubstraction() {
        String formula = "k = x <<| {1|->1}";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals(POW_INTEGER_INTEGER, formulaTypes.get("k"));
        assertEquals(POW_INTEGER, formulaTypes.get("x"));
    }

    @Test
    public void testRangeRestriction() {
        String formula = "k = {1|->1} |> x";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals(POW_INTEGER_INTEGER, formulaTypes.get("k"));
        assertEquals(POW_INTEGER, formulaTypes.get("x"));
    }

    @Test
    public void testRangeSubstraction() {
        String formula = "k = {1|->1} |>> x";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals(POW_INTEGER_INTEGER, formulaTypes.get("k"));
        assertEquals(POW_INTEGER, formulaTypes.get("x"));
    }

    @Test
    public void testInverseRelation() {
        String formula = "k = {1|->TRUE}~";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals("POW(BOOL*INTEGER)", formulaTypes.get("k"));
    }

    @Test
    public void testMultOrCart() {
        String formula = "a * b = c & c = d * e & e = 1";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals(INTEGER, formulaTypes.get("a"));
    }

    @Test
    public void testMultOrCartAndMinus() {
        String formula = "a * b = c & c = d - e & e = 1";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals(INTEGER, formulaTypes.get("a"));
    }

    @Test
    public void testMultOrCartAndSet() {
        String formula = "a * b = c & {} /= c  & c = {1|->TRUE}";
        Map<String, String> formulaTypes = getFormulaTypes(formula);
        assertEquals(POW_INTEGER, formulaTypes.get("a"));
    }

}
