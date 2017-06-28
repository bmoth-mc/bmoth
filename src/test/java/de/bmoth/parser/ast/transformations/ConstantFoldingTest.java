package de.bmoth.parser.ast.transformations;

import de.bmoth.backend.z3.AstTransformationsForZ3;
import de.bmoth.parser.ast.nodes.FormulaNode;
import de.bmoth.parser.ast.nodes.PredicateNode;
import org.junit.Test;

import static de.bmoth.TestParser.parseFormula;
import static org.junit.Assert.assertEquals;

public class ConstantFoldingTest {
    @Test
    public void testIntegerAddition() {
        String formula = "a = 1+2+3";
        FormulaNode formulaNode = parseFormula(formula);
        PredicateNode op = AstTransformationsForZ3.transformPredicate((PredicateNode) formulaNode.getFormula());
        assertEquals("EQUAL(a,6)", op.toString());
    }
}
