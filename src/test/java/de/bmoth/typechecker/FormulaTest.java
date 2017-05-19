package de.bmoth.typechecker;

import de.bmoth.exceptions.TypeErrorException;
import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.nodes.DeclarationNode;
import de.bmoth.parser.ast.nodes.FormulaNode;
import de.bmoth.parser.ast.nodes.QuantifiedExpressionNode;
import de.bmoth.parser.ast.nodes.QuantifiedPredicateNode;
import org.junit.Test;

import java.util.List;

import static de.bmoth.parser.ast.nodes.FormulaNode.FormulaType.EXPRESSION_FORMULA;
import static de.bmoth.parser.ast.nodes.FormulaNode.FormulaType.PREDICATE_FORMULA;
import static org.junit.Assert.assertEquals;

public class FormulaTest {

    @Test
    public void testExpressionFormula() {
        String formula = "x + 2 + 3";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(EXPRESSION_FORMULA, formulaNode.getFormulaType());
        DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", declarationNode.getName());
        assertEquals("INTEGER", declarationNode.getType().toString());
    }

    @Test
    public void testPredicateFormula() {
        String formula = "a = b & b = 1";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode node1 = formulaNode.getImplicitDeclarations().get(0);
        DeclarationNode node2 = formulaNode.getImplicitDeclarations().get(1);
        assertEquals("a", node1.getName());
        assertEquals("b", node2.getName());
        assertEquals("INTEGER", node1.getType().toString());
        assertEquals("INTEGER", node2.getType().toString());
    }

    @Test
    public void testArithmeticMinus() {
        String formula = "a - 1";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        DeclarationNode node1 = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("a", node1.getName());
        assertEquals("INTEGER", node1.getType().toString());
    }

    @Test
    public void testSetMinus() {
        String formula = "a - {1}";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        DeclarationNode node1 = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("a", node1.getName());
        assertEquals("POW(INTEGER)", node1.getType().toString());
    }

    @Test
    public void testMult() {
        String formula = "a * 1";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        DeclarationNode node1 = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("a", node1.getName());
        assertEquals("INTEGER", node1.getType().toString());
    }

    @Test
    public void testMult2() {
        String formula = "4 + 3 * 2 * 2";
        Parser.getFormulaAsSemanticAst(formula);
    }

    @Test
    public void testCartesianProduct() {
        String formula = "a * {1} = {TRUE |-> b}";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        DeclarationNode a = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("a", a.getName());
        assertEquals("POW(BOOL)", a.getType().toString());
        DeclarationNode b = formulaNode.getImplicitDeclarations().get(1);
        assertEquals("b", b.getName());
        assertEquals("INTEGER", b.getType().toString());
    }

    @Test(expected = TypeErrorException.class)
    public void testEmptySetError() {
        String formula = "{} = {}";
        Parser.getFormulaAsSemanticAst(formula);
    }

    @Test
    public void testSetMinus2() {
        String formula = "a - b = c & c = {TRUE}";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        DeclarationNode node1 = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("a", node1.getName());
        assertEquals("POW(BOOL)", node1.getType().toString());
    }

    @Test
    public void testSetEnumerationFormula() {
        String formula = "a = {1,2,3} ";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode node1 = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("a", node1.getName());
        assertEquals("POW(INTEGER)", node1.getType().toString());
    }

    @Test
    public void testUnionIntersectionFormula() {
        String formula = "a = {1} \\/ b  /\\ {c} ";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode a = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("a", a.getName());
        assertEquals("POW(INTEGER)", a.getType().toString());

        DeclarationNode b = formulaNode.getImplicitDeclarations().get(1);
        assertEquals("b", b.getName());
        assertEquals("POW(INTEGER)", b.getType().toString());

        DeclarationNode c = formulaNode.getImplicitDeclarations().get(2);
        assertEquals("c", c.getName());
        assertEquals("INTEGER", c.getType().toString());
    }

    @Test
    public void testCouple() {
        String formula = "a = 1 |-> 2 ";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode a = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("a", a.getName());
        assertEquals("INTEGER*INTEGER", a.getType().toString());
    }

    @Test
    public void testCouple2() {
        String formula = "1|->x = y |-> 2 ";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode x = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", x.getName());
        assertEquals("INTEGER", x.getType().toString());
        DeclarationNode y = formulaNode.getImplicitDeclarations().get(1);
        assertEquals("y", y.getName());
        assertEquals("INTEGER", y.getType().toString());
    }

    @Test
    public void testRelation() {
        String formula = "{1|->x} = {y |-> 2} ";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode x = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("x", x.getName());
        assertEquals("INTEGER", x.getType().toString());
        DeclarationNode y = formulaNode.getImplicitDeclarations().get(1);
        assertEquals("y", y.getName());
        assertEquals("INTEGER", y.getType().toString());
    }

    @Test
    public void testDomOperator() {
        String formula = "a = dom({1 |-> 2}) ";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode a = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("a", a.getName());
        assertEquals("POW(INTEGER)", a.getType().toString());
    }

    @Test
    public void testRanOperator() {
        String formula = "a = ran({1 |-> 2}) ";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode a = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("a", a.getName());
        assertEquals("POW(INTEGER)", a.getType().toString());
    }

    @Test
    public void testMinintMaxint() {
        String formula = "a = MININT & b = MAXINT ";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode a = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("a", a.getName());
        assertEquals("INTEGER", a.getType().toString());

        DeclarationNode b = formulaNode.getImplicitDeclarations().get(1);
        assertEquals("b", b.getName());
        assertEquals("INTEGER", b.getType().toString());
    }

    @Test
    public void testNatInt() {
        String formula = "a : NAT & b : INT ";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode a = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("a", a.getName());
        assertEquals("INTEGER", a.getType().toString());

        DeclarationNode b = formulaNode.getImplicitDeclarations().get(1);
        assertEquals("b", b.getName());
        assertEquals("INTEGER", b.getType().toString());
    }

    @Test
    public void testTuple() {
        String formula = "a = (1,2,3) ";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode a = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("a", a.getName());
        assertEquals("INTEGER*INTEGER*INTEGER", a.getType().toString());
    }

    @Test
    public void testTuple2() {
        String formula = "a = (1,(2,3)) ";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode a = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("a", a.getName());
        assertEquals("INTEGER*(INTEGER*INTEGER)", a.getType().toString());
    }

    @Test
    public void testElementOf() {
        String formula = "a : INTEGER";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode a = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("a", a.getName());
        assertEquals("INTEGER", a.getType().toString());
    }

    @Test
    public void testSetComprehension() {
        String formula = "a = {x | x : INTEGER & 1=1}";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        DeclarationNode a = formulaNode.getImplicitDeclarations().get(0);
        assertEquals("a", a.getName());
        assertEquals("POW(INTEGER)", a.getType().toString());
    }

    @Test
    public void testSetComprehension2() {
        String formula = "{a,b,c | a = b & b = c & c = 1 }";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(EXPRESSION_FORMULA, formulaNode.getFormulaType());
        QuantifiedExpressionNode setComprehension = (QuantifiedExpressionNode) formulaNode.getFormula();

        assertEquals(QuantifiedExpressionNode.QuatifiedExpressionOperator.SET_COMPREHENSION,
            setComprehension.getOperator());
        List<DeclarationNode> declarationList = setComprehension.getDeclarationList();
        DeclarationNode a = declarationList.get(0);
        DeclarationNode b = declarationList.get(1);
        DeclarationNode c = declarationList.get(2);
        assertEquals("a", a.getName());
        assertEquals("b", b.getName());
        assertEquals("c", c.getName());

        assertEquals("INTEGER", a.getType().toString());
        assertEquals("INTEGER", b.getType().toString());
        assertEquals("INTEGER", c.getType().toString());

        assertEquals("POW(INTEGER*INTEGER*INTEGER)", setComprehension.getType().toString());
    }

    @Test
    public void testUniversalQuantification() {
        String formula = "!x,y.(x : NATURAL => x : y)";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        QuantifiedPredicateNode quantification = (QuantifiedPredicateNode) formulaNode.getFormula();

        assertEquals(QuantifiedPredicateNode.QuantifiedPredicateOperator.UNIVERSAL_QUANTIFICATION,
            quantification.getOperator());
        List<DeclarationNode> declarationList = quantification.getDeclarationList();
        DeclarationNode x = declarationList.get(0);
        DeclarationNode y = declarationList.get(1);
        assertEquals("x", x.getName());
        assertEquals("y", y.getName());
        assertEquals("INTEGER", x.getType().toString());
        assertEquals("POW(INTEGER)", y.getType().toString());
    }

    @Test
    public void testExistentialQuantification() {
        String formula = "#x,y.(x : NATURAL & x : y)";
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
        QuantifiedPredicateNode quantification = (QuantifiedPredicateNode) formulaNode.getFormula();

        assertEquals(QuantifiedPredicateNode.QuantifiedPredicateOperator.EXISTENTIAL_QUANTIFICATION,
            quantification.getOperator());
        List<DeclarationNode> declarationList = quantification.getDeclarationList();
        DeclarationNode x = declarationList.get(0);
        DeclarationNode y = declarationList.get(1);
        assertEquals("x", x.getName());
        assertEquals("y", y.getName());
        assertEquals("INTEGER", x.getType().toString());
        assertEquals("POW(INTEGER)", y.getType().toString());
    }

}
