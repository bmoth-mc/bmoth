package de.bmoth.parser.ast.nodes;

import de.bmoth.antlr.BMoThParser.ExpressionContext;

import java.util.List;

public class SetComprehensionNode extends ExprNode {
    private List<DeclarationNode> declarationList;
    private PredicateNode predicateNode;

    public SetComprehensionNode(ExpressionContext ctx, List<DeclarationNode> declarationList, PredicateNode predicateNode) {
        super(ctx);
        this.declarationList = declarationList;
        this.predicateNode = predicateNode;
    }

    public List<DeclarationNode> getDeclarationList() {
        return declarationList;
    }

    public PredicateNode getPredicateNode() {
        return predicateNode;
    }

    public void setPredicate(PredicateNode newPredicate) {
        predicateNode = newPredicate;
    }

    @Override
    public boolean equalAst(Node other) {
        if (!NodeUtil.isSameClass(this, other)) {
            return false;
        }

        SetComprehensionNode that = (SetComprehensionNode) other;
        return this.predicateNode.equalAst(that.predicateNode)
            && NodeUtil.equalAst(this.declarationList, that.declarationList);

    }
}
