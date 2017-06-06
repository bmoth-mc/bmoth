package de.bmoth.typechecker;

import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.TypeErrorException;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TypeErrorExceptionTest {

    @Test(expected = TypeErrorException.class)
    public void testTypeErrorException() {
        String formula = "a = TRUE & b = 1 & a = b";
        Parser.getFormulaAsSemanticAst(formula);
    }

    @Test(expected = TypeErrorException.class)
    public void testTypeErrorException2() {
        String formula = "a = TRUE & a = 1 + 1";
        Parser.getFormulaAsSemanticAst(formula);
    }

    @Test
    public void testSetEnumeration() {
        String formula = "x = {1,2,(3,4)}";
        String exceptionMessage = getExceptionMessage(formula);
        assertTrue(exceptionMessage != null &&exceptionMessage.contains("Expected INTEGER but found INTEGER*INTEGER"));
    }

    @Test
    public void testSetEnumeration2() {
        String formula = "x = {1,2,{3,4}}";
        String exceptionMessage = getExceptionMessage(formula);
        assertTrue(exceptionMessage != null &&exceptionMessage.contains("found POW"));
    }

    private static String getExceptionMessage(String formula) {
        try {
            Parser.getFormulaAsSemanticAst(formula);
            fail("Expected a type error exception.");
            return "";
        } catch (TypeErrorException e) {
            return e.getMessage();
        }
    }
}
