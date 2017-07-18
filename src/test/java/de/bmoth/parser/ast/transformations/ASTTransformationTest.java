package de.bmoth.parser.ast.transformations;

import de.bmoth.backend.ltl.LTLTransformationUtil;
import de.bmoth.backend.z3.AstTransformationsForZ3;
import de.bmoth.parser.ast.nodes.FormulaNode;
import org.junit.Test;

import static de.bmoth.TestParser.parseFormula;
import static net.trajano.commons.testing.UtilityClassTestUtil.assertUtilityClassWellDefined;
import static org.junit.Assert.assertEquals;

public class ASTTransformationTest {
    @Test
    public void testElementOfCombinedWithUnion() {
        String formula = "a : {1} \\/ b";
        FormulaNode formulaNode = parseFormula(formula);
        formulaNode = AstTransformationsForZ3.transformFormulaNode(formulaNode);
        assertEquals("OR(ELEMENT_OF(a,SET_ENUMERATION(1)),ELEMENT_OF(a,b))", formulaNode.getFormula().toString());
    }

    @Test
    public void testElementOfCombinedWithMultipleUnions() {
        String formula = "a : {1} \\/ b \\/ c";
        FormulaNode formulaNode = parseFormula(formula);
        formulaNode = AstTransformationsForZ3.transformFormulaNode(formulaNode);
        assertEquals("OR(OR(ELEMENT_OF(a,SET_ENUMERATION(1)),ELEMENT_OF(a,b)),ELEMENT_OF(a,c))",
            formulaNode.getFormula().toString());
    }

    @Test
    public void testElementOfCombinedWithIntersection() {
        String formula = "a : {1} /\\ b";
        FormulaNode formulaNode = parseFormula(formula);
        formulaNode = AstTransformationsForZ3.transformFormulaNode(formulaNode);
        assertEquals("AND(ELEMENT_OF(a,SET_ENUMERATION(1)),ELEMENT_OF(a,b))", formulaNode.getFormula().toString());
    }

    @Test
    public void testElementOfCombinedWithMultipleIntersections() {
        String formula = "a : {1} /\\ b /\\ c";
        FormulaNode formulaNode = parseFormula(formula);
        formulaNode = AstTransformationsForZ3.transformFormulaNode(formulaNode);
        assertEquals("AND(AND(ELEMENT_OF(a,SET_ENUMERATION(1)),ELEMENT_OF(a,b)),ELEMENT_OF(a,c))",
            formulaNode.getFormula().toString());
    }

    @Test
    public void testMemberOfIntervalToLeqGeq() {
        String formula = "a : 1..7";
        FormulaNode formulaNode = parseFormula(formula);
        formulaNode = AstTransformationsForZ3.transformFormulaNode(formulaNode);
        assertEquals("AND(GREATER_EQUAL(a,1),LESS_EQUAL(a,7))", formulaNode.getFormula().toString());
    }

    @Test
    public void testMemberOfIntervalInsideComprehensionToLeqGeq() {
        String formula = "sc = {a | a : 1..7}";
        FormulaNode formulaNode = parseFormula(formula);
        formulaNode = AstTransformationsForZ3.transformFormulaNode(formulaNode);
        assertEquals("EQUAL(sc,SET_COMPREHENSION(a,AND(GREATER_EQUAL(a,1),LESS_EQUAL(a,7))))", formulaNode.getFormula().toString());
    }

    @Test
    public void testLTLTransformationUtil() throws ReflectiveOperationException {
        assertUtilityClassWellDefined(LTLTransformationUtil.class);
    }
}
