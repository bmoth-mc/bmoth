package de.bmoth.parser.ast;

import de.bmoth.backend.z3.AstTransformationsForZ3;
import de.bmoth.parser.ast.nodes.FormulaNode;
import de.bmoth.parser.ast.nodes.PredicateNode;
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
}
