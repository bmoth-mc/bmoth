package de.bmoth.parser.ast.visitors;

import de.bmoth.parser.ast.nodes.Node;

public interface ASTTransformation {
    
    boolean canHandleNode(Node node);

    Node transformNode(Node node);
}
