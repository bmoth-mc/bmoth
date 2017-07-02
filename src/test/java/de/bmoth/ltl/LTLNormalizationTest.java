package de.bmoth.ltl;

import de.bmoth.TestParser;
import de.bmoth.backend.ltl.LTLTransformations;
import de.bmoth.parser.ast.nodes.ltl.*;
import org.junit.Ignore;
import org.junit.Test;

import static de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode.Kind.NEXT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LTLNormalizationTest extends TestParser {
    @Test
    public void testNormalization1() {
        LTLFormula ltlFormula = parseLtlFormula("not(GG { 1=1 })");
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("UNTIL(TRUE,NOT(EQUAL(1,1)))", node.toString());
        assertTrue(isNormalized(node));
    }

    @Test
    public void testNormalization2() {
        LTLFormula ltlFormula = parseLtlFormula("G not(F { 1=1 })");
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("RELEASE(FALSE,RELEASE(FALSE,NOT(EQUAL(1,1))))", node.toString());
        assertTrue(isNormalized(node));
    }

    @Test
    public void testNormalization3() {
        LTLFormula ltlFormula = parseLtlFormula("{2=1} U ({1=3} U {5=5})");
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("UNTIL(EQUAL(2,1),UNTIL(EQUAL(1,3),EQUAL(5,5)))", node.toString());
        assertTrue(isNormalized(node));
    }

    @Test
    public void testNormalization4() {
        LTLFormula ltlFormula = parseLtlFormula("({1:dom([1,2,3])} U {2 /: ran([1])}) U {2<17}");
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("UNTIL(UNTIL(ELEMENT_OF(1,DOMAIN(SEQ_ENUMERATION(1,2,3))),NOT_BELONGING(2,RANGE(SEQ_ENUMERATION(1)))),LESS(2,17))", node.toString());
        assertTrue(isNormalized(node));
    }

    @Test
    public void testNormalization5() {
        LTLFormula ltlFormula = parseLtlFormula("not({ 1=1 })");
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());

        assertEquals("NOT(EQUAL(1,1))", node.toString());

        // check if we have the B not, not the LTL not
        assertTrue(node instanceof LTLBPredicateNode);
        assertTrue(isNormalized(node));
    }

    @Test
    public void testNormalization6() {
        LTLFormula ltlFormula = parseLtlFormula("X {1<3} U {3>1}");
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("UNTIL(NEXT(LESS(1,3)),GREATER(3,1))", node.toString());
        assertTrue(isNormalized(node));
    }

    @Test
    public void testNormalization7() {
        LTLFormula ltlFormula = parseLtlFormula("X ({1<3} U {3>1})");
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("UNTIL(NEXT(LESS(1,3)),NEXT(GREATER(3,1)))", node.toString());
        assertTrue(isNormalized(node));
    }

    @Test
    public void testNormalization8() {
        LTLFormula ltlFormula = parseLtlFormula("not( { 1=1 } U {2=1})");
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("RELEASE(AND(NOT(EQUAL(1,1)),NOT(EQUAL(2,1))),OR(AND(EQUAL(1,1),NOT(EQUAL(2,1))),AND(NOT(EQUAL(1,1)),NOT(EQUAL(2,1)))))", node.toString());
        assertTrue(isNormalized(node));
    }

    @Test
    public void testNormalization9() {
        LTLFormula ltlFormula = parseLtlFormula("not( { 1=1 } W {2=1})");
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("UNTIL(NOT(EQUAL(2,1)),AND(NOT(EQUAL(1,1)),NOT(EQUAL(2,1))))", node.toString());
        assertTrue(isNormalized(node));
    }

    @Test
    public void testNormalization10() {
        LTLFormula ltlFormula = parseLtlFormula("G { 1=1 }");
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("RELEASE(FALSE,EQUAL(1,1))", node.toString());
        assertTrue(isNormalized(node));
    }

    @Test
    public void testNormalization11() {
        LTLFormula ltlFormula = parseLtlFormula("F { 1=1 }");
        LTLNode node = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("UNTIL(TRUE,EQUAL(1,1))", node.toString());
        assertTrue(isNormalized(node));
    }

    private boolean isNormalized(LTLNode formula) {
        if (formula instanceof LTLBPredicateNode) {
            return true;
        }
        if (formula instanceof LTLKeywordNode) {
            return true;
        }
        if (formula instanceof LTLInfixOperatorNode) {
            LTLInfixOperatorNode node = (LTLInfixOperatorNode) formula;
            switch (node.getKind()) {
                case UNTIL:
                case RELEASE:
                case AND:
                case OR:
                    return isNormalized(node.getLeft()) && isNormalized(node.getRight());
                default:
                    return false;
            }
        }
        if (formula instanceof LTLPrefixOperatorNode && ((LTLPrefixOperatorNode) formula).getKind() == NEXT) {
            return isNormalized(((LTLPrefixOperatorNode) formula).getArgument());
        }
        return false;
    }
}
