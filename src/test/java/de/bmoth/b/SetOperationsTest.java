package de.bmoth.b;

import de.bmoth.parser.Parser;
import de.bmoth.parser.ParserException;
import de.bmoth.parser.ast.nodes.DeclarationNode;
import de.bmoth.parser.ast.nodes.FormulaNode;
import org.junit.Ignore;
import org.junit.Test;

import static de.bmoth.TestConstants.*;
import static de.bmoth.parser.ast.nodes.FormulaNode.FormulaType.PREDICATE_FORMULA;
import static org.junit.Assert.assertEquals;

public class SetOperationsTest {

    @Test
    public void emptySetTest() throws ParserException{
        String formula = "x = {} & x <: NAT";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void setComprehensionTest() throws ParserException{
        String formula = "x = {z | z : 1 .. 10 & z < 3}";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void unionTest() throws ParserException {
        String formula = "x = 1 .. 3 \\/ {3,4,5}";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void intersectionTest() throws ParserException {
        String formula = "x = 4 .. 10 /\\ 5 .. 7";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void differenceTest() throws ParserException {
        String formula = "x = {2,3} - {3}";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void orderedPairTest() throws ParserException {
        String formula = "x = 2|->3";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(INTEGER_INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void cartesianProductTest() throws ParserException  {
        String formula = "x = {2,3} * {4,5}";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER_INTEGER, declarationNode.getType().toString());
    }

    @Test @Ignore
    public void powerSetTest() throws ParserException {
        String formula = "x = POW({4,5})";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER_INTEGER, declarationNode.getType().toString());
    }

    @Test @Ignore
    public void nonEmptyPowerSetTest() throws ParserException {
        String formula = "x = POW1({4,5})";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER_INTEGER, declarationNode.getType().toString());
    }

    @Test @Ignore
    public void finiteSubsetsTest() throws ParserException {
        String formula = "x = FIN({4,5})";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER_INTEGER, declarationNode.getType().toString());
    }

    @Test @Ignore
    public void nonEmptyFiniteSubsetsTest() throws ParserException {
        String formula = "x = FIN1({4,5})";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER_INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void cardinalityTest() throws ParserException {
        String formula = "x = card({4,5})";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void generalizedUnionTest() throws ParserException {
        String formula = "x = union({{4,5}, {1,2}})";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void generalizedIntersectionTest() throws ParserException {
        String formula = "x = inter({{4,5}, {1,2}})";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER, declarationNode.getType().toString());
    }

    //TODO: number 17 and 18 of the summary

    @Test
    public void membershipTest() throws ParserException {
        String formula = "x : 4 .. 10";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals("INTEGER", declarationNode.getType().toString());
    }

    @Test
    public void nonMemberShipTest() throws ParserException {
        String formula = "x /: 4 .. 10";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void subsetTest() throws ParserException {
        String formula = "x <: 4 .. 10";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void nonSubsetTest() throws ParserException {
        String formula = "x /<: 4 .. 10";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void properSubsetTest() throws ParserException {
        String formula = "x <<: 4 .. 10";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void nonProperSubsetTest() throws ParserException {
        String formula = "x /<<: 4 .. 10";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER, declarationNode.getType().toString());
    }

    @Test @Ignore
    public void choiceTest() throws ParserException {
        String formula = "x :: 4 .. 10";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(INTEGER, declarationNode.getType().toString());
    }
}
