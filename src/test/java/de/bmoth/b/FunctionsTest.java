package de.bmoth.b;


import de.bmoth.parser.Parser;
import de.bmoth.parser.ParserException;
import de.bmoth.parser.ast.nodes.DeclarationNode;
import de.bmoth.parser.ast.nodes.FormulaNode;
import org.junit.Ignore;
import org.junit.Test;

import static de.bmoth.parser.ast.nodes.FormulaNode.FormulaType.PREDICATE_FORMULA;
import static org.junit.Assert.assertEquals;

public class FunctionsTest {

    @Test @Ignore
    public void partialFunctionTest() throws ParserException {
        String formula = "x = {2,3} +-> {1,4}";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals("POW(INTEGER*INTEGER)", declarationNode.getType().toString());
    }

    @Test @Ignore
    public void totalFunctionTest() throws ParserException {
        String formula = "x = {2,3} --> {1,4}";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals("POW(INTEGER*INTEGER)", declarationNode.getType().toString());
    }

    @Test @Ignore
    public void partialInjectionTest() throws ParserException {
        String formula = "x = {2,3} >+> {1,4}";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals("POW(INTEGER*INTEGER)", declarationNode.getType().toString());
    }

    @Test @Ignore
    public void totalInjectionTest() throws ParserException {
        String formula = "x = {2,3} >-> {1,4}";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals("POW(INTEGER*INTEGER)", declarationNode.getType().toString());
    }

    @Test @Ignore
    public void partialSurjectionTest() throws ParserException {
        String formula = "x = {2,3} +->> {1,4}";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals("POW(INTEGER*INTEGER)", declarationNode.getType().toString());
    }

    @Test @Ignore
    public void totalSurjectionTest() throws ParserException {
        String formula = "x = {2,3} -->> {1,4}";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals("POW(INTEGER*INTEGER)", declarationNode.getType().toString());
    }

    @Test @Ignore
    public void bijectionTest() throws ParserException {
        String formula = "x = {2,3} >->> {1,4}";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals("POW(INTEGER*INTEGER)", declarationNode.getType().toString());
    }

    @Test @Ignore
    public void lambdaTest() throws ParserException {
        String formula = "x = %z.(z : 1..10|z*z)";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals("POW(INTEGER*INTEGER)", declarationNode.getType().toString());
    }

    @Test
    public void functionApplicationTest() throws ParserException {
        String formula = "x = {1|->2, 2|->3,3|->4}(1)";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals("INTEGER", declarationNode.getType().toString());
    }




}
