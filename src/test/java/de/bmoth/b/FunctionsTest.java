package de.bmoth.b;


import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.nodes.DeclarationNode;
import de.bmoth.parser.ast.nodes.FormulaNode;
import org.junit.Ignore;
import org.junit.Test;

import static de.bmoth.parser.ast.nodes.FormulaNode.FormulaType.PREDICATE_FORMULA;
import static org.junit.Assert.assertEquals;

public class FunctionsTest {

    @Test @Ignore
    public void totalFunctionTest() {
        String formula = "x = {2,3} --> {1,4}";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        //assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        //assertEquals("x", declarationNode.getName());
        //assertEquals("POW(INTEGER)", declarationNode.getType().toString());
    }

    @Test @Ignore
    public void totalSurjectionTest() {
        String formula = "x = {2,3} -->> {1,4}";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        //assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        //assertEquals("x", declarationNode.getName());
        //assertEquals("POW(INTEGER)", declarationNode.getType().toString());
    }

    @Test @Ignore
    public void partialFunctionTest() {
        String formula = "x = {2,3} +-> {1,4}";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals("POW(INTEGER)", declarationNode.getType().toString());
    }

    @Test @Ignore
    public void partialSurjectionTest() {
        String formula = "x = {2,3} +->> {1,4}";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals("POW(INTEGER)", declarationNode.getType().toString());
    }
    
}
