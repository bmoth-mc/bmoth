package de.bmoth.parser.ast.nodes.ltl;

public class BuechiAutomaton {

    public String new_name() {
        return "place holder";
    }
    
    public BuechiAutomatonNode[] expand(BuechiAutomatonNode node, BuechiAutomatonNode[] nodes_set) {
        return null;
    }

    public BuechiAutomatonNode[] create_graph(LTLFormula formula) {
        String[] initIncoming = {"init"};
        BuechiAutomatonNode[] nodes_set={};
        return expand(new BuechiAutomatonNode(new_name(), initIncoming, new LTLFormula[]{formula},
            new LTLFormula[]{}, new LTLFormula[]{}), nodes_set);
    }
}
