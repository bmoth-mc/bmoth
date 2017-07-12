package de.bmoth.ltl;

import de.bmoth.backend.ltl.LTLTransformations;
import de.bmoth.parser.Parser;
import de.bmoth.parser.ParserException;
import de.bmoth.parser.ast.nodes.ltl.BuechiAutomaton;
import de.bmoth.parser.ast.nodes.ltl.LTLFormula;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LTLBuechiTest {

    @Test
    public void testGraphConstructionGlobally() throws ParserException {
        String formula = "G ({0=1})";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        assertEquals(1, buechiAutomaton.getFinalNodeSet().size());
        buechiAutomaton.labelNodeSet();
        System.out.println(buechiAutomaton.toString());
    }

    @Test
    public void testGraphConstructionGloballyNext() throws ParserException {
        String formula = "G (X {0=1})";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        assertEquals(2, buechiAutomaton.getFinalNodeSet().size());
        buechiAutomaton.labelNodeSet();
        System.out.println(buechiAutomaton.toString());
    }

    @Test
    public void testGraphConstructionUntil() throws ParserException {
        String formula = "{0=1} U {1=1}";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        assertEquals(3, buechiAutomaton.getFinalNodeSet().size());
        buechiAutomaton.labelNodeSet();
        System.out.println(buechiAutomaton.toString());
    }

    @Test
    public void testGraphConstructionUntilNext() throws ParserException {
        String formula = "{0=1} U (X {1=1})";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        assertEquals(4, buechiAutomaton.getFinalNodeSet().size());
        buechiAutomaton.labelNodeSet();
    }

    @Test
    public void testGraphConstructionGloballyFinally() throws ParserException {
        String formula = "G (F (X {0=1}))";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        assertEquals(4, buechiAutomaton.getFinalNodeSet().size());
        buechiAutomaton.labelNodeSet();
        System.out.println(buechiAutomaton.toString());
    }

    @Test
    public void testGraphConstructionFinally() throws ParserException {
        String formula = "F (X {0=1})";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        assertEquals(4, buechiAutomaton.getFinalNodeSet().size());
        buechiAutomaton.labelNodeSet();
    }

    @Test
    public void testGraphConstructionNot() throws ParserException {
        String formula = "G not (X {0=1})";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        assertEquals(2, buechiAutomaton.getFinalNodeSet().size());
        buechiAutomaton.labelNodeSet();
    }

    @Test
    public void testGraphConstructionAnd() throws ParserException {
        String formula = "G (X ( {0=1} & {2=3} ) )";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        assertEquals(2, buechiAutomaton.getFinalNodeSet().size());
        buechiAutomaton.labelNodeSet();
    }

    @Test
    public void testGraphConstructionAnd2() throws ParserException {
        String formula = "G ( {0=1} & {2=3} )";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        assertEquals(2, buechiAutomaton.getFinalNodeSet().size());
        buechiAutomaton.labelNodeSet();
    }

    @Test
    public void testGraphConstructionNotGloballyNextUntil() throws ParserException {
        String formula = "not G ( X ({3=4} U {2=3}) )";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        BuechiAutomaton buechiAutomaton = new BuechiAutomaton(node);
        assertEquals(6, buechiAutomaton.getFinalNodeSet().size());
        buechiAutomaton.labelNodeSet();
        System.out.println(buechiAutomaton.toString());
    }
}
