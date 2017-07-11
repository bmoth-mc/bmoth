package de.bmoth.parser.ast.nodes.ltl;

import de.bmoth.parser.ast.nodes.PredicateNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

public class BuechiAutomatonNode {

    String name;
    List<String> incoming;
    Set<LTLNode> unprocessed;
    Set<LTLNode> processed;
    Set<LTLNode> next;

    private List<PredicateNode> labels = new ArrayList<>();
    private Boolean isInitialState = false;
    Boolean isAcceptingState = false;

    public BuechiAutomatonNode(String name, List<String> incoming, Set<LTLNode> unprocessed, Set<LTLNode> processed,
                               Set<LTLNode> next) {
        this.name = name;
        this.incoming = incoming;
        this.unprocessed = unprocessed;
        this.processed = processed;
        this.next = next;
    }

    public void label() {
        if (incoming.contains("init")) {
            isInitialState = true;
        }
        for (LTLNode processedNode : processed) {
            if (processedNode instanceof LTLBPredicateNode) {
                labels.add(((LTLBPredicateNode) processedNode).getPredicate());
            }
        }
    }

    public String toString() {
        StringJoiner nodeString = new StringJoiner("\n", "", "");
        nodeString.add(this.name + ": ");

        StringJoiner incomingString = new StringJoiner(", ", "{", "}");
        for(String incomingNode: this.incoming) {
            incomingString.add(incomingNode);
        }
        nodeString.add("Incoming: " + incomingString.toString());

        StringJoiner unprocessedString = new StringJoiner("; ", "(", ")");
        for (LTLNode subNode : this.unprocessed) {
            unprocessedString.add(subNode.toString());
        }
        nodeString.add("Unprocessed: " + unprocessedString.toString());

        StringJoiner processedString = new StringJoiner("; ", "(", ")");
        for (LTLNode subNode : this.processed) {
            processedString.add(subNode.toString());
        }
        nodeString.add("Processed: " + processedString.toString());

        StringJoiner nextString = new StringJoiner("; ", "(", ")");
        for (LTLNode subNode : this.next) {
            nextString.add(subNode.toString());
        }
        nodeString.add("Next: " + nextString.toString());

        StringJoiner labelString = new StringJoiner("; ", "(", ")");
        for (PredicateNode predicate : labels) {
            labelString.add(predicate.toString());
        }
        nodeString.add("Labels: " + labelString.toString());
        nodeString.add("Initial state? " + isInitialState.toString());
        nodeString.add("Accepting state? " + isAcceptingState.toString());

        return nodeString.toString();
    }

}
