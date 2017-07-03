package de.bmoth.ltl;

import de.bmoth.TestParser;
import de.bmoth.backend.ltl.transformation.*;
import de.bmoth.parser.ast.nodes.ltl.LTLFormula;
import de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.visitors.ASTTransformation;
import org.junit.Test;

import static de.bmoth.backend.ltl.LTLTransformationUtil.*;
import static de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode.Kind.OR;
import static de.bmoth.parser.ast.nodes.ltl.LTLKeywordNode.Kind.FALSE;
import static de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode.Kind.FINALLY;
import static de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode.Kind.GLOBALLY;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.*;

public class LTLTransformationTest extends TestParser {

    @Test
    public void testFinallyFinallyToFinally() {
        LTLNode node1 = parseLtlFormula("F( F( { 1 = 1 } ) )").getLTLNode();
        LTLNode node2 = parseLtlFormula("F( not( F( { 1 = 1 } ) ) )").getLTLNode();
        LTLNode nodeFailNotFinally = parseLtlFormula("F( not( { 1 = 1 } ) )").getLTLNode();

        ASTTransformation transformation = new ConvertFinallyFinallyToFinally();

        assertTrue(transformation.canHandleNode(node1));
        assertTrue(transformation.canHandleNode(node2));
        assertFalse(transformation.canHandleNode(nodeFailNotFinally));

        LTLNode newNode1 = (LTLNode) transformation.transformNode(node1);
        LTLNode newNode2 = (LTLNode) transformation.transformNode(node2);

        assertEquals("FINALLY(EQUAL(1,1))", newNode1.toString());
        assertEquals("NOT(FINALLY(EQUAL(1,1)))", newNode2.toString());
    }

    @Test
    public void testGloballyGloballyToGlobally() {
        LTLNode node1 = parseLtlFormula("G( G( { 1 = 1 } ) )").getLTLNode();
        LTLNode node2 = parseLtlFormula("G( not( G( { 1 = 1 } ) ) )").getLTLNode();

        ASTTransformation transformation = new ConvertGloballyGloballyToGlobally();

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
        ASTTransformation transformation = new ConvertNotGloballyToFinallyNot();

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

        ASTTransformation transformation = new ConvertPhiUntilPhiUntilPsiToPhiUntilPsi();

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
    public void testFinallyPhiOrPsiToFinallyPhiOrFinallyPsi() {
        LTLNode node1 = parseLtlFormula("F( ( {3=3} or {2=2} ) )").getLTLNode();

        ASTTransformation transformation = new ConvertFinallyPhiOrPsiToFinallyPhiOrFinallyPsi();

        assertTrue(transformation.canHandleNode(node1));

        LTLNode newNode1 = (LTLNode) transformation.transformNode(node1);

        assertEquals("OR(FINALLY(EQUAL(3,3)),FINALLY(EQUAL(2,2)))", newNode1.toString());
    }

    @Test
    public void testGloballyPhiAndPsiToGloballyPhiAndGloballyPsi() {
        LTLNode node1 = parseLtlFormula("G( ( {3=3} & {2=2} ) )").getLTLNode();

        ASTTransformation transformation = new ConvertGloballyPhiAndPsiToGloballyPhiAndGloballyPsi();

        assertTrue(transformation.canHandleNode(node1));

        LTLNode newNode1 = (LTLNode) transformation.transformNode(node1);

        assertEquals("AND(GLOBALLY(EQUAL(3,3)),GLOBALLY(EQUAL(2,2)))", newNode1.toString());
    }

    @Test
    public void testNotRelease() {
        LTLNode node1 = parseLtlFormula("not( {23=23} R {24=24} )").getLTLNode();
        LTLNode node2 = parseLtlFormula("not( {23=23} U {24=24} )").getLTLNode();

        ASTTransformation transformation = new ConvertNotRelease();

        assertTrue(transformation.canHandleNode(node1));
        assertFalse(transformation.canHandleNode(node2));

        LTLNode newNode1 = (LTLNode) transformation.transformNode(node1);

        assertEquals("UNTIL(NOT(EQUAL(23,23)),NOT(EQUAL(24,24)))", newNode1.toString());
    }

    @Test
    public void testWeakToRelease() {
        LTLNode node1 = parseLtlFormula("{12=12} W {13=13}").getLTLNode();
        LTLNode node2 = parseLtlFormula("{12=12} R {13=13}").getLTLNode();

        ASTTransformation transformation = new ConvertWeakToRelease();

        assertTrue(transformation.canHandleNode(node1));
        assertFalse(transformation.canHandleNode(node2));

        LTLNode newNode1 = (LTLNode) transformation.transformNode(node1);

        assertEquals("RELEASE(EQUAL(13,13),OR(EQUAL(12,12),EQUAL(13,13)))", newNode1.toString());


    }

    @Test
    public void testTransformationNotFinallyToGloballyNot() {
        LTLNode node = parseLtlFormula("not (F {2=1})").getLTLNode();
        ASTTransformation transformation = new ConvertNotFinallyToGloballyNot();

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
    public void testRemoveTrueOr() {
        LTLNode node1 = parseLtlFormula("{0=1} or true").getLTLNode();
        LTLNode node2 = parseLtlFormula("true or {0=1}").getLTLNode();
        LTLNode nodeFailContainsRight = parseLtlFormula("{0=1} or false").getLTLNode();

        ASTTransformation transformation = new RemoveTrueOr();

        assertTrue(transformation.canHandleNode(node1));
        assertTrue(transformation.canHandleNode(node2));
        assertFalse(transformation.canHandleNode(nodeFailContainsRight));

        LTLNode newNode1 = (LTLNode) transformation.transformNode(node1);
        LTLNode newNode2 = (LTLNode) transformation.transformNode(node2);

        assertEquals("TRUE", newNode1.toString());
        assertEquals("TRUE", newNode2.toString());
    }

    @Test
    public void testRemoveFalseAnd() {
        LTLNode node1 = parseLtlFormula("{0=1} & false").getLTLNode();
        LTLNode node2 = parseLtlFormula("false & {0=1}").getLTLNode();
        LTLNode nodeFailContainsRight = parseLtlFormula("{0=1} & true").getLTLNode();

        ASTTransformation transformation = new RemoveFalseAnd();

        assertTrue(transformation.canHandleNode(node1));
        assertTrue(transformation.canHandleNode(node2));
        assertFalse(transformation.canHandleNode(nodeFailContainsRight));

        LTLNode newNode1 = (LTLNode) transformation.transformNode(node1);
        LTLNode newNode2 = (LTLNode) transformation.transformNode(node2);

        assertEquals("FALSE", newNode1.toString());
        assertEquals("FALSE", newNode2.toString());
    }

    @Test
    public void testRemoveTrueFromAnd() {
        LTLNode node1 = parseLtlFormula("{0=1} & true").getLTLNode();
        LTLNode node2 = parseLtlFormula("true & {0=1}").getLTLNode();

        ASTTransformation transformation = new RemoveTrueFromAnd();

        assertTrue(transformation.canHandleNode(node1));
        assertTrue(transformation.canHandleNode(node2));

        LTLNode newNode1 = (LTLNode) transformation.transformNode(node1);
        LTLNode newNode2 = (LTLNode) transformation.transformNode(node2);

        assertEquals("EQUAL(0,1)", newNode1.toString());
        assertEquals("EQUAL(0,1)", newNode2.toString());

    }

    @Test
    public void testRemoveFalseFromOr() {
        LTLNode node1 = parseLtlFormula("{234=234} or false").getLTLNode();
        LTLNode node2 = parseLtlFormula("false or {234=234}").getLTLNode();

        ASTTransformation transformation = new RemoveFalseFromOr();

        assertTrue(transformation.canHandleNode(node1));
        assertTrue(transformation.canHandleNode(node2));

        LTLNode newNode1 = (LTLNode) transformation.transformNode(node1);
        LTLNode newNode2 = (LTLNode) transformation.transformNode(node2);

        assertEquals("EQUAL(234,234)", newNode1.toString());
        assertEquals("EQUAL(234,234)", newNode2.toString());

    }

    @Test
    public void testConvertNotWeakUntil() {
        LTLFormula ltlFormula = parseLtlFormula("not({0=1} W false)");
        LTLNode node = (LTLNode) new ConvertNotWeakUntil().transformNode(ltlFormula.getLTLNode());
        assertEquals("UNTIL(AND(EQUAL(0,1),NOT(FALSE)),AND(NOT(EQUAL(0,1)),NOT(FALSE)))", node.toString());
    }

    @Test
    public void testLTLTransformationUtil() {
        LTLNode infixOp = new LTLInfixOperatorNode(OR, null, null);
        LTLNode prefixOp = new LTLPrefixOperatorNode(FINALLY, null);
        LTLNode prefixPrefixOp = new LTLPrefixOperatorNode(FINALLY, new LTLPrefixOperatorNode(FINALLY, null));

        // infix can not contain prefix op
        assertFalse(contains(infixOp, FINALLY));
        assertFalse(contains(infixOp, FINALLY, FINALLY));

        // infix can not contain keyword
        assertFalse(contains(infixOp, FALSE));

        // infix can not contain a single infix op (use containsLeft|Right instead!)
        assertFalse(contains(infixOp, OR));

        // first op: right type but mismatch kind...
        assertFalse(contains(prefixPrefixOp, FINALLY, GLOBALLY));

        // prefix can not contain left|right anything...
        assertFalse(containsLeft(prefixOp, OR));
        assertFalse(containsLeft(prefixOp, FALSE));
        assertFalse(containsRight(prefixOp, OR));
        assertFalse(containsRight(prefixOp, FALSE));

        // ... same with left|right child...
        assertNull(leftChild(prefixOp));
        assertNull(rightChild(prefixOp));

    }
}
