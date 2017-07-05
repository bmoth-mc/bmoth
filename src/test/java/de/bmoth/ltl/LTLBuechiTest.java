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
    public void testGraphConstructionNext() throws ParserException {
        String formula = "(X {0=1})";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        assertEquals(3, buechiAutomaton.getFinalNodeSet().size());
    }

    @Test
    public void testGraphConstructionGlobally() throws ParserException {
        String formula = "G (X {0=1})";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        assertEquals(2, buechiAutomaton.getFinalNodeSet().size());
    }

    @Test
    @Ignore
    public void testGraphConstructionGloballyFinally() throws ParserException {
        String formula = "G (F (X {0=1}))";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        assertEquals(4, buechiAutomaton.getFinalNodeSet().size());
    }

    @Test
    public void testGraphConstructionNot() throws ParserException {
        String formula = "G not (X {0=1})";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        assertEquals(2, buechiAutomaton.getFinalNodeSet().size());
    }

    @Test
    public void testGraphConstructionAnd() throws ParserException {
        String formula = "G (X ( {0=1} & {2=3} ) )";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        assertEquals(2, buechiAutomaton.getFinalNodeSet().size());
    }

}
