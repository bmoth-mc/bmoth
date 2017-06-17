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
        }
        return nodesSet;
    }

    public List<BuechiAutomatonNode> create_graph(LTLFormula formula) {
        List<String> initIncoming = new ArrayList<>();
        initIncoming.add("init");
        List<LTLFormula> unprocessed = new ArrayList<>();
        unprocessed.add(formula);
        List<BuechiAutomatonNode> nodes_set = new ArrayList<>();
        return expand(new BuechiAutomatonNode(new_name(), initIncoming, unprocessed, new ArrayList<>(),
            new ArrayList<>()), nodes_set);
    }
}
