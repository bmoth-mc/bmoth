package de.bmoth.parser.typechecker;

import de.bmoth.parser.ast.nodes.DeclarationNode;
import de.bmoth.parser.ast.nodes.FormulaNode;
import org.junit.Test;

import static de.bmoth.TestConstants.INTEGER;
import static de.bmoth.parser.ast.nodes.FormulaNode.FormulaType.EXPRESSION_FORMULA;
import static de.bmoth.parser.ast.nodes.FormulaNode.FormulaType.PREDICATE_FORMULA;
import static de.bmoth.parser.typechecker.TestTypechecker.*;
import static org.junit.Assert.assertEquals;
import static de.bmoth.TestParser.*;

public class OperatorCoverageTest {

    @Test
    public void testExpressionFormula() {
        String formula = "x - 2 / 3";
        FormulaNode formulaNode = parseFormula(formula);
        assertEquals(EXPRESSION_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void testPredicateFormula() {
        String formula = "a * b = x & b = 1";
        FormulaNode formulaNode = parseFormula(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode node1 = formulaNode.getImplicitDeclarations().get(0);
        DeclarationNode node2 = formulaNode.getImplicitDeclarations().get(1);
        assertEquals("a", node1.getName());
        assertEquals("b", node2.getName());
        assertEquals(INTEGER, node1.getType().toString());
        assertEquals(INTEGER, node2.getType().toString());
    }

    @Test
    public void testPredicateFormulaError() {
        String formula = "x = 2 / 3 & b : x ";
        typeCheckFormulaAndGetErrorMessage(formula);
    }

}
