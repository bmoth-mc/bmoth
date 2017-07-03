package de.bmoth.parser;

import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.fail;

import org.junit.Test;

import java.util.logging.Level;
import java.util.logging.Logger;
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
