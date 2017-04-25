package de.bmoth.typechecker;

import static org.junit.Assert.*;

import org.junit.Test;

import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.nodes.DeclarationNode;
import de.bmoth.parser.ast.nodes.FormulaNode;
import static de.bmoth.parser.ast.nodes.FormulaNode.FormulaType.*;

public class FormulaTest {

	@Test
	public void testExpressionFormula() throws Exception {
		String formula = "x + 2 + 3";
		FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
		assertEquals(EXPRESSION_FORMULA, formulaNode.getFormulaType());
		DeclarationNode declarationNode = formulaNode.getImplicitDeclarations().get(0);
		assertEquals("x", declarationNode.getName());
		assertEquals("INTEGER", declarationNode.getType().toString());
	}

	@Test
	public void testPredicateFormula() throws Exception {
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
	public void testSetEnumerationFormula() throws Exception {
		String formula = "a = {1,2,3} ";
		FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
		assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
		DeclarationNode node1 = formulaNode.getImplicitDeclarations().get(0);
		assertEquals("a", node1.getName());
		assertEquals("POW(INTEGER)", node1.getType().toString());
	}

	@Test
	public void testUnionIntersectionFormula() throws Exception {
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
	public void testCoupleFormula() throws Exception {
		String formula = "a = 1 |-> 2 ";
		FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
		assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
		DeclarationNode a = formulaNode.getImplicitDeclarations().get(0);
		assertEquals("a", a.getName());
		assertEquals("INTEGER*INTEGER", a.getType().toString());
	}

	@Test
	public void testDomOperator() throws Exception {
		String formula = "a = dom({1 |-> 2}) ";
		FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
		assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
		DeclarationNode a = formulaNode.getImplicitDeclarations().get(0);
		assertEquals("a", a.getName());
		assertEquals("POW(INTEGER)", a.getType().toString());
	}

	@Test
	public void testRanOperator() throws Exception {
		String formula = "a = ran({1 |-> 2}) ";
		FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
		assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
		DeclarationNode a = formulaNode.getImplicitDeclarations().get(0);
		assertEquals("a", a.getName());
		assertEquals("POW(INTEGER)", a.getType().toString());
	}

	@Test
	public void testTuple() throws Exception {
		String formula = "a = (1,2,3) ";
		FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
		assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
		DeclarationNode a = formulaNode.getImplicitDeclarations().get(0);
		assertEquals("a", a.getName());
		assertEquals("INTEGER*INTEGER*INTEGER", a.getType().toString());
	}

	@Test
	public void testTuple2() throws Exception {
		String formula = "a = (1,(2,3)) ";
		FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
		assertEquals(PREDICATE_FORMULA, formulaNode.getFormulaType());
		DeclarationNode a = formulaNode.getImplicitDeclarations().get(0);
		assertEquals("a", a.getName());
		assertEquals("INTEGER*(INTEGER*INTEGER)", a.getType().toString());
	}
}
