package de.bmoth.parser.ast.nodes.ltl;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.NodeUtil;
import de.bmoth.parser.ast.nodes.PredicateNode;

public class LTLBPredicateNode implements LTLNode {

    private PredicateNode predicate;

    public LTLBPredicateNode(PredicateNode pred) {
        this.predicate = pred;
    }

    public PredicateNode getPredicate() {
        return this.predicate;
    }

    public void setPredicateNode(PredicateNode predicate) {
        this.predicate = predicate;
    }

    @Override
    public String toString() {
        return this.predicate.toString();
    }

    @Override
    public boolean equalAst(Node other) {
        return NodeUtil.isSameClass(this, other)
            && this.predicate.equalAst(((LTLBPredicateNode) other).predicate);

    }
}
