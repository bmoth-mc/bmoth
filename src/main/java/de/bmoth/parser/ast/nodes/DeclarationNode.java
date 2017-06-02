package de.bmoth.parser.ast.nodes;

import org.antlr.v4.runtime.tree.TerminalNode;

public class DeclarationNode extends TypedNode {

    private final String name;
    private final TerminalNode terminalNode;

    public DeclarationNode(TerminalNode terminalNode, String name) {
        super(terminalNode);
        this.name = name;
        this.terminalNode = terminalNode;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getLine() {
        return terminalNode.getSymbol().getLine();
    }

    public Object getPos() {
        return terminalNode.getSymbol().getCharPositionInLine();
    }

}
