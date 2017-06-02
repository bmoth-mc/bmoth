package de.bmoth.parser.ast.nodes;

import org.antlr.v4.runtime.ParserRuleContext;

public class NumberNode extends ExprNode {

    private final int value;

    public NumberNode(ParserRuleContext ctx, int value) {
        super(ctx);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
