package de.bmoth.parser.ast.nodes.ltl;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import static de.bmoth.parser.ast.nodes.ltl.LTLKeywordNode.Kind.FALSE;

public class BuechiAutomaton {

    private int nodeCounter = 0;
    private List<BuechiAutomatonNode> finalNodesSet;

    public String new_name() {
        nodeCounter++;
        return "node" + String.valueOf(nodeCounter);
    }

    public BuechiAutomaton(LTLNode formula) {
        this.finalNodesSet = create_graph(formula);
    }

    private BuechiAutomatonNode nodeIsInNodeSet(BuechiAutomatonNode node, List<BuechiAutomatonNode> nodesSet) {
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

    private List<LTLNode> new1(LTLInfixOperatorNode node) {
        List<LTLNode> newNodes = new ArrayList<>();
        newNodes.add(node.getLeft());
        return newNodes;
    }

    private List<LTLNode> new2(LTLInfixOperatorNode node) {
        List<LTLNode> newNodes = new ArrayList<>();
        if (node.getKind() == LTLInfixOperatorNode.Kind.UNTIL) {
            newNodes.add(node);
        }
        // In case of OR an empty list is returned
        return newNodes;
    }

    private List<LTLNode> next1(LTLInfixOperatorNode node) {
        List<LTLNode> newNodes = new ArrayList<>();
        newNodes.add(node.getRight());
        return newNodes;
    }

    private BuechiAutomatonNode handleSecondNodeInSplit(BuechiAutomatonNode node, LTLNode formula, , List<LTLNode> newProcessed) {
        // Prepare the different parts
        List<LTLNode> unprocessed = new ArrayList<>(node.unprocessed);
        List<LTLNode> newUnprocessed = new1((LTLInfixOperatorNode) formula);
        newUnprocessed.removeAll(node.processed);
        newUnprocessed.addAll(unprocessed);
        List<LTLNode> newNext = new ArrayList<>(node.next);
        newNext.addAll(next1((LTLInfixOperatorNode) formula));

        return new BuechiAutomatonNode(new_name(), node.incoming,
            newUnprocessed, newProcessed, newNext);
    }

    private BuechiAutomatonNode handleFirstNodeInSplit(BuechiAutomatonNode node, LTLNode formula, List<LTLNode> newProcessed) {
        // Prepare the different parts
        List<LTLNode> unprocessed = new ArrayList<>(node.unprocessed);
        List<LTLNode> newUnprocessed = new2((LTLInfixOperatorNode) formula);
        newUnprocessed.removeAll(node.processed);
        newUnprocessed.addAll(unprocessed);

        return new BuechiAutomatonNode(new_name(), node.incoming,
            newUnprocessed, newProcessed, node.next);
    }

    private List<BuechiAutomatonNode> expand(BuechiAutomatonNode node, List<BuechiAutomatonNode> nodesSet) {
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
                List<BuechiAutomatonNode> newNodesSet = new ArrayList<>(nodesSet);
                newNodesSet.add(node);
                return expand(new BuechiAutomatonNode(new_name(), incoming, node.next,
                    new ArrayList<>(), new ArrayList<>()), newNodesSet);
            }
        } else {
            LTLNode formula = node.unprocessed.get(0);
            node.unprocessed.remove(0);

            // True, False
            if (formula instanceof LTLKeywordNode) {
                if (((LTLKeywordNode) formula).getKind() == FALSE) {
                    // Current node contains a contradiction, discard
                    return nodesSet;
                } else {
                    node.processed.add(formula);
                    return expand(node, nodesSet);
                }
            } else

                if (formula instanceof LTLPrefixOperatorNode) {

                    if (((LTLPrefixOperatorNode) formula).getKind() == LTLPrefixOperatorNode.Kind.NEXT) {
                        // Next
                        List<LTLNode> processed = new ArrayList<>(node.processed);
                        processed.add(formula);
                        List<LTLNode> next = node.next;
                        next.add(((LTLPrefixOperatorNode) formula).getArgument());
                        return expand(new BuechiAutomatonNode(node.name, node.incoming, node.unprocessed,
                            processed, next), nodesSet);
                    } else {
                        // Not
                        // TODO: Check if negative of predicate already occured -> contradiction -> boom
//                        if (neg(formula.getArgument()) in Old) {
//                            // Current node contains a contradiction, discard
//                            return nodesSet;
//                        } else {
//                            node.processed.add(formula);
//                            return expand(node, nodesSet);
//                        }
                        node.processed.add(formula);
                        return expand(node, nodesSet);
                    }
            } else

                if (formula instanceof LTLInfixOperatorNode) {

                    if (((LTLInfixOperatorNode) formula).getKind() == LTLInfixOperatorNode.Kind.AND) {
                        // Logical and
                        List<LTLNode> unprocessed = new ArrayList<>(node.unprocessed);
                        List<LTLNode> newUnprocessed = new ArrayList<>();
                        newUnprocessed.add(((LTLInfixOperatorNode) formula).getLeft());
                        newUnprocessed.add(((LTLInfixOperatorNode) formula).getRight());
                        newUnprocessed.removeAll(node.processed);
                        newUnprocessed.addAll(unprocessed);

                        List<LTLNode> newProcessed = new ArrayList<>(node.processed);
                        newProcessed.add(formula);

                        return expand(new BuechiAutomatonNode(node.name, node.incoming,
                            newUnprocessed, newProcessed, node.next), nodesSet);

                    } else {
                        // Until, logical or: Split the node in two
                        List<LTLNode> newProcessed = new ArrayList<>(node.processed);
                        newProcessed.add(formula);
                        return expand(handleFirstNodeInSplit(node, formula, newProcessed),
                            expand(handleSecondNodeInSplit(node, formula, newProcessed), nodesSet));
                    }
            }
        }
        return nodesSet;
    }

    private List<BuechiAutomatonNode> create_graph(LTLNode formula) {
        // Initialization
        List<String> initIncoming = new ArrayList<>();
        initIncoming.add("init");
        List<LTLNode> unprocessed = new ArrayList<>();
        unprocessed.add(formula);
        List<BuechiAutomatonNode> nodes_set = new ArrayList<>();

        return expand(new BuechiAutomatonNode(new_name(), initIncoming, unprocessed, new ArrayList<>(),
            new ArrayList<>()), nodes_set);
    }

    public String toString() {
        StringJoiner nodesString = new StringJoiner(", ", "(", ")");
        for (BuechiAutomatonNode node: finalNodesSet) {
            StringJoiner nodeString = new StringJoiner(" | ", "(", ")");
            nodeString.add("Node " + node.name + ": " + node.toString());
            StringJoiner incoming = new StringJoiner(", ", "{", "}");
            for(String incomingNode: node.incoming) {
                incoming.add(incomingNode);
            }
            nodeString.add("Incoming nodes: " + incoming.toString());
            nodesString.add(nodeString.toString());
        }
        return nodesString.toString();
    }
}
