package de.bmoth.b;

import de.bmoth.parser.Parser;
import de.bmoth.parser.ParserException;
import de.bmoth.parser.ast.nodes.DeclarationNode;
import de.bmoth.parser.ast.nodes.FormulaNode;
import org.junit.Ignore;
import org.junit.Test;

import static de.bmoth.parser.ast.nodes.FormulaNode.FormulaType.PREDICATE_FORMULA;
import static org.junit.Assert.assertEquals;

public class RelationTest {

    private static final String INTEGER = "INTEGER";
    private static final String POW_INTEGER = "POW(INTEGER)";
    private static final String POW_INTEGER_INTEGER = "POW(INTEGER*INTEGER)";

    @Test @Ignore
    public void relationTest() throws ParserException {
        String formula = "x = {1,2} <-> {10,20}";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void domainTest() throws ParserException {
        String formula = "x = dom({1|->2,2|->3})";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void rangeTest() throws ParserException {
        String formula = "x = ran({1|->2,2|->3})";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER, declarationNode.getType().toString());
    }

    @Test @Ignore
    public void forwardCompositionTest() throws ParserException {
        String formula = "x = ({1|->2,2|->3} ; {2|->1,3|->2})";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER_INTEGER, declarationNode.getType().toString());
    }

    @Test @Ignore
    public void identityTest() throws ParserException {
        String formula = "x = id({1,2})";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER_INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void domainRestrictionTest() throws ParserException {
        String formula = "x = {1,2}<|{1|->2,3|->4}";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER_INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void domainSubstractionTest() throws ParserException {
        String formula = "x = {1,2}<<|{1|->2,3|->4}";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER_INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void rangeRestrictionTest() throws ParserException {
        String formula = "x = {1|->2,3|->4} |> {1,2}";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER_INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void rangeSubstractionTest() throws ParserException {
        String formula = "x = {1|->2,3|->4} |>> {1,2}";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER_INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void inverseTest() throws ParserException {
        String formula = "x = {1|->2,3|->4}~";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER_INTEGER, declarationNode.getType().toString());
    }

    @Test @Ignore
    public void relationalImageTest() throws ParserException {
        String formula = "x = {1|->2,1|->7,3|->4}[{1}]";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void rightOverridingTest() throws ParserException {
        String formula = "x = {1|->2}<+{1|->3}";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER_INTEGER, declarationNode.getType().toString());
    }


    @Test
    public void directProductTest() throws ParserException {
        String formula = "x = {1|->2}><{1|->3}";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals("POW(INTEGER*(INTEGER*INTEGER))", declarationNode.getType().toString());
    }

    @Test @Ignore
    public void parallelProductTest() throws ParserException {
        String formula = "x = ({1|->2}||{1|->3})";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals("POW(INTEGER*(INTEGER*INTEGER))", declarationNode.getType().toString());
    }

    @Test @Ignore
    public void iterationTest() throws ParserException {
        String formula = "x = iterate({1|->2,2|->3},2)";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER_INTEGER, declarationNode.getType().toString());
    }

    @Test @Ignore
    public void closureTest() throws ParserException {
        String formula = "x = closure({1|->2, 2|->3})";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER_INTEGER, declarationNode.getType().toString());
    }

    @Test @Ignore
    public void projection1Test() throws ParserException {
        String formula = "x = prj1({1,2},{3,4})";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER_INTEGER, declarationNode.getType().toString());
    }

    @Test @Ignore
    public void projection2Test() throws ParserException {
        String formula = "x = prj2({1,2},{3,4})";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER_INTEGER, declarationNode.getType().toString());
    }
}
