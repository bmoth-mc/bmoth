package de.bmoth.ltl;

import de.bmoth.backend.ltl.LTLTransformations;
import de.bmoth.parser.Parser;
import de.bmoth.parser.ParserException;
import de.bmoth.parser.ast.nodes.ltl.BuechiAutomaton;
import de.bmoth.parser.ast.nodes.ltl.LTLFormula;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import org.junit.Ignore;
import org.junit.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

public class LTLBuechiTest {

    private final Logger logger = Logger.getLogger(getClass().getName());

    @Test
    public void testGraphConstructionGloballyNotFalse() throws ParserException {
        String formula = "G not false";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        assertEquals(1, buechiAutomaton.getFinalNodeSet().size());
        logger.log(Level.INFO, () -> buechiAutomaton.toString());
    }

    @Test
    public void testGraphConstructionGloballyFalsity() throws ParserException {
        String formula = "G ({0=1})";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        assertEquals(1, buechiAutomaton.getFinalNodeSet().size());
        logger.log(Level.INFO, () -> buechiAutomaton.toString());
    }

    @Test
    public void testGraphConstructionGloballyTruth() throws ParserException {
        String formula = "G ({1=1})";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        assertEquals(1, buechiAutomaton.getFinalNodeSet().size());
        logger.log(Level.INFO, () -> buechiAutomaton.toString());
    }

    @Test
    public void testGraphConstructionGloballyNext() throws ParserException {
        String formula = "G (X {0=1})";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        assertEquals(2, buechiAutomaton.getFinalNodeSet().size());
        logger.log(Level.INFO, () -> buechiAutomaton.toString());
    }

    @Test
    public void testGraphConstructionUntil() throws ParserException {
        String formula = "{0=1} U {1=1}";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        assertEquals(3, buechiAutomaton.getFinalNodeSet().size());
        logger.log(Level.INFO, () -> buechiAutomaton.toString());
    }

    @Test
    public void testGraphConstructionUntil2() throws ParserException {
        String formula = "{1=1} U ({2=2} U {3=3})";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        // should be 4?
        // assertEquals(6, buechiAutomaton.getFinalNodeSet().size());
        logger.log(Level.INFO, () -> buechiAutomaton.toString());
    }

    @Test
    public void testGraphConstructionRelease() throws ParserException {
        String formula = "{0=1} R {1=1}";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        assertEquals(3, buechiAutomaton.getFinalNodeSet().size());
        logger.log(Level.INFO, () -> buechiAutomaton.toString());
    }

    @Test
    public void testGraphConstructionRelease2() throws ParserException {
        String formula = "{1=1} R ({2=2} R {3=3})";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        assertEquals(7, buechiAutomaton.getFinalNodeSet().size());
        logger.log(Level.INFO, () -> buechiAutomaton.toString());
    }

    @Test
    public void testGraphConstructionUntilNext() throws ParserException {
        String formula = "{0=1} U (X {1=1})";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        assertEquals(4, buechiAutomaton.getFinalNodeSet().size());
        logger.log(Level.INFO, () -> buechiAutomaton.toString());
    }

    @Test
    public void testGraphConstructionGloballyFinally() throws ParserException {
        String formula = "G (F (X {0=1}))";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("RELEASE(FALSE,UNTIL(TRUE,NEXT(EQUAL(0,1))))", node.toString());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        assertEquals(4, buechiAutomaton.getFinalNodeSet().size());
        logger.log(Level.INFO, () -> buechiAutomaton.toString());
    }

    @Test
    public void testGraphConstructionFinally() throws ParserException {
        String formula = "F ({0=1})";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        assertEquals(3, buechiAutomaton.getFinalNodeSet().size());
        logger.log(Level.INFO, () -> buechiAutomaton.toString());
    }

    @Test
    public void testGraphConstructionFinallyNext() throws ParserException {
        String formula = "F (X {0=1})";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        assertEquals(4, buechiAutomaton.getFinalNodeSet().size());
        logger.log(Level.INFO, () -> buechiAutomaton.toString());
    }

    @Test
    public void testGraphConstructionNot() throws ParserException {
        String formula = "G not (X {0=1})";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        assertEquals(2, buechiAutomaton.getFinalNodeSet().size());
        logger.log(Level.INFO, () -> buechiAutomaton.toString());
    }

    @Test
    public void testGraphConstructionAnd() throws ParserException {
        String formula = "G (X ( {0=1} & {2=3} ) )";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        assertEquals(2, buechiAutomaton.getFinalNodeSet().size());
        logger.log(Level.INFO, () -> buechiAutomaton.toString());
    }

    @Test
    public void testGraphConstructionAnd2() throws ParserException {
        String formula = "G ( {0=1} & {2=3} )";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        assertEquals(1, buechiAutomaton.getFinalNodeSet().size());
        logger.log(Level.INFO, () -> buechiAutomaton.toString());
    }

    @Test
    public void testGraphConstructionNotGloballyNextUntil() throws ParserException {
        String formula = "not G ( X ({3=4} U {2=3}) )";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        assertEquals(5, buechiAutomaton.getFinalNodeSet().size());
        logger.log(Level.INFO, () -> buechiAutomaton.toString());
    }

    @Test
    public void testGraphConstructionFp1UGp2() throws ParserException {
        String formula = "(F {3=4}) U (G {2=3})";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        assertEquals(8, buechiAutomaton.getFinalNodeSet().size());
        logger.log(Level.INFO, () -> buechiAutomaton.toString());
    }

    @Test
    public void testGraphConstructionGp1Up2() throws ParserException {
        String formula = "(G {3=4}) U ({2=3})";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        assertEquals(5, buechiAutomaton.getFinalNodeSet().size());
        logger.log(Level.INFO, () -> buechiAutomaton.toString());
    }

    @Test
    @Ignore
    public void testGraphConstructionGFp1ImpliesGFp2() throws ParserException {
        String formula = "(GF {3=4}) => (GF {2=3})";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        // should be 9?
        assertEquals(6, buechiAutomaton.getFinalNodeSet().size());
        logger.log(Level.INFO, () -> buechiAutomaton.toString());
    }
}
