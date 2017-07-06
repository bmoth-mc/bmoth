package de.bmoth.parser.ast.nodes;

import org.antlr.v4.runtime.tree.ParseTree;

public class DeferredSetNode extends ExprNode {

    private DeclarationNode declarationNode;
    private String name;

    public DeferredSetNode(ParseTree parseTree, DeclarationNode declNode, String name) {
        super(parseTree);
        this.declarationNode = declNode;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public DeclarationNode getDeclarationNode() {
        return this.declarationNode;
    }

    @Override
    public boolean equalAst(Node other) {
        if (!NodeUtil.isSameClass(this, other)) {
            return false;
        }

        DeferredSetNode that = (DeferredSetNode) other;
        return this.name.equals(that.name)
            && this.declarationNode.equalAst(that.declarationNode);
    }
}
