package de.bmoth.ltl;

import de.bmoth.TestParser;
import de.bmoth.backend.ltl.LTLTransformations;
import de.bmoth.backend.ltl.transformation.*;
import de.bmoth.parser.ast.nodes.ltl.LTLBPredicateNode;
import de.bmoth.parser.ast.nodes.ltl.LTLFormula;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.visitors.AbstractASTTransformation;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LTLTransformationTest extends TestParser {

    @Test
    public void testFinallyFinallyToFinally() {
        LTLNode node1 = parseLtlFormula("F( F( { 1 = 1 } ) )").getLTLNode();
        LTLNode node2 = parseLtlFormula("F( not( F( { 1 = 1 } ) ) )").getLTLNode();

        AbstractASTTransformation transformation = new ConvertFinallyFinallyToFinally();

        assertTrue(transformation.canHandleNode(node1));
        assertTrue(transformation.canHandleNode(node2));

        LTLNode newNode1 = (LTLNode) new ConvertFinallyFinallyToFinally().transformNode(node1);
        LTLNode newNode2 = (LTLNode) new ConvertFinallyFinallyToFinally().transformNode(node2);

        assertEquals("FINALLY(EQUAL(1,1))", newNode1.toString());
        assertEquals("NOT(FINALLY(EQUAL(1,1)))", newNode2.toString());
    }

    @Test
    public void testGloballyGloballyToGlobally() {
        LTLNode node1 = parseLtlFormula("G( G( { 1 = 1 } ) )").getLTLNode();
        LTLNode node2 = parseLtlFormula("G( not( G( { 1 = 1 } ) ) )").getLTLNode();

        AbstractASTTransformation transformation = new ConvertGloballyGloballyToGlobally();

        assertTrue(transformation.canHandleNode(node1));
        assertTrue(transformation.canHandleNode(node2));

        LTLNode newNode1 = (LTLNode) transformation.transformNode(node1);
        LTLNode newNode2 = (LTLNode) transformation.transformNode(node2);

        assertEquals("GLOBALLY(EQUAL(1,1))", newNode1.toString());
        assertEquals("NOT(GLOBALLY(EQUAL(1,1)))", newNode2.toString());
    }

    @Test
    public void testNotGloballyToFinallyNot() {
        LTLNode node = parseLtlFormula("not(G { 1=1 })").getLTLNode();
        AbstractASTTransformation transformation = new ConvertNotGloballyToFinallyNot();

        assertTrue(transformation.canHandleNode(node));

        LTLNode newNode = (LTLNode) transformation.transformNode(node);
        assertEquals("FINALLY(NOT(EQUAL(1,1)))", newNode.toString());
    }

    @Test
    public void testPhiUntilPhiUntilPsiToPhiUntilPsi() {
        LTLNode node1 = parseLtlFormula("{3=3} U ( {3=3} U {2=2} )").getLTLNode();
        LTLNode node2 = parseLtlFormula("( {1=1} U {2=2} ) U {2=2}").getLTLNode();
        LTLNode node3 = parseLtlFormula("( {1=1} U {2=2} ) U {3=3}").getLTLNode();
        LTLNode node4 = parseLtlFormula("{3=3} U ( {5=5} U {2=2} )").getLTLNode();

        AbstractASTTransformation transformation = new ConvertPhiUntilPhiUntilPsiToPhiUntilPsi();

        assertTrue(transformation.canHandleNode(node1));
        assertTrue(transformation.canHandleNode(node2));
        assertFalse(transformation.canHandleNode(node3));
        assertFalse(transformation.canHandleNode(node4));

        LTLNode newNode1 = (LTLNode) transformation.transformNode(node1);
        LTLNode newNode2 = (LTLNode) transformation.transformNode(node2);

        assertEquals("UNTIL(EQUAL(3,3),EQUAL(2,2))", newNode1.toString());
        assertEquals("UNTIL(EQUAL(1,1),EQUAL(2,2))", newNode2.toString());
    }

    @Test
    public void testTransformationNotFinallyToGloballyNot() {
        LTLNode node = parseLtlFormula("not (F {2=1})").getLTLNode();
        AbstractASTTransformation transformation = new ConvertNotFinallyToGloballyNot();

        assertTrue(transformation.canHandleNode(node));

        LTLNode newNode = (LTLNode) transformation.transformNode(node);
        assertEquals("GLOBALLY(NOT(EQUAL(2,1)))", newNode.toString());
    }

    @Test
    public void testTransformationNotNextToNextNot() {
        LTLFormula ltlFormula = parseLtlFormula("not (X {0=1})");
        LTLNode node = (LTLNode) new ConvertNotNextToNextNot().transformNode(ltlFormula.getLTLNode());
        assertEquals("NEXT(NOT(EQUAL(0,1)))", node.toString());
    }

    @Test
    public void testTransformationFGFtoGF() {
        LTLFormula ltlFormula = parseLtlFormula("F(G (F {0=1}))");
        LTLNode node = (LTLNode) new ConvertFinallyGloballyFinallyToGloballyFinally().transformNode(ltlFormula.getLTLNode());
        assertEquals("GLOBALLY(FINALLY(EQUAL(0,1)))", node.toString());
    }

    @Test
    public void testTransformationGFGtoFG() {
        LTLFormula ltlFormula = parseLtlFormula("G (F (G {0=1}))");
        LTLNode node = (LTLNode) new ConvertGloballyFinallyGloballyToFinallyGlobally().transformNode(ltlFormula.getLTLNode());
        assertEquals("FINALLY(GLOBALLY(EQUAL(0,1)))", node.toString());
    }

    @Test
    public void testTransformation2() {
        LTLFormula ltlFormula = parseLtlFormula("not(GG { 1=1 })");
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("UNTIL(AND(WEAK_UNTIL(EQUAL(1,1),FALSE),NOT(FALSE)),AND(NOT(WEAK_UNTIL(EQUAL(1,1),FALSE)),NOT(FALSE)))", node1.toString());
    }

    @Test
    public void testTransformation3() {
        LTLFormula ltlFormula = parseLtlFormula("G not(E { 1=1 })");
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("WEAK_UNTIL(NOT(EQUAL(1,1)),FALSE)", node1.toString());
    }

    @Test
    public void testTransformation4() {
        LTLFormula ltlFormula = parseLtlFormula("{2=1} U ({1=3} U {5=5})");
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("UNTIL(EQUAL(2,1),UNTIL(EQUAL(1,3),EQUAL(5,5)))", node1.toString());
    }

    @Test
    public void testTransformation5() {
        LTLFormula ltlFormula = parseLtlFormula("({1:dom([1,2,3])} U {2 /: ran([1])}) U {2<17}");
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("UNTIL(UNTIL(ELEMENT_OF(1,DOMAIN(SEQ_ENUMERATION(1,2,3))),NOT_BELONGING(2,RANGE(SEQ_ENUMERATION(1)))),LESS(2,17))", node1.toString());
    }

    @Test
    public void testTransformation6() {
        LTLFormula ltlFormula = parseLtlFormula("not({ 1=1 })");
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());

        assertEquals("NOT(EQUAL(1,1))", node1.toString());

        // check if we have the B not, not the LTL not
        assertTrue(node1 instanceof LTLBPredicateNode);
    }

    @Test
    public void testTransformation7() {
        LTLFormula ltlFormula = parseLtlFormula("X {1<3} U {3>1}");
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("UNTIL(NEXT(LESS(1,3)),GREATER(3,1))", node1.toString());
    }

    @Test
    public void testTransformation8() {
        LTLFormula ltlFormula = parseLtlFormula("X ({1<3} U {3>1})");
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("UNTIL(NEXT(LESS(1,3)),NEXT(GREATER(3,1)))", node1.toString());
    }

    @Test
    public void testTransformationOfNotUntil() {
        LTLFormula ltlFormula = parseLtlFormula("not( { 1=1 } U {2=1})");
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("WEAK_UNTIL(AND(EQUAL(1,1),NOT(EQUAL(2,1))),AND(NOT(EQUAL(1,1)),NOT(EQUAL(2,1))))", node1.toString());
    }

    @Test
    public void testTransformationOfNotWeakUntil() {
        LTLFormula ltlFormula = parseLtlFormula("not( { 1=1 } W {2=1})");
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("UNTIL(AND(EQUAL(1,1),NOT(EQUAL(2,1))),AND(NOT(EQUAL(1,1)),NOT(EQUAL(2,1))))", node1.toString());
    }

    @Test
    public void testTransformationOfGlobally() {
        LTLFormula ltlFormula = parseLtlFormula("G { 1=1 }");
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("WEAK_UNTIL(EQUAL(1,1),FALSE)", node1.toString());
    }

    @Test
    public void testTransformationOfFinally() {
        LTLFormula ltlFormula = parseLtlFormula("F { 1=1 }");
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("UNTIL(TRUE,EQUAL(1,1))", node1.toString());
    }
}
