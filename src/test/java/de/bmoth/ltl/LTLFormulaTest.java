package de.bmoth.ltl;

import de.bmoth.parser.Parser;
import de.bmoth.parser.ParserException;
import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLBPredicateNode;
import de.bmoth.parser.ast.nodes.ltl.LTLFormula;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LTLFormulaTest {
    @Test
    public void testGloballyFinally() throws ParserException {
        String formula = "FG { 1=1 }";
        LTLFormula ltlFormula = Parser.getLTLFormulaAsSemanticAst(formula);
        Node f1 = ltlFormula.getLTLNode();
        assertTrue(f1 instanceof LTLPrefixOperatorNode);
        LTLPrefixOperatorNode p1 = (LTLPrefixOperatorNode) f1;
        assertEquals(LTLPrefixOperatorNode.Kind.FINALLY, p1.getKind());
        LTLNode f2 = p1.getArgument();
        assertTrue(f2 instanceof LTLPrefixOperatorNode);
        LTLPrefixOperatorNode p2 = (LTLPrefixOperatorNode) f2;
        assertEquals(LTLPrefixOperatorNode.Kind.GLOBALLY, p2.getKind());
        LTLNode f3 = p2.getArgument();
        assertTrue(f3 instanceof LTLBPredicateNode);
        LTLBPredicateNode p3 = (LTLBPredicateNode) f3;
        assertTrue(p3.getPredicate() != null);
    }

    @Test
    public void testParentheses() throws ParserException {
        String formula = "F ({ 1=1 })";
        Parser.getLTLFormulaAsSemanticAst(formula);
    }

    @Test
    public void testAnd() throws ParserException {
        String formula = " { 1=1 } & {1=1}";
        Parser.getLTLFormulaAsSemanticAst(formula);
    }

    @Test
    public void testUntil() throws ParserException {
        String formula = " { 1=1 } U {1=1}";
        Parser.getLTLFormulaAsSemanticAst(formula);
    }

    @Test
    public void testWeakUntil() throws ParserException {
        String formula = " { 1=1 } W {1=1}";
        Parser.getLTLFormulaAsSemanticAst(formula);
    }

    @Test
    public void testSet() throws ParserException {
        String formula = " { {} = {1} }";
        Parser.getLTLFormulaAsSemanticAst(formula);
    }

    @Test
    public void testSet2() throws ParserException {
        String formula = " {1<3} U { 1 : {1} }";
        Parser.getLTLFormulaAsSemanticAst(formula);
    }

}
