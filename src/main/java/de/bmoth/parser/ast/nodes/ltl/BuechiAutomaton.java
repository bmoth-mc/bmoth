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

    public BuechiAutomaton(LTLNode ltlNode) {
        this.finalNodeSet = createGraph(ltlNode);
    }

    private BuechiAutomatonNode nodeIsInNodeSet(BuechiAutomatonNode buechiNode, List<BuechiAutomatonNode> nodesSet) {
        // Check whether the finished node is already in the list (determined by the same Old- and Next-sets).
        BuechiAutomatonNode foundNode = null;
        for (BuechiAutomatonNode nodeInSet: nodesSet) {
            if (NodeUtil.equalAst(buechiNode.processed, nodeInSet.processed) && NodeUtil.equalAst(buechiNode.next, nodeInSet.next)) {
                    foundNode = nodeInSet;
                    break;
            }
        }
        return foundNode;
    }

    private List<LTLNode> new1(LTLInfixOperatorNode ltlNode) {
        List<LTLNode> newNodes = new ArrayList<>();
        if (ltlNode.getKind() == LTLInfixOperatorNode.Kind.RELEASE) {
            newNodes.add(ltlNode.getRight());
        } else {
            // Until, or
            newNodes.add(ltlNode.getLeft());
        }
        return newNodes;
    }

    private List<LTLNode> new2(LTLInfixOperatorNode ltlNode) {
        List<LTLNode> newNodes = new ArrayList<>();
        newNodes.add(ltlNode.getRight());
        if (ltlNode.getKind() == LTLInfixOperatorNode.Kind.RELEASE) {
            newNodes.add(ltlNode.getLeft());
        }
        return newNodes;
    }

    private List<LTLNode> next1(LTLInfixOperatorNode ltlNode) {
        List<LTLNode> newNodes = new ArrayList<>();
        if (ltlNode.getKind() == LTLInfixOperatorNode.Kind.UNTIL || ltlNode.getKind() == LTLInfixOperatorNode.Kind.RELEASE) {
            newNodes.add(ltlNode);
        }
        // In case of or an empty list is returned
        return newNodes;
    }

    private BuechiAutomatonNode buildFirstNodeInSplit(BuechiAutomatonNode buechiNode, LTLNode subNode, List<LTLNode> newProcessed) {
        // Prepare the different parts of the first new node created for Until, Release and Or
        List<LTLNode> newUnprocessed = new1((LTLInfixOperatorNode) subNode);
        newUnprocessed.removeAll(buechiNode.processed);
        newUnprocessed.addAll(buechiNode.unprocessed);
        List<LTLNode> newNext = new ArrayList<>(buechiNode.next);
        newNext.addAll(next1((LTLInfixOperatorNode) subNode));

        return new BuechiAutomatonNode(newName(), new ArrayList<>(buechiNode.incoming),
            newUnprocessed, newProcessed, newNext);
    }

    private BuechiAutomatonNode buildSecondNodeInSplit(BuechiAutomatonNode buechiNode, LTLNode subNode, List<LTLNode> newProcessed) {
        // Prepare the different parts of the second new node created for Until, Release and Or
        List<LTLNode> newUnprocessed = new2((LTLInfixOperatorNode) subNode);
        newUnprocessed.removeAll(buechiNode.processed);
        newUnprocessed.addAll(buechiNode.unprocessed);

        return new BuechiAutomatonNode(newName(), new ArrayList<>(buechiNode.incoming),
            newUnprocessed, newProcessed, new ArrayList<>(buechiNode.next));
    }

    private List<BuechiAutomatonNode> handleProcessedNode(BuechiAutomatonNode buechiNode, List<BuechiAutomatonNode> nodeSet) {
        // Add a processed node to the nodeSet or update it.
        BuechiAutomatonNode nodeInSet = nodeIsInNodeSet(buechiNode, nodeSet);
        if (nodeInSet != null) {
            nodeInSet.incoming.addAll(buechiNode.incoming);
            return nodeSet;
        } else {
            List<String> incoming = new ArrayList<>();
            incoming.add(buechiNode.name);
            List<BuechiAutomatonNode> newNodesSet = new ArrayList<>(nodeSet);
            newNodesSet.add(buechiNode);
            return expand(new BuechiAutomatonNode(newName(), incoming, new ArrayList<>(buechiNode.next),
                new ArrayList<>(), new ArrayList<>()), newNodesSet);
        }
    }

    private List<BuechiAutomatonNode> handleInfixOperatorNode(BuechiAutomatonNode buechiNode, LTLNode ltlNode,
                                                              List<BuechiAutomatonNode> nodeSet) {
        if (((LTLInfixOperatorNode) ltlNode).getKind() == LTLInfixOperatorNode.Kind.AND) {
            // And
            List<LTLNode> newUnprocessed = new ArrayList<>();
            newUnprocessed.add(((LTLInfixOperatorNode) ltlNode).getLeft());
            newUnprocessed.add(((LTLInfixOperatorNode) ltlNode).getRight());
            newUnprocessed.removeAll(buechiNode.processed);
            newUnprocessed.addAll(new ArrayList<>(buechiNode.unprocessed));

            List<LTLNode> newProcessed = new ArrayList<>(buechiNode.processed);
            newProcessed.add(ltlNode);

            return expand(new BuechiAutomatonNode(buechiNode.name, new ArrayList<>(buechiNode.incoming),
                newUnprocessed, newProcessed, new ArrayList<>(buechiNode.next)), nodeSet);
        } else {
            // Until, Release, Or: Split the node in two
            List<LTLNode> newProcessed = new ArrayList<>(buechiNode.processed);
            newProcessed.add(ltlNode);
            return expand(buildSecondNodeInSplit(buechiNode, ltlNode, newProcessed),
                expand(buildFirstNodeInSplit(buechiNode, ltlNode, newProcessed), nodeSet));
        }
    }

    private List<BuechiAutomatonNode> handlePrefixOperatorNode(BuechiAutomatonNode buechiNode, LTLNode ltlNode,
                                                               List<BuechiAutomatonNode> nodeSet) {
        if (((LTLPrefixOperatorNode) ltlNode).getKind() == LTLPrefixOperatorNode.Kind.NEXT) {
            // Next
            List<LTLNode> processed = new ArrayList<>(buechiNode.processed);
            processed.add(ltlNode);
            List<LTLNode> next = new ArrayList<>(buechiNode.next);
            next.add(((LTLPrefixOperatorNode) ltlNode).getArgument());
            return expand(new BuechiAutomatonNode(buechiNode.name + "_1", new ArrayList<>(buechiNode.incoming),
                new ArrayList<>(buechiNode.unprocessed), processed, next), nodeSet);
        } else {
            // Not
            buechiNode.processed.add(ltlNode);
            return expand(buechiNode, nodeSet);
        }
    }

    private List<BuechiAutomatonNode> expand(BuechiAutomatonNode buechiNode, List<BuechiAutomatonNode> nodeSet) {
        if (buechiNode.unprocessed.isEmpty()) {
            // The current node is completely processed and can be added to the list (or, in case he was
            // already added before, updated).
            return handleProcessedNode(buechiNode, nodeSet);
        } else {
            LTLNode ltlNode = buechiNode.unprocessed.get(0);
            buechiNode.unprocessed.remove(0);

            if (ltlNode instanceof LTLKeywordNode) {
                // True, False
                if (((LTLKeywordNode) ltlNode).getKind() == LTLKeywordNode.Kind.FALSE) {
                    // Current node contains a contradiction, discard
                    return nodeSet;
                } else {
                    buechiNode.processed.add(ltlNode);
                    return expand(buechiNode, nodeSet);
                }
            } else
                if (ltlNode instanceof LTLBPredicateNode) {
                    // B predicate
                    // TODO: Check if negation of predicate already occured -> contradiction -> boom
                    buechiNode.processed.add(ltlNode);
                    return expand(buechiNode, nodeSet);
                } else
                    if (ltlNode instanceof LTLPrefixOperatorNode) {
                            return handlePrefixOperatorNode(buechiNode, ltlNode, nodeSet);
                    } else if (ltlNode instanceof LTLInfixOperatorNode) {
                            return handleInfixOperatorNode(buechiNode, ltlNode, nodeSet);
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
