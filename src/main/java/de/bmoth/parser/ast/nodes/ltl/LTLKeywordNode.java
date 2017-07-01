package de.bmoth.parser.ast.nodes.ltl;

import de.bmoth.parser.ast.nodes.Node;
import de.bmoth.parser.ast.nodes.NodeUtil;

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
        return NodeUtil.isSameClass(this, other)
            && this.kind.equals(((LTLKeywordNode) other).kind);

    }

}
