package de.bmoth.typechecker;

import org.junit.Test;

import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.TypeErrorException;

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
}
