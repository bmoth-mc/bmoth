package de.bmoth.parser.ast.nodes.ltl;

import java.util.List;

import de.bmoth.parser.ast.nodes.DeclarationNode;
import de.bmoth.parser.ast.nodes.Node;

public class LTLFormula implements Node {

    private List<DeclarationNode> implicitDeclarations;
    private LTLNode ltlNode;

    public void setImplicitDeclarations(List<DeclarationNode> implicitDeclarations) {
        this.implicitDeclarations = implicitDeclarations;
    }

    public void setFormula(LTLNode ltlNode) {
        this.ltlNode = ltlNode;
    }

    public LTLNode getLTLNode() {
        return ltlNode;
    }

    public List<DeclarationNode> getImplicitDeclarations() {
        return implicitDeclarations;
    }

}
