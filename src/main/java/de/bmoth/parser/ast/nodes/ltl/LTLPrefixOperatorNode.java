package de.bmoth.parser.ast.nodes.ltl;

public class LTLPrefixOperatorNode implements LTLNode {

    public enum Kind {
        GLOBALLY, FINALLY, NEXT, NOT
    }

    private Kind kind;
    private LTLNode argument;

    public LTLPrefixOperatorNode(Kind kind, LTLNode node) {
        this.kind = kind;
        this.argument = node;
    }

    public Kind getKind() {
        return this.kind;
    }

    public LTLNode getArgument() {
        return this.argument;
    }

    public void setLTLNode(LTLNode argument) {
        this.argument = argument;
    }

    @Override
    public String toString() {
        return this.kind + "(" + this.argument + ")";
    }
}
