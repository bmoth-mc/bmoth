package de.bmoth.parser;

import org.junit.Test;

import de.bmoth.exceptions.ParseErrorException;

public class ParseErrorTest {

	@Test(expected = ParseErrorException.class)
	public void testParseError() throws Exception {
		String formula = " 1  {1} ";
		Parser.getFormulaAsSemanticAst(formula);
	}
}
