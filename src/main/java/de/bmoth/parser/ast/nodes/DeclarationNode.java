package de.bmoth.parser.ast.nodes;

import org.antlr.v4.runtime.Token;

public class DeclarationNode extends TypedNode {

    private final String name;
    private final Token token;

    public DeclarationNode(Token token, String name) {
        this.name = name;
        this.token = token;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getLine() {
        return token.getLine();
    }

    public int getPos() {
        return token.getCharPositionInLine();
    }

}
