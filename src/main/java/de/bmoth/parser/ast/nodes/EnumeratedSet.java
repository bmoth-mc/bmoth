package de.bmoth.parser.ast.nodes;

import java.util.List;
import java.util.stream.Collectors;

public class EnumeratedSet {
    final DeclarationNode setDeclaration;
    final List<DeclarationNode> elements;

    public EnumeratedSet(DeclarationNode setDeclaration, List<DeclarationNode> elements) {
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
}
