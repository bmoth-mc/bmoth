package de.bmoth.parser.ast.nodes;

import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.nodes.FormulaNode.FormulaType;

import org.junit.Test;

import java.util.ArrayList;

import static de.bmoth.parser.ast.nodes.ExpressionOperatorNode.ExpressionOperator.DOMAIN;
import static de.bmoth.parser.ast.nodes.FormulaNode.FormulaType.EXPRESSION_FORMULA;
import static de.bmoth.parser.ast.nodes.FormulaNode.FormulaType.PREDICATE_FORMULA;
import static de.bmoth.parser.ast.nodes.PredicateOperatorNode.PredicateOperator.AND;
import static de.bmoth.parser.ast.nodes.QuantifiedExpressionNode.QuatifiedExpressionOperator.SET_COMPREHENSION;
import static de.bmoth.parser.ast.nodes.QuantifiedPredicateNode.QuantifiedPredicateOperator.EXISTENTIAL_QUANTIFICATION;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class NodeTest {
    @Test
    public void testOperationNode() {
        MachineNode machine = Parser.getMachineAsSemanticAst(
                "MACHINE OpNodeMachine\nVARIABLES x\nPROPERTIES 1=1\nINVARIANT x:INTEGER\nOPERATIONS\n\tset = BEGIN x := 1 END;\n\tselect = SELECT x = 1 THEN x := x END\nEND");
        OperationNode setOperation = machine.getOperations().get(0);
        OperationNode selectOperation = machine.getOperations().get(1);
        assertEquals("set", setOperation.getName());
        assertEquals("set = BEGIN x := 1 END", setOperation.toString());
        assertEquals("select = SELECT EQUAL(x,1) THEN x := x END", selectOperation.toString());
        assertEquals("[x]", setOperation.getAssignedDeclarationNodes().toString());
    }

    @Test
    public void testPredicateOperatorNode() {
        PredicateOperatorNode andNode = (PredicateOperatorNode) Parser.getFormulaAsSemanticAst("x & y").getFormula();
        Node orNode = Parser.getFormulaAsSemanticAst("x or y").getFormula();
        Node trueNode = Parser.getFormulaAsSemanticAst("TRUE").getFormula();

        assertEquals("AND(x,y)", andNode.toString());
        assertEquals("OR(x,y)", orNode.toString());
        assertEquals("TRUE", trueNode.toString());
        assertEquals(AND, PredicateOperatorNode.PredicateOperator.valueOf("AND"));
    }

    @Test
    public void testPredicateOperatorWithExprArgsNode() {
        PredicateOperatorWithExprArgsNode equalNode = (PredicateOperatorWithExprArgsNode) Parser
                .getFormulaAsSemanticAst("x = 1").getFormula();

        PredicateOperatorWithExprArgsNode otherNode = (PredicateOperatorWithExprArgsNode) Parser
                .getFormulaAsSemanticAst("x <= 1").getFormula();

        assertEquals("EQUAL(x,1)", equalNode.toString());
        assertEquals("LESS_EQUAL(x,1)", otherNode.toString());

        otherNode.changeOperator(PredicateOperatorWithExprArgsNode.PredOperatorExprArgs.GREATER_EQUAL);
        assertEquals("GREATER_EQUAL(x,1)", otherNode.toString());
    }

    @Test
    public void testExpressionOperatorNode() {
        ExpressionOperatorNode node = (ExpressionOperatorNode) Parser.getFormulaAsSemanticAst("1**1").getFormula();

        assertEquals("POWER_OF(1,1)", node.toString());

        node.changeOperator(ExpressionOperatorNode.ExpressionOperator.TRUE);
        node.setExpressionList(new ArrayList<>());
        assertEquals("TRUE", node.toString());

        assertEquals(DOMAIN, ExpressionOperatorNode.ExpressionOperator.valueOf("DOMAIN"));
    }

    @Test
    public void testQuantifiedPredicateNode() {
        QuantifiedPredicateNode existsNode = (QuantifiedPredicateNode) Parser.getFormulaAsSemanticAst("#x.(x > 0)")
                .getFormula();
        QuantifiedPredicateNode forAllNode = (QuantifiedPredicateNode) Parser.getFormulaAsSemanticAst("!x.(x > 0)")
                .getFormula();

        assertEquals("EXISTS(x,GREATER(x,0))", existsNode.toString());
        assertEquals("FORALL(x,GREATER(x,0))", forAllNode.toString());

        assertEquals(EXISTENTIAL_QUANTIFICATION,
                QuantifiedPredicateNode.QuantifiedPredicateOperator.valueOf("EXISTENTIAL_QUANTIFICATION"));
    }

    @Test
    public void testCastPredicateExpressionNode() {
        CastPredicateExpressionNode node = (CastPredicateExpressionNode) Parser.getFormulaAsSemanticAst("bool(FALSE)")
                .getFormula();

        assertEquals("bool(FALSE)", node.toString());
    }

    @Test
    public void testDeclarationNode() {
        DeclarationNode node = ((IdentifierExprNode) ((PredicateOperatorWithExprArgsNode) Parser
                .getFormulaAsSemanticAst("\n  x = 1").getFormula()).getExpressionNodes().get(0)).getDeclarationNode();
        assertEquals(2, node.getLine());
        assertEquals(2, node.getPos());
    }

    @Test
    public void testFormulaNode() {
        assertEquals(EXPRESSION_FORMULA, FormulaNode.FormulaType.valueOf("EXPRESSION_FORMULA"));
        assertArrayEquals(new FormulaNode.FormulaType[] { EXPRESSION_FORMULA, PREDICATE_FORMULA },
                FormulaType.values());
    }

    @Test
    public void testQuantifiedExpressionNode() {
        assertEquals(SET_COMPREHENSION,
                QuantifiedExpressionNode.QuatifiedExpressionOperator.valueOf("SET_COMPREHENSION"));
    }
}
