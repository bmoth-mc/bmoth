package de.bmoth.parser.ast.nodes.ltl;

public class LTLInfixOperatorNode implements LTLNode {

    public enum Kind {
        IMPLICATION, UNTIL, RELEASE, AND, OR
    }

    private Kind kind;
    private LTLNode left;
    private LTLNode right;

    public LTLInfixOperatorNode(Kind kind, LTLNode left, LTLNode right) {
        this.kind = kind;
        this.left = left;
        this.right = right;
    }

    public Kind getKind() {
        return this.kind;
    }

    public LTLNode getLeft() {
        return this.left;
    }

    public LTLNode getRight() {
        return this.right;
    }

    public void setLeft(LTLNode left) {
        this.left = left;
    }

    public void setRight(LTLNode right) {
        this.right = right;
    }
}
