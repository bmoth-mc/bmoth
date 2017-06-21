package de.bmoth.parser.ast.nodes.ltl;

import java.util.ArrayList;
import java.util.List;

import static de.bmoth.parser.ast.nodes.ltl.LTLKeywordNode.Kind.FALSE;

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

    public List<LTLNode> new1(LTLInfixOperatorNode node) {
        List<LTLNode> newNodes = new ArrayList<>();
        if ((node.getKind() == LTLInfixOperatorNode.Kind.UNTIL) || (node.getKind() == LTLInfixOperatorNode.Kind.OR)) {
            newNodes.add(node.getLeft());
        }
        // TODO: weak-until
        return newNodes;
    }

    public List<LTLNode> new2(LTLInfixOperatorNode node) {
        List<LTLNode> newNodes = new ArrayList<>();
        if (node.getKind() == LTLInfixOperatorNode.Kind.UNTIL) {
            newNodes.add(node);
        }
        // In case of OR an empty list is returned
        // TODO: weak-until
        return newNodes;
    }

    public List<LTLNode> next1(LTLInfixOperatorNode node) {
        List<LTLNode> newNodes = new ArrayList<>();
        if ((node.getKind() == LTLInfixOperatorNode.Kind.UNTIL) || (node.getKind() == LTLInfixOperatorNode.Kind.OR)) {
            newNodes.add(node.getRight());
        }
        // TODO: weak-until
        return newNodes;
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

            // Predicate, True, False
            if (formula instanceof LTLKeywordNode) {
                if (((LTLKeywordNode) formula).getKind() == FALSE) {
                    // Current node contains a contradiction, discard
                    return nodesSet;
                } else {
                    node.processed.add(formula);
                    return expand(node, nodesSet);
                }
            } else

                // Next
                if (formula instanceof LTLPrefixOperatorNode) {
                List<LTLNode> processed = node.processed;
                processed.add(formula);
                List<LTLNode> next = node.next;
                next.add(((LTLPrefixOperatorNode) formula).getArgument());
                return expand(new BuechiAutomatonNode(node.name, node.incoming, node.unprocessed,
                    processed, next), nodesSet);
            } else

                // Until, weak-until, logical or: Split the node in two
                if (formula instanceof LTLInfixOperatorNode) {
                    // Prepare the parts for the first new node
                    List<LTLNode> unprocessed = new ArrayList<>(node.unprocessed);
                    unprocessed.addAll(new1((LTLInfixOperatorNode) formula));
                    unprocessed.removeAll(node.processed);

                    List<LTLNode> processed = new ArrayList<>(node.processed);
                    processed.add(formula);

                    List<LTLNode> next = new ArrayList<>(node.next);
                    next.addAll(next1((LTLInfixOperatorNode) formula));

                    // Create the first new node
                    BuechiAutomatonNode node1 = new BuechiAutomatonNode(new_name(), node.incoming,
                        unprocessed, processed, next);

                    // Prepare the parts for the second new node
                    unprocessed = new ArrayList<>(node.unprocessed);
                    unprocessed.addAll(new2((LTLInfixOperatorNode) formula));
                    unprocessed.removeAll(node.processed);

                    // Create the second new node
                    BuechiAutomatonNode node2 = new BuechiAutomatonNode(new_name(), node.incoming,
                        unprocessed, processed, node.next);

                    return expand(node2, expand(node1, nodesSet));
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
