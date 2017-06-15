package de.bmoth.parser;

import static org.junit.Assert.fail;

import org.junit.Test;

public class ParseErrorTest {

    @Test
    public void testParseError() {
        String formula = " 1  {1} ";
        try {
            Parser.getFormulaAsSemanticAst(formula);
            fail("Expected type error exception.");
        } catch (ParserException e) {
            if (e.getException() instanceof ParserException) {
                fail("Expected type error exception.");
            }
        }
    }
}
