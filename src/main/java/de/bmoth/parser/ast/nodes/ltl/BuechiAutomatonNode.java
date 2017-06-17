package de.bmoth.parser.ast.nodes.ltl;

import java.util.List;

public class BuechiAutomatonNode {

    String name;
    List<String> incoming;
    List<LTLFormula> unprocessed;
    List<LTLFormula> processed;
    List<LTLFormula> next;

    public BuechiAutomatonNode(String name, List<String> incoming, List<LTLFormula> unprocessed, List<LTLFormula> processed,
                               List<LTLFormula> next) {
        this.name = name;
        this.incoming = incoming;
        this.unprocessed = unprocessed;
        this.processed = processed;
        this.next = next;
    }
}
