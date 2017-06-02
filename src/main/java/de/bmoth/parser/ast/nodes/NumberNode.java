package de.bmoth.parser.ast.nodes;

import org.antlr.v4.runtime.ParserRuleContext;

import java.math.BigInteger;

public class NumberNode extends ExprNode {

    private final BigInteger value;

    public NumberNode(ParserRuleContext ctx, BigInteger value) {
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
}
