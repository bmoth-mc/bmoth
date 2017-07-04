package de.bmoth.parser.ast.nodes.ltl;

import java.util.List;
import java.util.StringJoiner;

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

    public String toString() {
        StringJoiner nodeString = new StringJoiner("\n| ", "", "");
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

        return nodeString.toString();
    }

}
