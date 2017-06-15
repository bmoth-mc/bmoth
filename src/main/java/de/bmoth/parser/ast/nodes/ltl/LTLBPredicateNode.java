package de.bmoth.parser.ast.nodes.ltl;

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

}
