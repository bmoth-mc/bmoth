package de.bmoth.parser;

import de.bmoth.exceptions.ParseErrorException;
import org.junit.Test;

public class ParseErrorTest {

    @Test(expected = ParseErrorException.class)
    public void testParseError() {
        String formula = " 1  {1} ";
        Parser.getFormulaAsSemanticAst(formula);
    }
}
