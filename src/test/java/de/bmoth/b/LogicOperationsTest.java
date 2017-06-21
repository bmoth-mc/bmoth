package de.bmoth.b;

import de.bmoth.parser.Parser;
import de.bmoth.parser.ParserException;
import de.bmoth.parser.ast.nodes.DeclarationNode;
import de.bmoth.parser.ast.nodes.FormulaNode;
import org.junit.Test;

import static de.bmoth.TestConstants.*;
import static de.bmoth.parser.ast.nodes.FormulaNode.FormulaType.PREDICATE_FORMULA;
import static org.junit.Assert.assertEquals;

public class LogicOperationsTest {

    @Test
    public void conjunctionTest() throws ParserException {
        String formula = "x < 3 & x > 4";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void disjunctionTest() throws ParserException {
        String formula = "x < 3 or x > 4";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void implicationTest() throws ParserException {
        String formula = "x < 3 => x < 4";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void equivalenceTest() throws ParserException {
        String formula = "x < y <=> y > x";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(INTEGER, declarationNode.getType().toString());
        DeclarationNode declarationNode2 = formulaNode.getImplicitDeclarations().get(1);
        assertEquals("y", declarationNode2.getName());
        assertEquals(INTEGER, declarationNode2.getType().toString());
    }

    @Test
    public void negationTest() throws ParserException {
        String formula = "x = bool(not(3>2))";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(BOOL, declarationNode.getType().toString());
    }

    @Test
    public void universalQuantificationTest() throws ParserException {
        String formula = "!x . (x : 1 .. 10 => x > 0)";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
    }

    @Test
    public void existentialQuantificationTest() throws ParserException {
        String formula = "#x . (x : 1 .. 10 => x > 0)";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
    }

    @Test
    public void inEqualityTest() throws ParserException {
        String formula = "x /= 3";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(INTEGER, declarationNode.getType().toString());
    }

}
