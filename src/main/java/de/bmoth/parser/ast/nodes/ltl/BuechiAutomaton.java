package de.bmoth.parser.ast.nodes.ltl;

import de.bmoth.parser.ast.nodes.PredicateNode;

import java.util.*;

public class BuechiAutomaton {

    private int nodeCounter = 0;
    private List<LTLInfixOperatorNode> subFormulasForAcceptance = new ArrayList<>();
    private List<List<BuechiAutomatonNode>> acceptingStateSets = new ArrayList<>();

    private List<BuechiAutomatonNode> finalNodeSet;

    public BuechiAutomaton(LTLNode ltlNode) {
        this.finalNodeSet = createGraph(ltlNode);
        labelNodeSet();
        determineSuccessors();
    }

    private String newName() {
        nodeCounter++;
        return "node" + nodeCounter;
    }

    private Boolean checkForContradiction(LTLNode ltlNode, Set<LTLNode> processedNodes) {
        PredicateNode negatedNode = ((LTLBPredicateNode) ltlNode).getPredicate().getNegatedPredicateNode();
        Boolean contradiction = false;
        for (LTLNode processedNode : processedNodes) {
            if (processedNode.getClass() == LTLBPredicateNode.class) {
                if (((LTLBPredicateNode) processedNode).getPredicate().equalAst(negatedNode)) {
                    contradiction = true;
                    break;
                }
            }
        }
        return contradiction;
    }

    private Boolean ltlNodeIsInList(LTLNode ltlNode, Set<LTLNode> processed) {
        Boolean isInNodeSet = false;
        for (LTLNode processedNode : processed) {
            if (ltlNode.equalAst(processedNode)) {
                isInNodeSet = true;
            }
        }
        return isInNodeSet;
    }

    private Boolean compareLTLNodeSets(Set<LTLNode> nodeSet, Set<LTLNode> processedNodeSet) {
        if (nodeSet.size() == processedNodeSet.size()){
            Set<LTLNode> nodeProcessed = new HashSet<>(nodeSet);
            Set<LTLNode> nodeInSetProcessed = new HashSet<>(processedNodeSet);
            Iterator<LTLNode> nodeIterator = nodeProcessed.iterator();
            while(nodeIterator.hasNext()){
                LTLNode ltlNode = nodeIterator.next();
                Iterator<LTLNode> nodeInSetIterator = nodeInSetProcessed.iterator();
                while(nodeInSetIterator.hasNext()) {
                    LTLNode ltlNodeInSet = nodeInSetIterator.next();
                    if (ltlNode.equalAst(ltlNodeInSet)) {
                        nodeIterator.remove();
                        nodeInSetIterator.remove();
                        break;
                    }
                }
            }
            return (nodeProcessed.isEmpty() && nodeInSetProcessed.isEmpty());
        } else {
            return false;
        }
    }

    private BuechiAutomatonNode buechiNodeIsInNodeSet(BuechiAutomatonNode buechiNode, List<BuechiAutomatonNode> nodesSet) {
        // Check whether the finished node is already in the list (determined by the same Old- and Next-sets).
        BuechiAutomatonNode foundNode = null;
        for (BuechiAutomatonNode nodeInSet: nodesSet) {
            Boolean processedEquals = compareLTLNodeSets(buechiNode.processed, nodeInSet.processed);
            Boolean nextEquals = compareLTLNodeSets(buechiNode.next, nodeInSet.next);
            if (processedEquals && nextEquals) {
                foundNode = nodeInSet;
                break;
            }
        }
        return foundNode;
    }

    private Set<LTLNode> new1(LTLInfixOperatorNode ltlNode) {
        Set<LTLNode> newNodes = new HashSet<>();
        if (ltlNode.getKind() == LTLInfixOperatorNode.Kind.RELEASE) {
            newNodes.add(ltlNode.getRight());
        } else {
            // Until, or
            newNodes.add(ltlNode.getLeft());
        }
        return newNodes;
    }

    private Set<LTLNode> new2(LTLInfixOperatorNode ltlNode) {
        Set<LTLNode> newNodes = new HashSet<>();
        newNodes.add(ltlNode.getRight());
        if (ltlNode.getKind() == LTLInfixOperatorNode.Kind.RELEASE) {
            newNodes.add(ltlNode.getLeft());
        }
        return newNodes;
    }

    private Set<LTLNode> next1(LTLInfixOperatorNode ltlNode) {
        Set<LTLNode> newNodes = new HashSet<>();
        if (ltlNode.getKind() == LTLInfixOperatorNode.Kind.UNTIL || ltlNode.getKind() == LTLInfixOperatorNode.Kind.RELEASE) {
            newNodes.add(ltlNode);
        }
        // In case of or an empty list is returned
        return newNodes;
    }

    private BuechiAutomatonNode buildFirstNodeInSplit(BuechiAutomatonNode buechiNode, LTLNode subNode, Set<LTLNode> newProcessed) {
        // Prepare the different parts of the first new node created for Until, Release and Or
        Set<LTLNode> unprocessed = new1((LTLInfixOperatorNode) subNode);
        unprocessed.removeAll(buechiNode.processed);
        unprocessed.addAll(buechiNode.unprocessed);
        Set<LTLNode> next = new HashSet<>(buechiNode.next);
        next.addAll(next1((LTLInfixOperatorNode) subNode));

        return new BuechiAutomatonNode(newName(), new HashSet<>(buechiNode.incoming),
            unprocessed, newProcessed, next);
    }

    private BuechiAutomatonNode buildSecondNodeInSplit(BuechiAutomatonNode buechiNode, LTLNode subNode, Set<LTLNode> processed) {
        // Prepare the different parts of the second new node created for Until, Release and Or
        Set<LTLNode> unprocessed = new2((LTLInfixOperatorNode) subNode);
        unprocessed.removeAll(buechiNode.processed);
        unprocessed.addAll(buechiNode.unprocessed);

        return new BuechiAutomatonNode(newName(), new HashSet<>(buechiNode.incoming),
            unprocessed, processed, new HashSet<>(buechiNode.next));
    }

    private List<BuechiAutomatonNode> handleProcessedNode(BuechiAutomatonNode buechiNode, List<BuechiAutomatonNode> nodeSet) {
        // Add a processed node to the nodeSet or update it.
        BuechiAutomatonNode nodeInSet = buechiNodeIsInNodeSet(buechiNode, nodeSet);
        if (nodeInSet != null) {
            nodeInSet.incoming.addAll(buechiNode.incoming);
            return nodeSet;
        } else {
            Set<BuechiAutomatonNode> incoming = new HashSet<>();
            incoming.add(buechiNode);
            nodeSet.add(buechiNode);
            return expand(new BuechiAutomatonNode(newName(), incoming, new HashSet<>(buechiNode.next),
                new HashSet<>(), new HashSet<>()), nodeSet);
        }
    }

    private List<BuechiAutomatonNode> handleInfixOperatorNode(BuechiAutomatonNode buechiNode, LTLNode ltlNode,
                                                              List<BuechiAutomatonNode> nodeSet) {
        if (((LTLInfixOperatorNode) ltlNode).getKind() == LTLInfixOperatorNode.Kind.AND) {
            // And
            Set<LTLNode> unprocessed = new HashSet<>(buechiNode.unprocessed);
            unprocessed.add(((LTLInfixOperatorNode) ltlNode).getLeft());
            unprocessed.add(((LTLInfixOperatorNode) ltlNode).getRight());
            unprocessed.removeAll(buechiNode.processed);

            Set<LTLNode> processed = new HashSet<>(buechiNode.processed);
            processed.add(ltlNode);

            return expand(new BuechiAutomatonNode(buechiNode.name, new HashSet<>(buechiNode.incoming),
                unprocessed, processed, new HashSet<>(buechiNode.next)), nodeSet);
        } else {
            // Until, Release, Or: Split the node in two
            if ((((LTLInfixOperatorNode) ltlNode).getKind() == LTLInfixOperatorNode.Kind.UNTIL) ||
                (((LTLInfixOperatorNode) ltlNode).getKind() == LTLInfixOperatorNode.Kind.RELEASE)) {
                subFormulasForAcceptance.add((LTLInfixOperatorNode) ltlNode);
            }
            Set<LTLNode> processed = new HashSet<>(buechiNode.processed);
            processed.add(ltlNode);
            return expand(buildSecondNodeInSplit(buechiNode, ltlNode, processed),
                expand(buildFirstNodeInSplit(buechiNode, ltlNode, processed), nodeSet));
        }
    }

    private List<BuechiAutomatonNode> handlePrefixOperatorNode(BuechiAutomatonNode buechiNode, LTLNode ltlNode,
                                                               List<BuechiAutomatonNode> nodeSet) {
        if (((LTLPrefixOperatorNode) ltlNode).getKind() == LTLPrefixOperatorNode.Kind.NEXT) {
            // Next
            Set<LTLNode> processed = new HashSet<>(buechiNode.processed);
            processed.add(ltlNode);
            Set<LTLNode> next = new HashSet<>(buechiNode.next);
            next.add(((LTLPrefixOperatorNode) ltlNode).getArgument());

            return expand(new BuechiAutomatonNode(buechiNode.name + "_1", new HashSet<>(buechiNode.incoming),
                new HashSet<>(buechiNode.unprocessed), processed, next), nodeSet);
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
            Iterator<LTLNode> iterator = buechiNode.unprocessed.iterator();
            LTLNode ltlNode =  iterator.next();
            iterator.remove();

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
                if (!checkForContradiction(ltlNode, buechiNode.processed)) {
                    buechiNode.processed.add(ltlNode);
                    return expand(buechiNode, nodeSet);
                } else {
                    return nodeSet;
                }
            } else if (ltlNode instanceof LTLPrefixOperatorNode) {
                return handlePrefixOperatorNode(buechiNode, ltlNode, nodeSet);
            } else if (ltlNode instanceof LTLInfixOperatorNode) {
                return handleInfixOperatorNode(buechiNode, ltlNode, nodeSet);
            }
        }
        return nodeSet;
    }

    private List<BuechiAutomatonNode> createGraph(LTLNode node) {
        Set<BuechiAutomatonNode> initIncoming = new HashSet<>();
        initIncoming.add(new BuechiAutomatonNode("init", new HashSet<>(), new HashSet<>(),
            new HashSet<>(), new HashSet<>()));
        Set<LTLNode> unprocessed = new HashSet<>();
        unprocessed.add(node);
        return expand(new BuechiAutomatonNode(newName(), initIncoming, unprocessed, new HashSet<>(),
            new HashSet<>()), new ArrayList<>());
    }

    private void labelNodeSet() {
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

    private void determineSuccessors() {
        for (BuechiAutomatonNode node: finalNodeSet) {
            for (BuechiAutomatonNode incomingNode : node.incoming) {
                incomingNode.successors.add(node);
            }
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
