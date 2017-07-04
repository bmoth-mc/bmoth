package de.bmoth.parser.ast.nodes.ltl;

import java.util.List;

public class BuechiAutomatonNode {

    String name;
    List<String> incoming;
    List<LTLNode> unprocessed;
    List<LTLNode> processed;
    List<LTLNode> next;

    public BuechiAutomatonNode(String name, List<String> incoming, List<LTLNode> unprocessed, List<LTLNode> processed,
                               List<LTLNode> next) {
        this.name = name;
        this.incoming = incoming;
        this.unprocessed = unprocessed;
        this.processed = processed;
        this.next = next;
    }

    
}
