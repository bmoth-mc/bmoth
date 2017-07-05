package de.bmoth.ltl;

import de.bmoth.backend.ltl.LTLTransformations;
import de.bmoth.parser.Parser;
import de.bmoth.parser.ParserException;
import de.bmoth.parser.ast.nodes.ltl.BuechiAutomaton;
import de.bmoth.parser.ast.nodes.ltl.LTLFormula;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LTLBuechiTest {

    @Test
    public void testGraphConstruction() throws ParserException {
        String formula = "(X {0=1})";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        System.out.println(buechiAutomaton.toString());
        assertEquals(3, buechiAutomaton.getFinalNodeSet().size());
    }

    @Test
    public void testGraphConstruction2() throws ParserException {
        String formula = "G (X {0=1})";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        System.out.println(buechiAutomaton.toString());
        assertEquals(2, buechiAutomaton.getFinalNodeSet().size());
    }

    @Test
    @Ignore 
    public void testGraphConstruction3() throws ParserException {
        String formula = "G (F (X {0=1}))";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        System.out.println(node.toString());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        System.out.println(buechiAutomaton.toString());
        assertEquals(4, buechiAutomaton.getFinalNodeSet().size());
    }

}
