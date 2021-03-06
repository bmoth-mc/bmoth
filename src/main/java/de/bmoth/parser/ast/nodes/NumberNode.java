package de.bmoth.parser.ast.nodes;

import org.antlr.v4.runtime.tree.ParseTree;

import java.math.BigInteger;

public class NumberNode extends ExprNode {

    private final BigInteger value;

    public NumberNode(ParseTree ctx, BigInteger value) {
        super(ctx);
        this.value = value;
    }

    public BigInteger getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equalAst(Node other) {
        return NodeUtil.isSameClass(this, other)
            && this.value.equals(((NumberNode) other).value);
    }
}
