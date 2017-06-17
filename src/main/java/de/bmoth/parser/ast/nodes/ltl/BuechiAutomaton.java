package de.bmoth.parser.ast.nodes.ltl;

import org.apache.commons.lang3.ArrayUtils;

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
            BuechiAutomatonNode nodeInSet = nodeIsInNodeSet(node, nodesSet);
            if (nodeInSet != null) {
                node.incoming = ArrayUtils.addAll(node.incoming, nodeInSet.incoming);
                return nodesSet;
            } else {
                return expand(new BuechiAutomatonNode(new_name(), new String[]{node.name}, node.next,
                    new LTLFormula[]{}, new LTLFormula[]{}), nodesSet);
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
