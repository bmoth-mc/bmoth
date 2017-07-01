package de.bmoth.parser.ast.nodes;

import org.antlr.v4.runtime.tree.ParseTree;

public class EnumeratedSetElementNode extends ExprNode {

    final EnumeratedSetDeclarationNode enumeratedSetDeclarationNode;
    final DeclarationNode declarationNode;
    final String elementName;

    public EnumeratedSetElementNode(ParseTree parseTree, EnumeratedSetDeclarationNode enumeratedSetDeclarationNode,
            String elementName, DeclarationNode declarationNode) {
        super(parseTree);
        this.enumeratedSetDeclarationNode = enumeratedSetDeclarationNode;
        this.elementName = elementName;
        this.declarationNode = declarationNode;
    }

    public String getName() {
        return this.elementName;
    }

    public EnumeratedSetDeclarationNode getEnumeratedSetDeclarationNode() {
        return this.enumeratedSetDeclarationNode;
    }

    public DeclarationNode getDeclarationNode() {
        return declarationNode;
    }

    @Override
    public boolean equalAst(Node other) {
        if (!NodeUtil.isSameClass(this, other)) {
            return false;
        }

        EnumeratedSetElementNode that = (EnumeratedSetElementNode) other;
        return this.elementName.equals(that.elementName)
            && this.declarationNode.equalAst(that.declarationNode)
            && this.enumeratedSetDeclarationNode.equalAst(that.enumeratedSetDeclarationNode);
    }
}
