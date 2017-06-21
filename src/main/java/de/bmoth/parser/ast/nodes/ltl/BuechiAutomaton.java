package de.bmoth.parser.ast.nodes.ltl;

import java.util.ArrayList;
import java.util.List;

public class BuechiAutomaton {

    private int nodeCounter = 0;

    public String new_name() {
        nodeCounter++;
        return "place holder" + String.valueOf(nodeCounter);
    }

    public BuechiAutomatonNode nodeIsInNodeSet(BuechiAutomatonNode node, List<BuechiAutomatonNode> nodesSet) {
        // Check whether the finished node is already in the list (determined by the same Old- and Next-sets).
        BuechiAutomatonNode foundNode = null;
        for (BuechiAutomatonNode nodeInSet: nodesSet) {
            if ((nodeInSet.processed == node.processed) && (nodeInSet.next == node.next)) {
                foundNode = nodeInSet;
                break;
            }
        }
        return foundNode;
    }

    public List<BuechiAutomatonNode> expand(BuechiAutomatonNode node, List<BuechiAutomatonNode> nodesSet) {
        if (node.unprocessed.size() == 0) {
            // The current node is completely processed and can be added to the list (or, in case he was
            // already added before, updated).
            BuechiAutomatonNode nodeInSet = nodeIsInNodeSet(node, nodesSet);
            if (nodeInSet != null) {
                node.incoming.addAll(nodeInSet.incoming);
                return nodesSet;
            } else {
                List<String> incoming = new ArrayList<>();
                incoming.add(node.name);
                return expand(new BuechiAutomatonNode(new_name(), incoming, node.next,
                    new ArrayList<>(), new ArrayList<>()), nodesSet);
            }
        } else {
            LTLNode formula = node.unprocessed.get(0);
            node.unprocessed.remove(0);
            if (formula instanceof LTLPrefixOperatorNode) {
                List<LTLNode> processed = node.processed;
                processed.add(formula);
                List<LTLNode> next = node.next;
                next.add(((LTLPrefixOperatorNode) formula).getArgument());
                return expand(new BuechiAutomatonNode(node.name, node.incoming, node.unprocessed,
                    processed, next), nodesSet);
            }
        }
        return nodesSet;
    }

    public List<BuechiAutomatonNode> create_graph(LTLNode formula) {
        // Initialization
        List<String> initIncoming = new ArrayList<>();
        initIncoming.add("init");
        List<LTLNode> unprocessed = new ArrayList<>();
        unprocessed.add(formula);
        List<BuechiAutomatonNode> nodes_set = new ArrayList<>();

        return expand(new BuechiAutomatonNode(new_name(), initIncoming, unprocessed, new ArrayList<>(),
            new ArrayList<>()), nodes_set);
    }
}
