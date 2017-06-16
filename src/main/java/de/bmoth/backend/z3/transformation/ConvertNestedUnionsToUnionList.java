package de.bmoth.backend.z3.transformation;

import de.bmoth.parser.ast.nodes.ExprNode;
import de.bmoth.parser.ast.nodes.ExpressionOperatorNode;
import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.visitors.AbstractASTTransformation;

import java.util.ArrayList;
import java.util.List;

public class ConvertNestedUnionsToUnionList extends AbstractASTTransformation {

    @Override
    public boolean canHandleNode(Node node) {
        return node instanceof ExpressionOperatorNode;
    }

    @Override
    public Node transformNode(Node node2) {
        ExpressionOperatorNode node = (ExpressionOperatorNode) node2;
        final List<ExprNode> arguments = node.getExpressionNodes();
        if (node.getOperator() == ExpressionOperatorNode.ExpressionOperator.UNION) {
            List<ExprNode> list = new ArrayList<>();
            for (ExprNode expr : node.getExpressionNodes()) {
                if (expr instanceof ExpressionOperatorNode
                    && ((ExpressionOperatorNode) expr).getOperator() == ExpressionOperatorNode.ExpressionOperator.UNION) {
                    list.addAll(((ExpressionOperatorNode) expr).getExpressionNodes());
                    setChanged();
                } else {
                    list.add(expr);
                }
            }
            node.setExpressionList(list);
            return node;
        } else {
            node.setExpressionList(arguments);
            return node;
        }
    }
}
