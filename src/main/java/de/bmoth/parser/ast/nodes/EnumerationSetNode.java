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

}
