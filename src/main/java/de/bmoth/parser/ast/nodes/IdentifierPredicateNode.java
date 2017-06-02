package de.bmoth.parser.ast.nodes;

import org.antlr.v4.runtime.tree.TerminalNode;

public class IdentifierPredicateNode extends PredicateNode {

    final TerminalNode terminalNode;
    final String name;
    private final DeclarationNode declarationNode;

    public IdentifierPredicateNode(TerminalNode terminalNode, DeclarationNode declarationNode) {
        super(terminalNode);
        this.terminalNode = terminalNode;
        this.name = terminalNode.getSymbol().getText();
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
