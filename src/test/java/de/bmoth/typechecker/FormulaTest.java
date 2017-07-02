package de.bmoth.typechecker;

import de.bmoth.parser.ast.nodes.*;
import org.junit.Test;

import java.util.List;

import static de.bmoth.TestConstants.INTEGER;
import static de.bmoth.TestConstants.POW_INTEGER;
import static de.bmoth.TestParser.parseFormula;
import static de.bmoth.parser.ast.nodes.FormulaNode.FormulaType.EXPRESSION_FORMULA;
import static de.bmoth.parser.ast.nodes.FormulaNode.FormulaType.PREDICATE_FORMULA;
import static de.bmoth.typechecker.TestTypechecker.typeCheckFormulaAndGetErrorMessage;
import static org.junit.Assert.assertEquals;

public class FormulaTest {


    @Test
    public void testExpressionFormula() {
        String formula = "x + 2 + 3";
        FormulaNode formulaNode = parseFormula(formula);
        assertEquals(EXPRESSION_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals(INTEGER, declarationNode.getType().toString());
    }

    @Test
    public void testPredicateFormula() {
        String formula = "a = b & b = 1";
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
    public void testArithmeticMinus() {
        String formula = "a - 1";
        FormulaNode formulaNode = parseFormula(formula);
        DeclarationNode node1 = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("a", node1.getName());
        assertEquals(INTEGER, node1.getType().toString());
    }

    @Test
    public void testSetMinus() {
        String formula = "a - {1}";
        FormulaNode formulaNode = parseFormula(formula);
        DeclarationNode node1 = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("a", node1.getName());
        assertEquals(POW_INTEGER, node1.getType().toString());
    }

    @Test
    public void testMult() {
        String formula = "a * 1";
        FormulaNode formulaNode = parseFormula(formula);
        DeclarationNode node1 = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("a", node1.getName());
        assertEquals(INTEGER, node1.getType().toString());
    }

    @Test
    public void testMult2() {
        String formula = "4 + 3 * 2 * 2";
        parseFormula(formula);
    }

    @Test
    public void testCartesianProduct() {
        String formula = "a * {1} = {TRUE |-> b}";
        FormulaNode formulaNode = parseFormula(formula);
        DeclarationNode a = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("a", a.getName());
        assertEquals("POW(BOOL)", a.getType().toString());
        DeclarationNode b = formulaNode.getImplicitDeclarations().get(1);
        assertEquals("b", b.getName());
        assertEquals(INTEGER, b.getType().toString());
    }

    @Test
    public void testEmptySetError() {
        String formula = "{} = {}";
        typeCheckFormulaAndGetErrorMessage(formula);
    }

    @Test
    public void testSetMinus2() {
        String formula = "a - b = c & c = {TRUE}";
        FormulaNode formulaNode = parseFormula(formula);
        DeclarationNode node1 = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("a", node1.getName());
        assertEquals("POW(BOOL)", node1.getType().toString());
    }

    @Test
    public void testSetEnumerationFormula() {
        String formula = "a = {1,2,3} ";
        FormulaNode formulaNode = parseFormula(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode node1 = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("a", node1.getName());
        assertEquals(POW_INTEGER, node1.getType().toString());
    }

    @Test
    public void testUnionIntersectionFormula() {
        String formula = "a = {1} \\/ b  /\\ {c} ";
        FormulaNode formulaNode = parseFormula(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode a = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("a", a.getName());
        assertEquals(POW_INTEGER, a.getType().toString());

        DeclarationNode b = formulaNode.getImplicitDeclarations().get(1);
        assertEquals("b", b.getName());
        assertEquals(POW_INTEGER, b.getType().toString());

        DeclarationNode c = formulaNode.getImplicitDeclarations().get(2);
        assertEquals("c", c.getName());
        assertEquals(INTEGER, c.getType().toString());
    }

    @Test
    public void testCouple() {
        String formula = "a = 1 |-> 2 ";
        FormulaNode formulaNode = parseFormula(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode a = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("a", a.getName());
        assertEquals("INTEGER*INTEGER", a.getType().toString());
    }

    @Test
    public void testCouple2() {
        String formula = "1|->x = y |-> 2 ";
        FormulaNode formulaNode = parseFormula(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode x = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", x.getName());
        assertEquals(INTEGER, x.getType().toString());
        DeclarationNode y = formulaNode.getImplicitDeclarations().get(1);
        assertEquals("y", y.getName());
        assertEquals(INTEGER, y.getType().toString());
    }

    @Test
    public void testRelation() {
        String formula = "{1|->x} = {y |-> 2} ";
        FormulaNode formulaNode = parseFormula(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode x = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", x.getName());
        assertEquals(INTEGER, x.getType().toString());
        DeclarationNode y = formulaNode.getImplicitDeclarations().get(1);
        assertEquals("y", y.getName());
        assertEquals(INTEGER, y.getType().toString());
    }

    @Test
    public void testDomOperator() {
        String formula = "a = dom({1 |-> 2}) ";
        FormulaNode formulaNode = parseFormula(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode a = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("a", a.getName());
        assertEquals(POW_INTEGER, a.getType().toString());
    }

    @Test
    public void testRanOperator() {
        String formula = "a = ran({1 |-> 2}) ";
        FormulaNode formulaNode = parseFormula(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode a = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("a", a.getName());
        assertEquals(POW_INTEGER, a.getType().toString());
    }

    @Test
    public void testMinintMaxint() {
        String formula = "a = MININT & b = MAXINT ";
        FormulaNode formulaNode = parseFormula(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode a = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("a", a.getName());
        assertEquals(INTEGER, a.getType().toString());

        DeclarationNode b = formulaNode.getImplicitDeclarations().get(1);
        assertEquals("b", b.getName());
        assertEquals(INTEGER, b.getType().toString());
    }

    @Test
    public void testNatInt() {
        String formula = "a : NAT & b : INT ";
        FormulaNode formulaNode = parseFormula(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode a = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("a", a.getName());
        assertEquals(INTEGER, a.getType().toString());

        DeclarationNode b = formulaNode.getImplicitDeclarations().get(1);
        assertEquals("b", b.getName());
        assertEquals(INTEGER, b.getType().toString());
    }

    @Test
    public void testTuple() {
        String formula = "a = (1,2,3) ";
        FormulaNode formulaNode = parseFormula(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode a = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("a", a.getName());
        assertEquals("INTEGER*INTEGER*INTEGER", a.getType().toString());
    }

    @Test
    public void testTuple2() {
        String formula = "a = (1,(2,3)) ";
        FormulaNode formulaNode = parseFormula(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode a = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("a", a.getName());
        assertEquals("INTEGER*(INTEGER*INTEGER)", a.getType().toString());
    }

    @Test
    public void testElementOf() {
        String formula = "a : INTEGER";
        FormulaNode formulaNode = parseFormula(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode a = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("a", a.getName());
        assertEquals(INTEGER, a.getType().toString());
    }

    @Test
    public void testSetComprehension() {
        String formula = "a = {x | x : INTEGER & 1=1}";
        FormulaNode formulaNode = parseFormula(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode a = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("a", a.getName());
        assertEquals(POW_INTEGER, a.getType().toString());
    }

    @Test
    public void testSetComprehension2() {
        String formula = "{a,b,c | a = b & b = c & c = 1 }";
        FormulaNode formulaNode = parseFormula(formula);
        assertEquals(EXPRESSION_FORMULA, formulaNode.getFormulaType());
        SetComprehensionNode setComprehension = (SetComprehensionNode) formulaNode.getFormula();
        
        List<DeclarationNode> declarationList = setComprehension.getDeclarationList();
        DeclarationNode a = declarationList.get(0);
        DeclarationNode b = declarationList.get(1);
        DeclarationNode c = declarationList.get(2);
        assertEquals("a", a.getName());
        assertEquals("b", b.getName());
        assertEquals("c", c.getName());

        assertEquals(INTEGER, a.getType().toString());
        assertEquals(INTEGER, b.getType().toString());
        assertEquals(INTEGER, c.getType().toString());

        assertEquals("POW(INTEGER*INTEGER*INTEGER)", setComprehension.getType().toString());
    }

    @Test
    public void testUniversalQuantification() {
        String formula = "!x,y.(x : NATURAL => x : y)";
        FormulaNode formulaNode = parseFormula(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        QuantifiedPredicateNode quantification = (QuantifiedPredicateNode) formulaNode.getFormula();

        assertEquals(QuantifiedPredicateNode.QuantifiedPredicateOperator.UNIVERSAL_QUANTIFICATION,
            quantification.getOperator());
        List<DeclarationNode> declarationList = quantification.getDeclarationList();
        DeclarationNode x = declarationList.get(0);
        DeclarationNode y = declarationList.get(1);
        assertEquals("x", x.getName());
        assertEquals("y", y.getName());
        assertEquals(INTEGER, x.getType().toString());
        assertEquals(POW_INTEGER, y.getType().toString());
    }

    @Test
    public void testExistentialQuantification() {
        String formula = "#x,y.(x : NATURAL & x : y)";
        FormulaNode formulaNode = parseFormula(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        QuantifiedPredicateNode quantification = (QuantifiedPredicateNode) formulaNode.getFormula();

        assertEquals(QuantifiedPredicateNode.QuantifiedPredicateOperator.EXISTENTIAL_QUANTIFICATION,
            quantification.getOperator());
        List<DeclarationNode> declarationList = quantification.getDeclarationList();
        DeclarationNode x = declarationList.get(0);
        DeclarationNode y = declarationList.get(1);
        assertEquals("x", x.getName());
        assertEquals("y", y.getName());
        assertEquals(INTEGER, x.getType().toString());
        assertEquals(POW_INTEGER, y.getType().toString());
    }

    @Test
    public void cannotInferFormulaType() {
        String formula = "x - y";
        typeCheckFormulaAndGetErrorMessage(formula);
    }

    @Test
    public void cannotInferTypeOfLocalVariable() {
        String formula = "x = y";
        typeCheckFormulaAndGetErrorMessage(formula);
    }

    @Test
    public void testDirectProduct() {
        String formula = "{1 |-> 2} >< {1 |-> 3}";
        FormulaNode node = parseFormula(formula);
        assertEquals(EXPRESSION_FORMULA, node.getFormulaType());
        ExpressionOperatorNode exprNode = (ExpressionOperatorNode) node.getFormula();
        assertEquals(2, exprNode.getArity());
        assertEquals(ExpressionOperatorNode.ExpressionOperator.DIRECT_PRODUCT, exprNode.getOperator());
    }

}
