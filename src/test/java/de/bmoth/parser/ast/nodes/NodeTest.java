package de.bmoth.parser.ast.nodes;

import com.google.common.collect.Lists;
import de.bmoth.TestParser;
import de.bmoth.parser.ast.nodes.FormulaNode.FormulaType;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;

import static de.bmoth.parser.ast.nodes.ConditionSubstitutionNode.Kind.ASSERT;
import static de.bmoth.parser.ast.nodes.ConditionSubstitutionNode.Kind.PRECONDITION;
import static de.bmoth.parser.ast.nodes.ExpressionOperatorNode.ExpressionOperator.DOMAIN;
import static de.bmoth.parser.ast.nodes.FormulaNode.FormulaType.EXPRESSION_FORMULA;
import static de.bmoth.parser.ast.nodes.FormulaNode.FormulaType.PREDICATE_FORMULA;
import static de.bmoth.parser.ast.nodes.PredicateOperatorNode.PredicateOperator.AND;
import static de.bmoth.parser.ast.nodes.PredicateOperatorWithExprArgsNode.PredOperatorExprArgs.GREATER_EQUAL;
import static de.bmoth.parser.ast.nodes.QuantifiedExpressionNode.QuantifiedExpressionOperator.QUANTIFIED_INTER;
import static de.bmoth.parser.ast.nodes.QuantifiedPredicateNode.QuantifiedPredicateOperator.EXISTENTIAL_QUANTIFICATION;
import static org.junit.Assert.*;

public class NodeTest extends TestParser {

    @Test
    public void testAnySubstitutionNode() {
        MachineNode machine = new MachineBuilder()
            .setName("AnySubstitutionMachine")
            .setVariables("a")
            .setInvariant("a : INTEGER")
            .setInitialization("a := 0")
            .addOperation("anyUpA = ANY x WHERE x > 0 THEN a := x END")
            .addOperation("anyDown = ANY x WHERE x < 0 THEN a := x END")
            .addOperation("anyUpB = ANY x WHERE x > 0 THEN a := x END")
            .addOperation("anyUpFailParams = ANY y WHERE y > 0 THEN a := y END")
            .addOperation("anyUpFailSubstitutions = ANY x WHERE x > 0 THEN a := x + 1 END")
            .build();

        AnySubstitutionNode anyUpA = (AnySubstitutionNode) machine.getOperations().get(0).getSubstitution();
        AnySubstitutionNode anyDown = (AnySubstitutionNode) machine.getOperations().get(1).getSubstitution();
        AnySubstitutionNode assembleAnyUp = new AnySubstitutionNode(anyUpA.getParameters(), anyDown.getWherePredicate(), anyDown.getThenSubstitution());
        AnySubstitutionNode anyUpB = (AnySubstitutionNode) machine.getOperations().get(2).getSubstitution();
        AnySubstitutionNode anyUpFailParams = (AnySubstitutionNode) machine.getOperations().get(3).getSubstitution();
        AnySubstitutionNode anyUpFailSubstitutions = (AnySubstitutionNode) machine.getOperations().get(4).getSubstitution();

        assertEquals("ANY [x] WHERE GREATER(x,0) THEN a := x END", anyUpA.toString());
        assertEquals("ANY [x] WHERE LESS(x,0) THEN a := x END", anyDown.toString());

        assembleAnyUp.setPredicate(anyUpA.getWherePredicate());
        assembleAnyUp.setSubstitution(anyUpA.getThenSubstitution());
        assertEquals(anyUpA.toString(), assembleAnyUp.toString());

        assertTrue(anyUpA.equalAst(anyUpB));
        assertFalse(anyUpA.equalAst(anyDown));
        assertFalse(anyUpA.equalAst(anyUpFailParams));
        assertFalse(anyUpA.equalAst(anyUpFailSubstitutions));
        assertFalse(anyUpA.equalAst(other -> false));
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

        assertArrayEquals(new ConditionSubstitutionNode.Kind[]{PRECONDITION, ASSERT}, ConditionSubstitutionNode.Kind.values());
        assertEquals(PRECONDITION, ConditionSubstitutionNode.Kind.valueOf("PRECONDITION"));
        assertEquals(ASSERT, ConditionSubstitutionNode.Kind.valueOf("ASSERT"));
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

        otherNode.setOperator(GREATER_EQUAL);
        assertEquals("GREATER_EQUAL(x,1)", otherNode.toString());

        assertEquals(GREATER_EQUAL, PredicateOperatorWithExprArgsNode.PredOperatorExprArgs.valueOf("GREATER_EQUAL"));
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
        DeclarationNode node1 = ((IdentifierExprNode) ((PredicateOperatorWithExprArgsNode) parseFormula("\n  x = 1").getFormula()).getExpressionNodes().get(0)).getDeclarationNode();
        DeclarationNode node2 = ((IdentifierExprNode) ((PredicateOperatorWithExprArgsNode) parseFormula("x = 1").getFormula()).getExpressionNodes().get(0)).getDeclarationNode();
        DeclarationNode node3 = ((IdentifierExprNode) ((PredicateOperatorWithExprArgsNode) parseFormula("y = 1").getFormula()).getExpressionNodes().get(0)).getDeclarationNode();

        assertEquals(2, node1.getLine());
        assertEquals(2, node1.getPos());

        assertTrue(node1.equalAst(node2));
        assertFalse(node1.equalAst(other -> false));
        assertFalse(node1.equalAst(node3));
    }

    @Test
    public void testFormulaNode() {
        assertEquals(EXPRESSION_FORMULA, FormulaNode.FormulaType.valueOf("EXPRESSION_FORMULA"));
        assertArrayEquals(new FormulaNode.FormulaType[]{EXPRESSION_FORMULA, PREDICATE_FORMULA},
            FormulaType.values());
    }

    @Test
    public void testQuantifiedExpressionNode() {
        assertEquals(QUANTIFIED_INTER,
            QuantifiedExpressionNode.QuantifiedExpressionOperator.valueOf("QUANTIFIED_INTER"));
    }

    @Test
    public void testNumberNode() {
        NumberNode node2578921A = new NumberNode(null, new BigInteger("2578921"));
        NumberNode node2578921B = new NumberNode(null, new BigInteger("2578921"));
        NumberNode node1 = new NumberNode(null, BigInteger.ONE);

        assertFalse(node2578921A.equalAst(node1));
        assertFalse(node2578921A.equalAst(other -> false));
        assertTrue(node2578921A.equalAst(node2578921B));
    }

    @Test
    public void testIdentifierExpressionNode() {
        IdentifierExprNode node = new IdentifierExprNode(getTerminalNode("x"), new DeclarationNode(null, "x"));
        IdentifierExprNode nodeFailName = new IdentifierExprNode(getTerminalNode("y"), new DeclarationNode(null, "y"));
        IdentifierExprNode nodeFailDeclaration = new IdentifierExprNode(getTerminalNode("x"), new DeclarationNode(null, "y"));

        assertFalse(node.equalAst(nodeFailName));
        assertFalse(node.equalAst(nodeFailDeclaration));
    }

    @Test
    public void testSkipSubstitutionNode() {
        SkipSubstitutionNode node1 = new SkipSubstitutionNode();
        SkipSubstitutionNode node2 = new SkipSubstitutionNode();

        assertTrue(node1.equalAst(node2));
        assertFalse(node1.equalAst(other -> false));
    }

    @Test
    public void testSingleAssignSubstitutionNode() {
        SingleAssignSubstitutionNode node = new SingleAssignSubstitutionNode(new IdentifierExprNode(getTerminalNode("x"), new DeclarationNode(null, "x")), new NumberNode(null, BigInteger.ONE));
        SingleAssignSubstitutionNode nodeSetValue = new SingleAssignSubstitutionNode(new IdentifierExprNode(getTerminalNode("x"), new DeclarationNode(null, "x")), new NumberNode(null, BigInteger.ZERO));
        SingleAssignSubstitutionNode nodeFailIdentifier = new SingleAssignSubstitutionNode(new IdentifierExprNode(getTerminalNode("y"), new DeclarationNode(null, "y")), new NumberNode(null, BigInteger.ONE));

        assertFalse(node.equalAst(nodeFailIdentifier));
        assertFalse(node.equalAst(other -> false));

        nodeSetValue.setValue(new NumberNode(null, BigInteger.ONE));
        assertTrue(node.equalAst(nodeSetValue));
    }

    @Test
    public void testNodeUtil() {
        assertFalse(NodeUtil.isSameClass(first -> false, null));
        assertFalse(NodeUtil.isSameClass(null, second -> false));

        assertFalse(NodeUtil.equalAst(Lists.asList(element -> false, new Node[0]), Collections.emptyList()));
    }

    private static TerminalNode getTerminalNode(String name) {
        return new TerminalNode() {
            @Override
            public Interval getSourceInterval() {
                return null;
            }

            @Override
            public ParseTree getParent() {
                return null;
            }

            @Override
            public Object getPayload() {
                return null;
            }

            @Override
            public ParseTree getChild(int i) {
                return null;
            }

            @Override
            public int getChildCount() {
                return 0;
            }

            @Override
            public String toStringTree() {
                return null;
            }

            @Override
            public void setParent(RuleContext parent) {
                throw new UnsupportedOperationException();
            }

            @Override
            public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
                return null;
            }

            @Override
            public String getText() {
                return name;
            }

            @Override
            public String toStringTree(Parser parser) {
                return null;
            }

            @Override
            public Token getSymbol() {
                return null;
            }
        };
    }
}
