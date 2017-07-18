package de.bmoth.parser.ast.nodes.ltl;

import de.bmoth.parser.ast.nodes.PredicateNode;

import java.util.*;

public class BuechiAutomatonNode {

    String name;
    Set<BuechiAutomatonNode> incoming;
    Set<BuechiAutomatonNode> successors = new HashSet<>();
    Set<LTLNode> unprocessed;
    Set<LTLNode> processed;
    Set<LTLNode> next;

    private List<PredicateNode> labels = new ArrayList<>();
    boolean isInitialState = false;
    boolean isAcceptingState = false;

    public BuechiAutomatonNode(String name, Set<BuechiAutomatonNode> incoming, Set<LTLNode> unprocessed, Set<LTLNode> processed,
                               Set<LTLNode> next) {
        this.name = name;
        this.incoming = incoming;
        this.unprocessed = unprocessed;
        this.processed = processed;
        this.next = next;
    }

    public void label() {
        for (BuechiAutomatonNode incomingNode : incoming) {
            if (incomingNode.name.equals("init")) {
                isInitialState = true;
            }
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
        for (BuechiAutomatonNode incomingNode : this.incoming) {
            incomingString.add(incomingNode.name);
        }
        nodeString.add("Incoming: " + incomingString.toString());

        StringJoiner successorString = new StringJoiner(", ", "{", "}");
        for (BuechiAutomatonNode successorNode : this.successors) {
            successorString.add(successorNode.name);
        }
        nodeString.add("Successors: " + successorString.toString());

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
        nodeString.add("Initial state? " + isInitialState);
        nodeString.add("Accepting state? " + isAcceptingState);

        return nodeString.toString();
    }

    public Set<BuechiAutomatonNode> getSuccessors() {
        return successors;
    }

    public List<PredicateNode> getLabels() {
        return labels;
    }

    public boolean isAccepting() {
        return isAcceptingState;
    }
}
