package de.bmoth.ltl;

import de.bmoth.TestParser;
import de.bmoth.backend.ltl.LTLTransformations;
import de.bmoth.parser.ast.nodes.ltl.LTLBPredicateNode;
import de.bmoth.parser.ast.nodes.ltl.LTLFormula;
import de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LTLNormalizationTest extends TestParser {
    @Test
    public void testNormalization1() {
        LTLFormula ltlFormula = parseLtlFormula("not(GG { 1=1 })");
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("UNTIL(AND(WEAK_UNTIL(EQUAL(1,1),FALSE),NOT(FALSE)),AND(NOT(WEAK_UNTIL(EQUAL(1,1),FALSE)),NOT(FALSE)))", node1.toString());
    }

    @Test
    public void testNormalization2() {
        LTLFormula ltlFormula = parseLtlFormula("G not(E { 1=1 })");
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("WEAK_UNTIL(NOT(EQUAL(1,1)),FALSE)", node1.toString());
    }

    @Test
    public void testNormalization3() {
        LTLFormula ltlFormula = parseLtlFormula("{2=1} U ({1=3} U {5=5})");
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("UNTIL(EQUAL(2,1),UNTIL(EQUAL(1,3),EQUAL(5,5)))", node1.toString());
    }

    @Test
    public void testNormalization4() {
        LTLFormula ltlFormula = parseLtlFormula("({1:dom([1,2,3])} U {2 /: ran([1])}) U {2<17}");
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("UNTIL(UNTIL(ELEMENT_OF(1,DOMAIN(SEQ_ENUMERATION(1,2,3))),NOT_BELONGING(2,RANGE(SEQ_ENUMERATION(1)))),LESS(2,17))", node1.toString());
    }

    @Test
    public void testNormalization5() {
        LTLFormula ltlFormula = parseLtlFormula("not({ 1=1 })");
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());

        assertEquals("NOT(EQUAL(1,1))", node1.toString());

        // check if we have the B not, not the LTL not
        assertTrue(node1 instanceof LTLBPredicateNode);
    }

    @Test
    public void testNormalization6() {
        LTLFormula ltlFormula = parseLtlFormula("X {1<3} U {3>1}");
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("UNTIL(NEXT(LESS(1,3)),GREATER(3,1))", node1.toString());
    }

    @Test
    public void testNormalization7() {
        LTLFormula ltlFormula = parseLtlFormula("X ({1<3} U {3>1})");
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("UNTIL(NEXT(LESS(1,3)),NEXT(GREATER(3,1)))", node1.toString());
    }

    @Test
    public void testNormalization8() {
        LTLFormula ltlFormula = parseLtlFormula("not( { 1=1 } U {2=1})");
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("WEAK_UNTIL(AND(EQUAL(1,1),NOT(EQUAL(2,1))),AND(NOT(EQUAL(1,1)),NOT(EQUAL(2,1))))", node1.toString());
    }

    @Test
    public void testNormalization9() {
        LTLFormula ltlFormula = parseLtlFormula("not( { 1=1 } W {2=1})");
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("UNTIL(AND(EQUAL(1,1),NOT(EQUAL(2,1))),AND(NOT(EQUAL(1,1)),NOT(EQUAL(2,1))))", node1.toString());
    }

    @Test
    public void testNormalization10() {
        LTLFormula ltlFormula = parseLtlFormula("G { 1=1 }");
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("WEAK_UNTIL(EQUAL(1,1),FALSE)", node1.toString());
    }

    @Test
    public void testNormalization11() {
        LTLFormula ltlFormula = parseLtlFormula("F { 1=1 }");
        LTLNode node1 = LTLTransformations.transformLTLNode(ltlFormula.getLTLNode());
        assertEquals("UNTIL(TRUE,EQUAL(1,1))", node1.toString());
    }

    private boolean isNormalized(LTLNode formula) {
        if (formula instanceof LTLBPredicateNode) {
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
        return false;
    }
}
