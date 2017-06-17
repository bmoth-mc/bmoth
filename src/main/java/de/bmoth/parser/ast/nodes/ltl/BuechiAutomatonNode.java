package de.bmoth.parser.ast.nodes.ltl;

public class BuechiAutomatonNode {

    String name;
    String[] incoming;
    LTLFormula[] nonprocessed;
    LTLFormula[] processed;
    LTLFormula[] next;

    public BuechiAutomatonNode(String name, String[] incoming, LTLFormula[] nonprocessed, LTLFormula[] processed, LTLFormula[] next) {
        this.name = name;
        this.incoming = incoming;
        this.nonprocessed = nonprocessed;
        this.processed = processed;
        this.next = next;
    }
}
