package de.bmoth.parser.ast.nodes.ltl;

public class LTLKeywordNode implements LTLNode {

    public enum Kind {
        TRUE, FALSE
    }

    private Kind kind;

    public LTLKeywordNode(Kind kind) {
        this.kind = kind;
    }

    public Kind getKind() {
        return this.kind;
    }

    public String toString() {
        return this.kind.toString();
    }

    @Override
    public boolean equalAst(Node other) {
        if (!sameClass(other)) {
            return false;
        }

        return this.kind.equals(((LTLKeywordNode) other).kind);
    }

}
