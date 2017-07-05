package de.bmoth.parser.ast.nodes.ltl;

import de.bmoth.parser.ast.nodes.NodeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class BuechiAutomaton {

    private int nodeCounter = 0;
    private List<BuechiAutomatonNode> finalNodeSet;

    public String newName() {
        nodeCounter++;
        return "node" + nodeCounter;
    }

    public BuechiAutomaton(LTLNode node) {
        this.finalNodeSet = createGraph(node);
    }

    private BuechiAutomatonNode nodeIsInNodeSet(BuechiAutomatonNode node, List<BuechiAutomatonNode> nodesSet) {
        // Check whether the finished node is already in the list (determined by the same Old- and Next-sets).
        BuechiAutomatonNode foundNode = null;
        for (BuechiAutomatonNode nodeInSet: nodesSet) {
            if (NodeUtil.equalAst(node.processed, nodeInSet.processed) && NodeUtil.equalAst(node.next, nodeInSet.next)) {
                    foundNode = nodeInSet;
                    break;
            }
        }
        return foundNode;
    }

    private List<LTLNode> new1(LTLInfixOperatorNode node) {
        List<LTLNode> newNodes = new ArrayList<>();
        if (node.getKind() == LTLInfixOperatorNode.Kind.RELEASE) {
            newNodes.add(node.getRight());
        } else {
            // Until, or
            newNodes.add(node.getLeft());
        }
        return newNodes;
    }

    private List<LTLNode> new2(LTLInfixOperatorNode node) {
        List<LTLNode> newNodes = new ArrayList<>();
        newNodes.add(node.getRight());
        if (node.getKind() == LTLInfixOperatorNode.Kind.RELEASE) {
            newNodes.add(node.getLeft());
        }
        return newNodes;
    }

    private List<LTLNode> next1(LTLInfixOperatorNode subNode) {
        List<LTLNode> newNodes = new ArrayList<>();
        if (subNode.getKind() == LTLInfixOperatorNode.Kind.UNTIL || subNode.getKind() == LTLInfixOperatorNode.Kind.RELEASE) {
            newNodes.add(subNode);
        }
        // In case of or an empty list is returned
        return newNodes;
    }

    private BuechiAutomatonNode buildFirstNodeInSplit(BuechiAutomatonNode node, LTLNode subNode, List<LTLNode> newProcessed) {
        // Prepare the different parts of the first new node created for Until, Release and Or
        List<LTLNode> newUnprocessed = new1((LTLInfixOperatorNode) subNode);
        newUnprocessed.removeAll(node.processed);
        newUnprocessed.addAll(node.unprocessed);
        List<LTLNode> newNext = new ArrayList<>(node.next);
        newNext.addAll(next1((LTLInfixOperatorNode) subNode));

        return new BuechiAutomatonNode(newName(), new ArrayList<>(node.incoming),
            newUnprocessed, newProcessed, newNext);
    }

    private BuechiAutomatonNode buildSecondNodeInSplit(BuechiAutomatonNode node, LTLNode subNode, List<LTLNode> newProcessed) {
        // Prepare the different parts of the second new node created for Until, Release and Or
        List<LTLNode> newUnprocessed = new2((LTLInfixOperatorNode) subNode);
        newUnprocessed.removeAll(node.processed);
        newUnprocessed.addAll(node.unprocessed);

        return new BuechiAutomatonNode(newName(), new ArrayList<>(node.incoming),
            newUnprocessed, newProcessed, new ArrayList<>(node.next));
    }

    private List<BuechiAutomatonNode> handleProcessedNode(BuechiAutomatonNode node, List<BuechiAutomatonNode> nodeSet) {
        // Add a processed node to the nodeSet or update it.
        BuechiAutomatonNode nodeInSet = nodeIsInNodeSet(node, nodeSet);
        if (nodeInSet != null) {
            nodeInSet.incoming.addAll(node.incoming);
            return nodeSet;
        } else {
            List<String> incoming = new ArrayList<>();
            incoming.add(node.name);
            List<BuechiAutomatonNode> newNodesSet = new ArrayList<>(nodeSet);
            newNodesSet.add(node);
            return expand(new BuechiAutomatonNode(newName(), incoming, new ArrayList<>(node.next),
                new ArrayList<>(), new ArrayList<>()), newNodesSet);
        }
    }

    private List<BuechiAutomatonNode> expand(BuechiAutomatonNode node, List<BuechiAutomatonNode> nodeSet) {
        if (node.unprocessed.isEmpty()) {
            // The current node is completely processed and can be added to the list (or, in case he was
            // already added before, updated).
            return handleProcessedNode(node, nodeSet);
        } else {
            LTLNode subNode = node.unprocessed.get(0);
            node.unprocessed.remove(0);

            if (subNode instanceof LTLKeywordNode) {
                // True, False
                if (((LTLKeywordNode) subNode).getKind() == LTLKeywordNode.Kind.FALSE) {
                    // Current node contains a contradiction, discard
                    return nodeSet;
                } else {
                    node.processed.add(subNode);
                    return expand(node, nodeSet);
                }
            } else
                if (subNode instanceof LTLBPredicateNode) {
                    // B predicate
                    // TODO: Check if negation of predicate already occured -> contradiction -> boom
                    node.processed.add(subNode);
                    return expand(node, nodeSet);
                } else
                    if (subNode instanceof LTLPrefixOperatorNode) {
                        if (((LTLPrefixOperatorNode) subNode).getKind() == LTLPrefixOperatorNode.Kind.NEXT) {
                            // Next
                            List<LTLNode> processed = new ArrayList<>(node.processed);
                            processed.add(subNode);
                            List<LTLNode> next = new ArrayList<>(node.next);
                            next.add(((LTLPrefixOperatorNode) subNode).getArgument());
                            return expand(new BuechiAutomatonNode(node.name + "_1", new ArrayList<>(node.incoming),
                                new ArrayList<>(node.unprocessed), processed, next), nodeSet);
                        } else {
                            // Not
                            node.processed.add(subNode);
                            return expand(node, nodeSet);
                        }
                    } else if (subNode instanceof LTLInfixOperatorNode) {
                            if (((LTLInfixOperatorNode) subNode).getKind() == LTLInfixOperatorNode.Kind.AND) {
                                // And
                                List<LTLNode> unprocessed = new ArrayList<>(node.unprocessed);
                                List<LTLNode> newUnprocessed = new ArrayList<>();
                                newUnprocessed.add(((LTLInfixOperatorNode) subNode).getLeft());
                                newUnprocessed.add(((LTLInfixOperatorNode) subNode).getRight());
                                newUnprocessed.removeAll(node.processed);
                                newUnprocessed.addAll(unprocessed);

                                List<LTLNode> newProcessed = new ArrayList<>(node.processed);
                                newProcessed.add(subNode);

                                return expand(new BuechiAutomatonNode(node.name, new ArrayList<>(node.incoming),
                                    newUnprocessed, newProcessed, new ArrayList<>(node.next)), nodeSet);

                            } else {
                                // Until, Release, Or: Split the node in two
                                List<LTLNode> newProcessed = new ArrayList<>(node.processed);
                                newProcessed.add(subNode);
                                return expand(buildSecondNodeInSplit(node, subNode, newProcessed),
                                    expand(buildFirstNodeInSplit(node, subNode, newProcessed), nodeSet));
                            }
                        }
        }
        return nodeSet;
    }

    private List<BuechiAutomatonNode> createGraph(LTLNode node) {
        // Initialization of the graph 
        List<String> initIncoming = new ArrayList<>();
        initIncoming.add("init");
        List<LTLNode> unprocessed = new ArrayList<>();
        unprocessed.add(node);
        return expand(new BuechiAutomatonNode(newName(), initIncoming, unprocessed, new ArrayList<>(),
            new ArrayList<>()), new ArrayList<>());
    }

    public String toString() {
        StringJoiner nodesString = new StringJoiner(",\n\n", "", "");
        for (BuechiAutomatonNode node: finalNodeSet) {
            nodesString.add(node.toString());
        }
        return nodesString.toString();
    }
}
