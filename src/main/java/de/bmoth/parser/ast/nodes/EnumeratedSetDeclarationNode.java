package de.bmoth.parser.ast.nodes;

import java.util.List;
import java.util.stream.Collectors;

public class EnumeratedSetDeclarationNode implements Node {
    final DeclarationNode setDeclaration;
    final List<DeclarationNode> elements;

    public EnumeratedSetDeclarationNode(DeclarationNode setDeclaration, List<DeclarationNode> elements) {
        this.setDeclaration = setDeclaration;
        this.elements = elements;
    }

    public DeclarationNode getSetDeclaration() {
        return this.setDeclaration;
    }

    public List<DeclarationNode> getElements() {
        return this.elements;
    }

    public List<String> getElementsAsStrings() {
        return elements.stream().map(DeclarationNode::getName).collect(Collectors.toList());
    }

    @Override
    public boolean equalAst(Node other) {
        if (!NodeUtil.isSameClass(this, other)) {
            return false;
        }

        EnumeratedSetDeclarationNode that = (EnumeratedSetDeclarationNode) other;
        return this.setDeclaration.equalAst(that.setDeclaration)
            && NodeUtil.equalAst(this.elements, that.elements);
    }
}
