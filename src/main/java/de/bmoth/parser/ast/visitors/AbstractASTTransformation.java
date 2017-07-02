package de.bmoth.parser.ast.visitors;

import de.bmoth.parser.ast.nodes.Node;

public abstract class AbstractASTTransformation {
    public abstract boolean canHandleNode(Node node);

    public abstract Node transformNode(Node node);
}
