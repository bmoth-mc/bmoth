package de.bmoth.parser.ast.nodes;

import de.bmoth.TestParser;
import de.bmoth.parser.ast.nodes.FormulaNode.FormulaType;
import org.junit.Test;

import java.util.ArrayList;

import static de.bmoth.parser.ast.nodes.ExpressionOperatorNode.ExpressionOperator.DOMAIN;
import static de.bmoth.parser.ast.nodes.FormulaNode.FormulaType.EXPRESSION_FORMULA;
import static de.bmoth.parser.ast.nodes.FormulaNode.FormulaType.PREDICATE_FORMULA;
import static de.bmoth.parser.ast.nodes.PredicateOperatorNode.PredicateOperator.AND;
import static de.bmoth.parser.ast.nodes.QuantifiedExpressionNode.QuantifiedExpressionOperator.SET_COMPREHENSION;
import static de.bmoth.parser.ast.nodes.QuantifiedPredicateNode.QuantifiedPredicateOperator.EXISTENTIAL_QUANTIFICATION;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class NodeTest extends TestParser {

    @Test
    public void testAnySubstitutionNode() {
        MachineNode machine = new MachineBuilder()
            .setName("AnySubstitutionMachine")
            .setVariables("a")
            .setInvariant("a : INTEGER")
            .setInitialization("a := 0")
            .addOperation("anyUp = ANY x WHERE x > 0 THEN a := x END")
            .addOperation("anyDown = ANY x WHERE x < 0 THEN a := x END")
            .build();

        AnySubstitutionNode anyUp = (AnySubstitutionNode) machine.getOperations().get(0).getSubstitution();
        AnySubstitutionNode anyDown = (AnySubstitutionNode) machine.getOperations().get(1).getSubstitution();
        AnySubstitutionNode assembleAnyUp = new AnySubstitutionNode(anyUp.getParameters(), anyDown.getWherePredicate(), anyDown.getThenSubstitution());


        assertEquals("ANY [x] WHERE GREATER(x,0) THEN a := x END", anyUp.toString());
        assertEquals("ANY [x] WHERE LESS(x,0) THEN a := x END", anyDown.toString());

        assembleAnyUp.setPredicate(anyUp.getWherePredicate());
        assembleAnyUp.setSubstitution(anyUp.getThenSubstitution());

        assertEquals(anyUp.toString(), assembleAnyUp.toString());
    }

    @Test
    public void testConditionSubstitutionNode() {
        MachineNode machine = new MachineBuilder()
            .setName("CondSubstNodeMachine")
            .setVariables("x")
            .setInvariant("x : INTEGER")
            .setInitialization("x := 1")
            .addOperation("condOp1 = SELECT x = 1 THEN x := 2 END")
            .addOperation("condOp2 = SELECT x = 25 THEN x := 5000 END")
            .build();

        SelectSubstitutionNode condSub1 = (SelectSubstitutionNode) machine.getOperations().get(0).getSubstitution();
        SelectSubstitutionNode condSub2 = (SelectSubstitutionNode) machine.getOperations().get(1).getSubstitution();

        assertEquals("[x]", condSub1.getAssignedVariables().toString());
        assertEquals("[x]", condSub2.getAssignedVariables().toString());

        assertEquals("EQUAL(x,1)", condSub1.getConditions().get(0).toString());
        assertEquals("EQUAL(x,25)", condSub2.getConditions().get(0).toString());

        assertEquals("x := 2", condSub1.getSubstitutions().get(0).toString());
        assertEquals("x := 5000", condSub2.getSubstitutions().get(0).toString());
    }

    @Test
    public void testOperationNode() {
        MachineNode machine = new MachineBuilder()
            .setName("OpNodeMachine")
            .setVariables("x")
            .setProperties("1 = 1")
            .setInvariant("x : INTEGER")
            .addOperation("set = BEGIN x := 1 END")
            .addOperation("select = SELECT x = 1 THEN x := x END")
            .build();

        OperationNode setOperation = machine.getOperations().get(0);
        OperationNode selectOperation = machine.getOperations().get(1);
        assertEquals("set", setOperation.getName());
        assertEquals("set = BEGIN x := 1 END", setOperation.toString());
        assertEquals("select = SELECT EQUAL(x,1) THEN x := x END", selectOperation.toString());
        assertEquals("[x]", setOperation.getAssignedDeclarationNodes().toString());
    }

    @Test
    public void testPredicateOperatorNode() {
        PredicateOperatorNode andNode = (PredicateOperatorNode) parseFormula("x & y").getFormula();
        Node orNode = parseFormula("x or y").getFormula();
        Node trueNode = parseFormula("TRUE").getFormula();

        assertEquals("AND(x,y)", andNode.toString());
        assertEquals("OR(x,y)", orNode.toString());
        assertEquals("TRUE", trueNode.toString());
        assertEquals(AND, PredicateOperatorNode.PredicateOperator.valueOf("AND"));
    }

    @Test
    public void testPredicateOperatorWithExprArgsNode() {
        PredicateOperatorWithExprArgsNode equalNode = (PredicateOperatorWithExprArgsNode) parseFormula("x = 1").getFormula();

        PredicateOperatorWithExprArgsNode otherNode = (PredicateOperatorWithExprArgsNode) parseFormula("x <= 1").getFormula();

        assertEquals("EQUAL(x,1)", equalNode.toString());
        assertEquals("LESS_EQUAL(x,1)", otherNode.toString());

        otherNode.setOperator(PredicateOperatorWithExprArgsNode.PredOperatorExprArgs.GREATER_EQUAL);
        assertEquals("GREATER_EQUAL(x,1)", otherNode.toString());
    }

    @Test
    public void testExpressionOperatorNode() {
        ExpressionOperatorNode node = (ExpressionOperatorNode) parseFormula("1**1").getFormula();

        assertEquals("POWER_OF(1,1)", node.toString());

        node.setOperator(ExpressionOperatorNode.ExpressionOperator.TRUE);
        node.setExpressionList(new ArrayList<>());
        assertEquals("TRUE", node.toString());

        assertEquals(DOMAIN, ExpressionOperatorNode.ExpressionOperator.valueOf("DOMAIN"));
    }

    @Test
    public void testQuantifiedPredicateNode() {
        QuantifiedPredicateNode existsNode = (QuantifiedPredicateNode) parseFormula("#x.(x > 0)")
            .getFormula();
        QuantifiedPredicateNode forAllNode = (QuantifiedPredicateNode) parseFormula("!x.(x > 0)")
            .getFormula();

        assertEquals("EXISTS(x,GREATER(x,0))", existsNode.toString());
        assertEquals("FORALL(x,GREATER(x,0))", forAllNode.toString());

        assertEquals(EXISTENTIAL_QUANTIFICATION,
            QuantifiedPredicateNode.QuantifiedPredicateOperator.valueOf("EXISTENTIAL_QUANTIFICATION"));
    }

    @Test
    public void testCastPredicateExpressionNode() {
        CastPredicateExpressionNode node = (CastPredicateExpressionNode) parseFormula("bool(FALSE)")
            .getFormula();

        assertEquals("bool(FALSE)", node.toString());
    }

    @Test
    public void testDeclarationNode() {
        DeclarationNode node = ((IdentifierExprNode) ((PredicateOperatorWithExprArgsNode) parseFormula("\n  x = 1").getFormula()).getExpressionNodes().get(0)).getDeclarationNode();
        assertEquals(2, node.getLine());
        assertEquals(2, node.getPos());
    }

    @Test
    public void testFormulaNode() {
        assertEquals(EXPRESSION_FORMULA, FormulaNode.FormulaType.valueOf("EXPRESSION_FORMULA"));
        assertArrayEquals(new FormulaNode.FormulaType[]{EXPRESSION_FORMULA, PREDICATE_FORMULA},
            FormulaType.values());
    }

    @Test
    public void testQuantifiedExpressionNode() {
        assertEquals(SET_COMPREHENSION,
            QuantifiedExpressionNode.QuantifiedExpressionOperator.valueOf("SET_COMPREHENSION"));
    }
}
