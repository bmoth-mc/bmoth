package de.bmoth.parser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ParseErrorTest {

    private Level savedParserLevel;
    private Logger parserLogger;

    @Before
    public void init() {
        // prepare logger to be quiet during test (prevent irritating red stuff...)
        parserLogger = Logger.getLogger(Parser.class.getName());
        savedParserLevel = parserLogger.getLevel();
        parserLogger.setLevel(Level.OFF);
    }

    @After
    public void tearDown() {
        // reset logger
        parserLogger.setLevel(savedParserLevel);
    }

    @Test
    public void testParserErrorException() {
        String formula = " 1  {1} ";
        try {
            Parser.getLTLFormulaAsCST(formula);
            fail("Expected parser error exception.");
        } catch (ParseErrorException e) {
            assertEquals("Parse error: Unexpected input '1' in line 1 column 1.\n" +
                "Additional information: extraneous input '1' expecting {'true', 'false', 'not', LTL_LEFT_PAR, 'G', 'F', 'X', LTL_B_START}", e.toString());
        }
    }

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
