package de.bmoth.parser.ast.nodes.ltl;

import de.bmoth.parser.ast.nodes.NodeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class BuechiAutomaton {

    private int nodeCounter = 0;
    private List<LTLInfixOperatorNode> subFormulasForAcceptance = new ArrayList<>();
    private List<List<BuechiAutomatonNode>> acceptingStateSets = new ArrayList<>();

    private List<BuechiAutomatonNode> finalNodeSet;

    public BuechiAutomaton(LTLNode ltlNode) {
        this.finalNodeSet = createGraph(ltlNode);
    }

    private String newName() {
        nodeCounter++;
        return "node" + nodeCounter;
    }

    private Boolean ltlNodeIsInList(LTLNode ltlNode, List<LTLNode> processed) {
        Boolean isInNodeSet = false;
        for (LTLNode processedNode : processed) {
            if (ltlNode.equalAst(processedNode)) {
                isInNodeSet = true;
            }
        }
        return isInNodeSet;
    }

    private List<LTLNode> removeDuplicates(List<LTLNode> ltlNodes) {
        List<LTLNode> singleLtlNodes = new ArrayList<>();
        for (LTLNode ltlNode : ltlNodes) {
            if (!ltlNodeIsInList(ltlNode, singleLtlNodes)) {
                singleLtlNodes.add(ltlNode);
            }
        }
        return singleLtlNodes;
    }

    private BuechiAutomatonNode buechiNodeIsInNodeSet(BuechiAutomatonNode buechiNode, List<BuechiAutomatonNode> nodesSet) {
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
        List<LTLNode> unprocessed = new1((LTLInfixOperatorNode) subNode);
        unprocessed.removeAll(buechiNode.processed);
        unprocessed.addAll(buechiNode.unprocessed);
        List<LTLNode> next = new ArrayList<>(buechiNode.next);
        next.addAll(next1((LTLInfixOperatorNode) subNode));

        unprocessed = removeDuplicates(unprocessed);
        next = removeDuplicates(next);
        return new BuechiAutomatonNode(newName(), new ArrayList<>(buechiNode.incoming),
            unprocessed, newProcessed, next);
    }

    private BuechiAutomatonNode buildSecondNodeInSplit(BuechiAutomatonNode buechiNode, LTLNode subNode, List<LTLNode> processed) {
        // Prepare the different parts of the second new node created for Until, Release and Or
        List<LTLNode> unprocessed = new2((LTLInfixOperatorNode) subNode);
        unprocessed.removeAll(buechiNode.processed);
        unprocessed.addAll(buechiNode.unprocessed);

        unprocessed = removeDuplicates(unprocessed);
        return new BuechiAutomatonNode(newName(), new ArrayList<>(buechiNode.incoming),
            unprocessed, processed, new ArrayList<>(buechiNode.next));
    }

    private List<BuechiAutomatonNode> handleProcessedNode(BuechiAutomatonNode buechiNode, List<BuechiAutomatonNode> nodeSet) {
        // Add a processed node to the nodeSet or update it.
        BuechiAutomatonNode nodeInSet = buechiNodeIsInNodeSet(buechiNode, nodeSet);
        if (nodeInSet != null) {
            nodeInSet.incoming.addAll(buechiNode.incoming);
            return nodeSet;
        } else {
            List<String> incoming = new ArrayList<>();
            incoming.add(buechiNode.name);
            nodeSet.add(buechiNode);
            return expand(new BuechiAutomatonNode(newName(), incoming, new ArrayList<>(buechiNode.next),
                new ArrayList<>(), new ArrayList<>()), nodeSet);
        }
    }

    private List<BuechiAutomatonNode> handleInfixOperatorNode(BuechiAutomatonNode buechiNode, LTLNode ltlNode,
                                                              List<BuechiAutomatonNode> nodeSet) {
        if (((LTLInfixOperatorNode) ltlNode).getKind() == LTLInfixOperatorNode.Kind.AND) {
            // And
            List<LTLNode> unprocessed = new ArrayList<>();
            unprocessed.add(((LTLInfixOperatorNode) ltlNode).getLeft());
            unprocessed.add(((LTLInfixOperatorNode) ltlNode).getRight());
            unprocessed.removeAll(buechiNode.processed);
            unprocessed.addAll(new ArrayList<>(buechiNode.unprocessed));

            List<LTLNode> processed = new ArrayList<>(buechiNode.processed);
            processed.add(ltlNode);

            unprocessed = removeDuplicates(unprocessed);
            processed = removeDuplicates(processed);
            return expand(new BuechiAutomatonNode(buechiNode.name, new ArrayList<>(buechiNode.incoming),
                unprocessed, processed, new ArrayList<>(buechiNode.next)), nodeSet);
        } else {
            // Until, Release, Or: Split the node in two
            if ((((LTLInfixOperatorNode) ltlNode).getKind() == LTLInfixOperatorNode.Kind.UNTIL) ||
                (((LTLInfixOperatorNode) ltlNode).getKind() == LTLInfixOperatorNode.Kind.RELEASE)) {
                subFormulasForAcceptance.add((LTLInfixOperatorNode) ltlNode);
            }
            List<LTLNode> processed = new ArrayList<>(buechiNode.processed);
            processed.add(ltlNode);
            processed = removeDuplicates(processed);
            return expand(buildSecondNodeInSplit(buechiNode, ltlNode, processed),
                expand(buildFirstNodeInSplit(buechiNode, ltlNode, processed), nodeSet));
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

            processed = removeDuplicates(processed);
            next = removeDuplicates(next);
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
                    // Discard the current node
                    return nodeSet;
                } else {
                    buechiNode.processed.add(ltlNode);
                    return expand(buechiNode, nodeSet);
                }
            } else if (ltlNode instanceof LTLBPredicateNode) {
                // B predicate
                buechiNode.processed.add(ltlNode);
                return expand(buechiNode, nodeSet);
            } else if (ltlNode instanceof LTLPrefixOperatorNode) {
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

    public void labelNodeSet() {
        for (BuechiAutomatonNode buechiNode : finalNodeSet) {
            buechiNode.label();
        }
        for (LTLInfixOperatorNode untilNode : subFormulasForAcceptance) {
            List<BuechiAutomatonNode> acceptingStateSet = new ArrayList<>();
            for (BuechiAutomatonNode buechiNode : finalNodeSet) {
                if (!ltlNodeIsInList(untilNode, buechiNode.processed) ||
                    ltlNodeIsInList(untilNode.getRight(), buechiNode.processed)) {
                    buechiNode.isAcceptingState = true;
                    acceptingStateSet.add(buechiNode);
                }
            }
            acceptingStateSets.add(acceptingStateSet);
        }
    }

    public String toString() {
        StringJoiner nodesString = new StringJoiner(",\n\n", "", "");
        for (BuechiAutomatonNode node: finalNodeSet) {
            nodesString.add(node.toString());
        }
        StringJoiner acceptingString = new StringJoiner(", ", "[", "]");
        for (List<BuechiAutomatonNode> acceptingStateSet : acceptingStateSets) {
            StringJoiner acceptingStatesString = new StringJoiner(", ", "(", ")");
            for (BuechiAutomatonNode node : acceptingStateSet) {
                acceptingStatesString.add(node.name);
            }
            acceptingString.add(acceptingStatesString.toString());
        }
        nodesString.add("Accepting state sets: " + acceptingString.toString());
        return nodesString.toString();
    }

    public List<BuechiAutomatonNode> getFinalNodeSet() {
        return finalNodeSet;
    }
}
