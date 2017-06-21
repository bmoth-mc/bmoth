package de.bmoth.ltl;

import de.bmoth.backend.ltl.LTLTransformations;
import de.bmoth.parser.Parser;
import de.bmoth.parser.ParserException;
import de.bmoth.parser.ast.nodes.ltl.LTLFormula;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LTLTransformationTest {

    @Test
    public void testTransformationNotGloballyToFinallyNot() throws ParserException {
        String formula = "not(G { 1=1 })";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("FINALLY(NOT(EQUAL(1,1)))", node1.toString());
    }
}
