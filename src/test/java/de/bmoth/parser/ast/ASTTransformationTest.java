package de.bmoth.parser.ast;

import static de.bmoth.parser.ast.nodes.FormulaNode.FormulaType.*;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.bmoth.backend.z3.AstTransformationsForZ3;
import de.bmoth.parser.ast.nodes.ExprNode;
import de.bmoth.parser.ast.nodes.ExpressionOperatorNode;
import de.bmoth.parser.ast.nodes.FormulaNode;
import de.bmoth.parser.ast.nodes.PredicateNode;
import static de.bmoth.TestParser.*;
public class ASTTransformationTest {

    @Test
    public void testFlattenUnion() {
        String formula = "({1} \\/ a) \\/ b";
        FormulaNode formulaNode = parseFormula(formula);
        assertEquals(EXPRESSION_FORMULA, formulaNode.getFormulaType());
        ExprNode transformExprNode = AstTransformationsForZ3
                .transformExprNode((ExpressionOperatorNode) formulaNode.getFormula());
        assertEquals("UNION(SET_ENUMERATION(1),a,b)", transformExprNode.toString());
    }

    @Test
    public void testElementOfCombinedWithUnion() {
        String formula = "a : {1} \\/ b";
        FormulaNode formulaNode = parseFormula(formula);
        PredicateNode op = AstTransformationsForZ3.transformPredicate((PredicateNode) formulaNode.getFormula());
        assertEquals("OR(ELEMENT_OF(a,SET_ENUMERATION(1)),ELEMENT_OF(a,b))", op.toString());
    }
}
