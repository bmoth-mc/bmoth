package de.bmoth.parser.ast.nodes;

import org.antlr.v4.runtime.tree.ParseTree;

public class EnumerationSetNode extends ExprNode {

    final EnumeratedSetDeclarationNode enumeratedSetDeclarationNode;
    final String name;

    public EnumerationSetNode(ParseTree parseTree, EnumeratedSetDeclarationNode enumeratedSetNode, String name) {
        super(parseTree);
        this.enumeratedSetDeclarationNode = enumeratedSetNode;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public EnumeratedSetDeclarationNode getEnumeratedSetDeclarationNode() {
        return this.enumeratedSetDeclarationNode;
    }

    @Override
    public boolean equalAst(Node other) {
        if (!NodeUtil.isSameClass(this, other)) {
            return false;
        }

        EnumerationSetNode that = (EnumerationSetNode) other;
        return this.name.equals(that.name)
            && this.enumeratedSetDeclarationNode.equalAst(that.enumeratedSetDeclarationNode);
    }
}
