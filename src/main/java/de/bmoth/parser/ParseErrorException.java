package de.bmoth.parser;

import org.antlr.v4.runtime.CommonToken;

public class ParseErrorException extends Exception {
    private static final long serialVersionUID = 2305560853973886094L;
    private final CommonToken token;

    public ParseErrorException(CommonToken token, String message) {
        super(message);
        this.token = token;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Parse error: Unexpected input '").append(token.getText()).append("' ");
        sb.append("in line ").append(token.getLine());
        sb.append(" column " + token.getCharPositionInLine()).append(".\n");
        sb.append("Additional information: ").append(super.getMessage());
        return sb.toString();
    }
}
