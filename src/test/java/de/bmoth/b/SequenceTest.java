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

public class SequenceTest {

    @Test
    public void emptySequenceTest() throws ParserException {
        String formula = "x = <> & x <: NAT*NAT";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER_INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void finiteSequencesTest() throws ParserException {
        String formula = "x = seq({1,2,3})";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_POW_INTEGER_INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void finiteNonEmptySequencesTest() throws ParserException {
        String formula = "x = seq1({1,2,3})";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_POW_INTEGER_INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void injectiveSequencesTest() throws ParserException {
        String formula = "x = iseq({1,2,3})";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_POW_INTEGER_INTEGER, declarationNode.getType().toString());
    }

    @Test
    @Ignore
    public void permutationsTest() throws ParserException {
        String formula = "x = perm({1,2,3})";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_POW_INTEGER_INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void sequenceConcatenationTest() throws ParserException {
        String formula = "x = {1|->2,2|->3}^{1|->4,2|->5}";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER_INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void prependTest() throws ParserException {
        String formula = "x = 5 -> {1|->2,2|->3}";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER_INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void appendTest() throws ParserException {
        String formula = "x = {1|->2,2|->3} <- 5";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER_INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void singletonSequenceTest() throws ParserException {
        String formula = "x = [2]";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER_INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void sequenceConstructionTest() throws ParserException {
        String formula = "x = [2,3,4]";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER_INTEGER, declarationNode.getType().toString());
    }

    @Test
    @Ignore
    public void sizeTest() throws ParserException {
        String formula = "x = size([2,3,4])";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(INTEGER, declarationNode.getType().toString());
    }

    @Test
    @Ignore
    public void reverseTest() throws ParserException {
        String formula = "x = rev([2,3,4])";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER_INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void takeTest() throws ParserException {
        String formula = "x = [2,3,4]/|\\2";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER_INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void dropTest() throws ParserException {
        String formula = "x = [2,3,4]\\|/2";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER_INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void firstTest() throws ParserException {
        String formula = "x = first([2,3,4,5])";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void lastTest() throws ParserException {
        String formula = "x = last([2,3,4,5])";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void tailTest() throws ParserException {
        String formula = "x = tail([2,3,4,5])";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER_INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void frontTest() throws ParserException {
        String formula = "x = front([2,3,4,5])";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER_INTEGER, declarationNode.getType().toString());
    }

    @Test
    @Ignore
    public void concatenationTest() throws ParserException {
        String formula = "x = conc([[1,2,3],[6,7,8]])";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER_INTEGER, declarationNode.getType().toString());
    }

    @Test
    @Ignore
    public void stringTest() throws ParserException {
        String formula = "x = \"test\"";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(POW_INTEGER_INTEGER, declarationNode.getType().toString());
    }

}
