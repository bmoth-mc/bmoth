package de.bmoth.ltl;

import de.bmoth.backend.ltl.LTLTransformations;
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

public class LTLTransformationTest {

    @Test
    public void testTransformation1() throws ParserException {
        String formula = "not(G { 1=1 })";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("FINALLY(NOT(EQUAL(1,1)))", node1.toString());
    }
<<<<<<< HEAD
    
    @Test
    public void testTransformationNotFinallyToGloballyNot() throws ParserException{
    	String formula = "not (F {2=1})";
    	LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("GLOBALLY(NOT(EQUAL(2,1)))", node1.toString());
    }
    
    @Test
    public void testTransformationNotNextToNextNot() throws ParserException{
    	String formula = "not (X {0=1})";
    	LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("NEXT(NOT(EQUAL(0,1)))", node1.toString());
    }
    
    @Test
    public void testTransformationFGFtoGF() throws ParserException{
    	String formula = "F(G (F {0=1}))";
    	LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("GLOBALLY(FINALLY(EQUAL(0,1)))", node1.toString());
    }
    
    @Test
    public void testTransformationGFGtoFG() throws ParserException{
    	String formula = "G (F (G {0=1}))";
    	LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("FINALLY(GLOBALLY(EQUAL(0,1)))", node1.toString());
=======

    @Test
    public void testTransformation2() throws ParserException {
        String formula = "not(GG { 1=1 })";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("FINALLY(NOT(EQUAL(1,1)))", node1.toString());
    }

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
>>>>>>> 0500f2415138555a9f7dcecad560111b3bd4f6cd
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
}
