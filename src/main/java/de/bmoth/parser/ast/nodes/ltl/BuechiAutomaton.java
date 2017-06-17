package de.bmoth.parser.ast.nodes.ltl;

public class BuechiAutomaton {

    public String new_name() {
        return "place holder";
    }

    public BuechiAutomatonNode nodeIsInNodeSet(BuechiAutomatonNode node, BuechiAutomatonNode[] nodesSet) {
        BuechiAutomatonNode foundNode = null;
        for (BuechiAutomatonNode nodeInSet: nodesSet) {
            if ((nodeInSet.processed == node.processed) && (nodeInSet.next == node.next)) {
                foundNode = nodeInSet;
                break;
            }
        }
        return foundNode;
    }

    public BuechiAutomatonNode[] expand(BuechiAutomatonNode node, BuechiAutomatonNode[] nodesSet) {
        if (node.nonprocessed.length == 0) {
            if (nodeIsInNodeSet(node, nodesSet) != null) {
                return nodesSet;
            }
        }
        return nodesSet;
    }

    public BuechiAutomatonNode[] create_graph(LTLFormula formula) {
        String[] initIncoming = {"init"};
        BuechiAutomatonNode[] nodes_set={};
        return expand(new BuechiAutomatonNode(new_name(), initIncoming, new LTLFormula[]{formula},
            new LTLFormula[]{}, new LTLFormula[]{}), nodes_set);
    }
}
