package de.bmoth.ltl;

import org.junit.Test;
import static org.junit.Assert.*;
import de.bmoth.backend.ltl.LTLTransformations;
import de.bmoth.parser.Parser;
import de.bmoth.parser.ParserException;
import de.bmoth.parser.ast.nodes.ltl.LTLFormula;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;

public class LTLTransformationTest {

    
    @Test
    public void testTransformationNotGloballyToFinallyNot() throws ParserException {
        String formula = "not(G { 1=1 })";
        LTLFormula node = Parser.getLTLFormulaAsSemanticAst(formula);
        LTLNode node1 = LTLTransformations.transformLTLNode(node);
        System.out.println(node.getFormula());
        System.out.println(node1);
        
    }
}
