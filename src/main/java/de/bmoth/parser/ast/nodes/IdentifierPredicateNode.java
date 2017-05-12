package de.bmoth.parser.ast.nodes;

import org.antlr.v4.runtime.Token;

public class IdentifierPredicateNode extends PredicateNode {

    final Token token;
    final String name;
    private final DeclarationNode declarationNode;

    public IdentifierPredicateNode(Token token, DeclarationNode declarationNode) {
        this.token = token;
        this.name = token.getText();
        this.declarationNode = declarationNode;
    }

    public DeclarationNode getDeclarationNode() {
        return declarationNode;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return name;
    }

}
