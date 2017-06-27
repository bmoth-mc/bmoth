package de.bmoth.parser.ast;

import de.bmoth.backend.z3.AstTransformationsForZ3;
import de.bmoth.parser.ast.nodes.FormulaNode;
import de.bmoth.parser.ast.nodes.PredicateNode;
import org.junit.Ignore;
import org.junit.Test;

import static de.bmoth.TestParser.parseFormula;
import static org.junit.Assert.assertEquals;

public class ASTTransformationTest {
    @Test
    public void testElementOfCombinedWithUnion() {
        String formula = "a : {1} \\/ b";
        FormulaNode formulaNode = parseFormula(formula);
        PredicateNode op = AstTransformationsForZ3.transformPredicate((PredicateNode) formulaNode.getFormula());
        assertEquals("OR(ELEMENT_OF(a,SET_ENUMERATION(1)),ELEMENT_OF(a,b))", op.toString());
    }

    @Test
    @Ignore
    public void testElementOfCombinedWithMultipleUnions() {
        String formula = "a : {1} \\/ b \\/ c";
        FormulaNode formulaNode = parseFormula(formula);
        PredicateNode op = AstTransformationsForZ3.transformPredicate((PredicateNode) formulaNode.getFormula());
        assertEquals("OR(ELEMENT_OF(a,SET_ENUMERATION(1)),ELEMENT_OF(a,b))", op.toString());
    }

    @Test
    public void testElementOfCombinedWithIntersection() {
        String formula = "a : {1} /\\ b";
        FormulaNode formulaNode = parseFormula(formula);
        PredicateNode op = AstTransformationsForZ3.transformPredicate((PredicateNode) formulaNode.getFormula());
        assertEquals("AND(ELEMENT_OF(a,SET_ENUMERATION(1)),ELEMENT_OF(a,b))", op.toString());
    }

    @Test
    @Ignore
    public void testElementOfCombinedWithMultipleIntersections() {
        String formula = "a : {1} /\\ b /\\ c";
        FormulaNode formulaNode = parseFormula(formula);
        PredicateNode op = AstTransformationsForZ3.transformPredicate((PredicateNode) formulaNode.getFormula());
        assertEquals("AND(ELEMENT_OF(a,SET_ENUMERATION(1)),ELEMENT_OF(a,b))", op.toString());
    }

    @Test
    public void testMemberOfIntervalToLeqGeq() {
        String formula = "a : 1..7";
        FormulaNode formulaNode = parseFormula(formula);
        PredicateNode op = AstTransformationsForZ3.transformPredicate((PredicateNode) formulaNode.getFormula());
        assertEquals("AND(GREATER_EQUAL(a,1),LESS_EQUAL(a,7))", op.toString());
    }
}
