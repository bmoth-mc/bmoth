package de.bmoth.parser.ast.nodes;

import java.util.List;

public interface Node {
    default boolean sameClass(Node that) {
        return this == that || that != null && this.getClass() == that.getClass();
    }

    boolean equalAst(Node other);

    class ListAstEquals<T extends Node> {
        public boolean equalAst(List<T> first, List<T> second) {
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
}
