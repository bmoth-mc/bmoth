package de.bmoth.parser.ast.nodes.ltl;

import de.bmoth.parser.ast.nodes.DeclarationNode;
import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.NodeUtil;

import java.util.List;

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

    @Override
    public boolean equalAst(Node other) {
        if (!NodeUtil.isSameClass(this, other)) {
            return false;
        }

        LTLFormula that = (LTLFormula) other;
        return this.ltlNode.equalAst(that.ltlNode)
            && NodeUtil.equalAst(this.implicitDeclarations, that.implicitDeclarations);
    }
}
