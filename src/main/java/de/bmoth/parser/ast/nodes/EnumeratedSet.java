package de.bmoth.parser.ast.nodes;

import java.util.List;

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
}
