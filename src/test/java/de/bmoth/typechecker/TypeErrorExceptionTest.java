package de.bmoth.typechecker;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static de.bmoth.typechecker.TestTypechecker.*;

public class TypeErrorExceptionTest {

    @Test
    public void testTypeErrorException() {
        String formula = "a = TRUE & b = 1 & a = b";
        typeCheckFormulaAndGetErrorMessage(formula);
    }

    @Test
    public void testTypeErrorException2() {
        String formula = "a = TRUE & a = 1 + 1";
        typeCheckFormulaAndGetErrorMessage(formula);
    }

    @Test
    public void testSetEnumeration() {
        String formula = "x = {1,2,(3,4)}";
        String exceptionMessage = typeCheckFormulaAndGetErrorMessage(formula);
        assertTrue(exceptionMessage.contains("Expected INTEGER but found INTEGER*INTEGER"));
    }

    @Test
    public void testSetEnumeration2() {
        String formula = "x = {1,2,{3,4}}";
        String exceptionMessage = typeCheckFormulaAndGetErrorMessage(formula);
        assertTrue(exceptionMessage.contains("found POW"));
    }
}
