package de.bmoth.backend.ltl.transformation;

import de.bmoth.antlr.BMoThParser;
import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.ltl.LTLInfixOperatorNode;
import de.bmoth.parser.ast.nodes.ltl.LTLNode;
import de.bmoth.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
import de.bmoth.parser.ast.visitors.AbstractASTTransformation;

public class ConvertPhiUntilPhiUntilPsiToPhiUntilPsi extends AbstractASTTransformation{

    @Override
    public boolean canHandleNode(Node node) {
        return node instanceof LTLInfixOperatorNode;
    }

    @Override
    public Node transformNode(Node oldNode) {
        LTLInfixOperatorNode untilOperator01 = (LTLInfixOperatorNode) oldNode;
        if (untilOperator01.getKind() == LTLInfixOperatorNode.Kind.UNTIL) {
            LTLNode argument01Left = untilOperator01.getLeft();
            LTLNode argument01Right = untilOperator01.getRight();
            if (argument01Right instanceof LTLInfixOperatorNode) {
                LTLInfixOperatorNode operator02 = (LTLInfixOperatorNode) argument01Right;
                // case U(x,U(x,y))->U(x,y)
                if (operator02.getKind() == LTLInfixOperatorNode.Kind.UNTIL) {
                    LTLNode argument02Left = operator02.getLeft();
                    if (argument01Left == argument02Left) {
                        setChanged();
                        return operator02;
                    }
                }
            }
            if (argument01Left instanceof LTLInfixOperatorNode) {
                LTLInfixOperatorNode operator02 = (LTLInfixOperatorNode) argument01Left;
                // case U(U(x,y),y)->U(x,y)
                if (operator02.getKind() == LTLInfixOperatorNode.Kind.UNTIL) {
                    LTLNode argument02Right = operator02.getRight();
                    if (argument01Right == argument02Right) {
                        setChanged();
                        return operator02;
                    }
                }
            }
        }
        return oldNode;
    }
}
