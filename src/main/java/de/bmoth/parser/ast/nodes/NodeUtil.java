package de.bmoth.parser.ast.nodes;

import java.util.List;

public class NodeUtil {
    private NodeUtil() {
    }

    public static boolean isSameClass(Node node1, Node node2) {
        return node1 == node2
            || (node1 != null
            && node2 != null
            && node1.getClass() == node2.getClass());
    }

    public static boolean equalAst(List<? extends Node> first, List<? extends Node> second) {
        if (first.size() != second.size()) {
            return false;
        }

        // relies on same ordering of elements, e.g. x+1 != 1+x
        for (int i = 0; i < first.size(); i++) {
            if (!first.get(i).equalAst(second.get(i))) {
                return false;
            }
        }
        return true;
    }
}
