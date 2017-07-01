package de.bmoth.parser.ast.nodes;

import org.antlr.v4.runtime.tree.TerminalNode;

public class IdentifierExprNode extends ExprNode {

    final TerminalNode terminalNode;
    final String name;
    private final DeclarationNode declarationNode;

    public IdentifierExprNode(TerminalNode terminalNode, DeclarationNode declarationNode) {
        super(terminalNode);
        this.terminalNode = terminalNode;
        this.name = terminalNode.getText();
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

    @Override
    public boolean equalAst(Node other) {
        if (!NodeUtil.isSameClass(this, other)) {
            return false;
        }

        IdentifierExprNode that = (IdentifierExprNode) other;
        return this.name.equals(that.name)
            && this.declarationNode.equalAst(that.declarationNode);
    }
}
