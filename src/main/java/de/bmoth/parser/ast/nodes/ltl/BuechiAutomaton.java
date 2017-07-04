package de.bmoth.parser.ast.nodes.ltl;

import de.bmoth.parser.ast.nodes.NodeUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.StringJoiner;

import static de.bmoth.parser.ast.nodes.ltl.LTLKeywordNode.Kind.FALSE;

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

            if (NodeUtil.equalAst(node.processed, nodeInSet.processed)) {
                if (NodeUtil.equalAst(node.next, nodeInSet.next)) {
                    foundNode = nodeInSet;
                    break;
                }
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

    private List<LTLNode> next1(LTLInfixOperatorNode node) {
        List<LTLNode> newNodes = new ArrayList<>();
        if (node.getKind() == LTLInfixOperatorNode.Kind.UNTIL || node.getKind() == LTLInfixOperatorNode.Kind.RELEASE) {
            newNodes.add(node);
        }
        // In case of or an empty list is returned
        return newNodes;
    }

    private BuechiAutomatonNode handleFirstNodeInSplit(BuechiAutomatonNode node, LTLNode subNode, List<LTLNode> newProcessed) {
        // Prepare the different parts
        List<LTLNode> newUnprocessed = new1((LTLInfixOperatorNode) subNode);
        newUnprocessed.removeAll(node.processed);
        newUnprocessed.addAll(node.unprocessed);
        List<LTLNode> newNext = new ArrayList<>(node.next);
        newNext.addAll(next1((LTLInfixOperatorNode) subNode));

        return new BuechiAutomatonNode(newName(), node.incoming,
            newUnprocessed, newProcessed, newNext);
    }

    private BuechiAutomatonNode handleSecondNodeInSplit(BuechiAutomatonNode node, LTLNode subNode, List<LTLNode> newProcessed) {
        // Prepare the different parts
        List<LTLNode> newUnprocessed = new2((LTLInfixOperatorNode) subNode);
        newUnprocessed.removeAll(node.processed);
        newUnprocessed.addAll(node.unprocessed);

        return new BuechiAutomatonNode(newName(), node.incoming,
            newUnprocessed, newProcessed, node.next);
    }

    private List<BuechiAutomatonNode> expand(BuechiAutomatonNode node, List<BuechiAutomatonNode> nodesSet) {
        if (node.unprocessed.isEmpty()) {
            // The current node is completely processed and can be added to the list (or, in case he was
            // already added before, updated).
            BuechiAutomatonNode nodeInSet = nodeIsInNodeSet(node, nodesSet);
            if (nodeInSet != null) {
                nodeInSet.incoming.addAll(node.incoming);
                return nodesSet;
            } else {
                List<String> incoming = new ArrayList<>();
                incoming.add(node.name);
                List<BuechiAutomatonNode> newNodesSet = new ArrayList<>(nodesSet);
                newNodesSet.add(node);
                return expand(new BuechiAutomatonNode(newName(), incoming, node.next,
                    new ArrayList<>(), new ArrayList<>()), newNodesSet);
            }
        } else {
            LTLNode subNode = node.unprocessed.get(0);
            node.unprocessed.remove(0);

            // True, False
            if (subNode instanceof LTLKeywordNode) {
                if (((LTLKeywordNode) subNode).getKind() == FALSE) {
                    // Current node contains a contradiction, discard
                    return nodesSet;
                } else {
                    node.processed.add(subNode);
                    return expand(node, nodesSet);
                }
            } else
                // B predicate
                if (subNode instanceof LTLBPredicateNode) {
                    // TODO: Check if negation of predicate already occured -> contradiction -> boom
                    node.processed.add(subNode);
                    return expand(node, nodesSet);
            } else
                // Next, Not
                if (subNode instanceof LTLPrefixOperatorNode) {
                    if (((LTLPrefixOperatorNode) subNode).getKind() == LTLPrefixOperatorNode.Kind.NEXT) {
                        // Next
                        List<LTLNode> processed = new ArrayList<>(node.processed);
                        processed.add(subNode);
                        List<LTLNode> next = node.next;
                        next.add(((LTLPrefixOperatorNode) subNode).getArgument());
                        return expand(new BuechiAutomatonNode(node.name, node.incoming, node.unprocessed,
                            processed, next), nodesSet);
                    } else {
                        // Not
                        // TODO: Check if negative of predicate already occured -> contradiction -> boom
                        node.processed.add(subNode);
                        return expand(node, nodesSet);
                    }
            } else
                // And, Or, Until, Weak-Until
                if (subNode instanceof LTLInfixOperatorNode) {
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

                        return expand(new BuechiAutomatonNode(node.name, node.incoming,
                            newUnprocessed, newProcessed, node.next), nodesSet);

                    } else {
                        // Until, Weak-until, Or: Split the node in two
                        List<LTLNode> newProcessed = new ArrayList<>(node.processed);
                        newProcessed.add(subNode);
                        return expand(handleSecondNodeInSplit(node, subNode, newProcessed),
                            expand(handleFirstNodeInSplit(node, subNode, newProcessed), nodesSet));
                    }
            }
        }
        return nodesSet;
    }

    private List<BuechiAutomatonNode> createGraph(LTLNode node) {
        // Initialization
        List<String> initIncoming = new ArrayList<>();
        initIncoming.add("init");
        List<LTLNode> unprocessed = new ArrayList<>();
        unprocessed.add(node);
        return expand(new BuechiAutomatonNode(newName(), initIncoming, unprocessed, new ArrayList<>(),
            new ArrayList<>()), new ArrayList<>());
    }

    public String toString() {
        StringJoiner nodesString = new StringJoiner(",\n\n", "(", ")");
        for (BuechiAutomatonNode node: finalNodeSet) {
            StringJoiner nodeString = new StringJoiner("\n| ", "(", ")");
            StringJoiner processed = new StringJoiner("; ", "(", ")");
            for (LTLNode subNode : node.processed) {
                processed.add(subNode.toString());
            }
            nodeString.add(node.name + ": " + processed.toString());
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
