package de.bmoth.parser.ast.nodes.ltl;

public class LTLInfixOperatorNode implements LTLNode {

    public enum Kind {
        IMPLICATION, UNTIL, WEAK_UNTIL, RELEASE, AND, OR
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

    @Override
    public String toString() {
        return this.kind + "(" + this.left + "," + this.right + ")";
    }

    @Override
    public boolean equalAst(Node other) {
        if (!sameClass(other)) {
            return false;
        }

        LTLInfixOperatorNode that = (LTLInfixOperatorNode) other;
        return this.kind.equals(that.kind)
            && this.left.equalAst(that.left)
            && this.right.equalAst(that.right);
    }
}
