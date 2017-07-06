package de.bmoth.parser.ast.transformations;

import de.bmoth.backend.z3.AstTransformationsForZ3;
import de.bmoth.parser.ast.nodes.FormulaNode;
import org.junit.Test;

import static de.bmoth.TestParser.parseFormula;
import static org.junit.Assert.assertEquals;

public class ConstantFoldingTest {
    @Test
    public void testIntegerAddition() {
        String formula = "a = 1+2+3";
        FormulaNode formulaNode = parseFormula(formula);
        formulaNode = AstTransformationsForZ3.transformFormulaNode(formulaNode);
        assertEquals("EQUAL(a,6)", formulaNode.getFormula().toString());
    }
}
