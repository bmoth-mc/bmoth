package de.bmoth.parser.ast;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.bmoth.parser.ast.nodes.TypedNode;
import de.bmoth.parser.ast.types.BType;
import de.bmoth.parser.ast.types.UnificationException;

public class TypeErrorException extends Exception {
    private static final long serialVersionUID = -5344167922965323221L;
    private final String message;

    private static final String TYPE_ERROR = "Type error";

    public TypeErrorException(String message) {
        this.message = message;
    }

    public TypeErrorException(BType expected, BType found, TypedNode node, UnificationException e) {
        this.message = createErrorMessage(expected, found, node);
        final Logger logger = Logger.getLogger(getClass().getName());
        logger.log(Level.SEVERE, TYPE_ERROR, e);
    }

    private String createErrorMessage(BType expected, BType found, TypedNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append("Expected ").append(expected).append(" but found ").append(found).append(" ");
        int line;
        int pos;
        String text;
        if (node.getParseTree() instanceof ParserRuleContext) {
            ParserRuleContext ctx = (ParserRuleContext) node.getParseTree();
            text = ctx.getText();
            line = ctx.getStart().getLine();
            pos = ctx.getStart().getCharPositionInLine();
        } else {
            TerminalNode terminalNode = (TerminalNode) node.getParseTree();
            text = terminalNode.getText();
            line = terminalNode.getSymbol().getLine();
            pos = terminalNode.getSymbol().getCharPositionInLine();
        }
        sb.append("at '").append(text).append("' starting ");
        sb.append("in line ").append(line);
        sb.append(" column ").append(pos).append(".");
        return sb.toString();
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String toString() {
        return this.message;
    }

}
