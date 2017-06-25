package de.bmoth.ltl;

import de.bmoth.TestParser;
import de.bmoth.backend.ltl.LTLTransformations;
import de.bmoth.backend.ltl.transformation.ConvertFinallyFinallyToFinally;
import de.bmoth.backend.ltl.transformation.ConvertNotFinallyToGloballyNot;
import de.bmoth.backend.ltl.transformation.ConvertNotGloballyToFinallyNot;
import de.bmoth.parser.Parser;
import de.bmoth.parser.ParserException;
import de.bmoth.parser.ast.nodes.ltl.LTLBPredicateNode;
import de.bmoth.parser.ast.nodes.ltl.LTLFormula;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LTLTransformationTest extends TestParser {

    @Test
    public void testFinallyFinallyToFinally() {
        LTLFormula ltlFormula = parseLtlFormula("F( F( { 1 = 1 } ) )");
        LTLNode node = (LTLNode) new ConvertFinallyFinallyToFinally().transformNode(ltlFormula.getLTLNode());
        assertEquals("FINALLY(EQUAL(1,1))", node.toString());
    }

    @Test
    public void testNotGloballyToFinallyNot() {
        LTLFormula ltlFormula = parseLtlFormula("not(G { 1=1 })");
        LTLNode node = (LTLNode) new ConvertNotGloballyToFinallyNot().transformNode(ltlFormula.getLTLNode());
        assertEquals("FINALLY(NOT(EQUAL(1,1)))", node.toString());
    }

    @Test
    public void testTransformationNotFinallyToGloballyNot() {
        LTLFormula ltlFormula = parseLtlFormula("not (F {2=1})");
        LTLNode node = (LTLNode) new ConvertNotFinallyToGloballyNot().transformNode(ltlFormula.getLTLNode());
        assertEquals("GLOBALLY(NOT(EQUAL(2,1)))", node.toString());
    }

    @Ignore
    @Test
    public void testTransformationNotNextToNextNot() throws ParserException{
    	String formula = "not (X {0=1})";
    	LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("NEXT(NOT(EQUAL(0,1)))", node1.toString());
    }
    @Ignore
    @Test
    public void testTransformationFGFtoGF() throws ParserException{
    	String formula = "F(G (F {0=1}))";
    	LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("GLOBALLY(FINALLY(EQUAL(0,1)))", node1.toString());
    }
    @Ignore
    @Test
    public void testTransformationGFGtoFG() throws ParserException{
    	String formula = "G (F (G {0=1}))";
    	LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("FINALLY(GLOBALLY(EQUAL(0,1)))", node1.toString());
    }
    @Ignore
    @Test
    public void testTransformation2() throws ParserException {
        String formula = "not(GG { 1=1 })";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("FINALLY(NOT(EQUAL(1,1)))", node1.toString());
    }
    @Ignore
    @Test
    public void testTransformation3() throws ParserException {
        String formula = "G not(E { 1=1 })";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("GLOBALLY(NOT(EQUAL(1,1)))", node1.toString());
    }

    @Test
    public void testTransformation4() throws ParserException {
        String formula = "{1=1} U ({1=1} U {2=2})";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("UNTIL(EQUAL(1,1),EQUAL(2,2))", node1.toString());
    }

    @Test
    public void testTransformation5() throws ParserException {
        String formula = "({1=1} U {2=2}) U {2=2}";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("UNTIL(EQUAL(1,1),EQUAL(2,2))", node1.toString());
    }

    @Test
    @Ignore
    public void testTransformation6() throws ParserException {
        String formula = "not(G { 1=1 })";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());

        assertEquals("FINALLY(NOT(EQUAL(1,1)))", node1.toString());

        // check if we have the B not, not the LTL not
        assertTrue(node1 instanceof LTLPrefixOperatorNode);
        LTLPrefixOperatorNode node1PO = (LTLPrefixOperatorNode) node1;
        assertEquals(LTLPrefixOperatorNode.Kind.FINALLY, node1PO.getKind());

        assertTrue(node1PO.getArgument() instanceof LTLBPredicateNode);
    }
    @Ignore
    @Test
    public void testTransformationOfNotUntil() throws ParserException {
        String formula = "not( { 1=1 } U {2=1})";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("", node1.toString());
    }

    @Ignore
    @Test
    public void testTransformationOfNotWeakUntil() throws ParserException {
        String formula = "not( { 1=1 } W {2=1})";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("", node1.toString());
    }
    @Ignore
    @Test
    public void testTransformationOfGlobally() throws ParserException {
        String formula = "G { 1=1 }";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("WEAK_UNTIL(EQUAL(1,1), FALSE)", node1.toString());
    }
    @Ignore
    @Test
    public void testTransformationOfFinally() throws ParserException {
        String formula = "F { 1=1 }";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("UNTIL(TRUE, EQUAL(1,1))", node1.toString());
    }
}
