package de.bmoth.b;

import de.bmoth.parser.Parser;
import de.bmoth.parser.ParserException;
import de.bmoth.parser.ast.nodes.DeclarationNode;
import de.bmoth.parser.ast.nodes.FormulaNode;
import org.junit.Ignore;
import org.junit.Test;

import static de.bmoth.parser.ast.nodes.FormulaNode.FormulaType.PREDICATE_FORMULA;
import static org.junit.Assert.assertEquals;

public class SetOperationsTest {

    @Test
    public void setSubstractionTest() throws ParserException {
        String formula = "x = {2,3} - {3}";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals("POW(INTEGER)", declarationNode.getType().toString());
    }

    @Test
    public void intervalTest() throws ParserException {
        String formula = "x = 2 .. 10";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals("POW(INTEGER)", declarationNode.getType().toString());
    }

    @Test
    public void elementTest() throws ParserException {
        String formula = "x : 4 .. 10";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals("INTEGER", declarationNode.getType().toString());
    }

    @Test @Ignore
    public void choiceTest() throws ParserException {
        String formula = "x :: 4 .. 10";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals("INTEGER", declarationNode.getType().toString());
    }

    @Test
    public void notElementTest() throws ParserException {
        String formula = "x /: 4 .. 10";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals("INTEGER", declarationNode.getType().toString());
    }

    @Test
    public void notIncludedOrEqualTest() throws ParserException {
        String formula = "x /<: 4 .. 10";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals("POW(INTEGER)", declarationNode.getType().toString());
    }

    @Test
    public void notIncludedTest() throws ParserException {
        String formula = "x /<<: 4 .. 10";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals("POW(INTEGER)", declarationNode.getType().toString());
    }

    @Test
    public void intersectionTest() throws ParserException {
        String formula = "x = 4 .. 10 /\\ 5 .. 7";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals("POW(INTEGER)", declarationNode.getType().toString());
    }
}
