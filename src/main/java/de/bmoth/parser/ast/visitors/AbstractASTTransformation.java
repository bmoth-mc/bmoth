package de.bmoth.parser.ast.visitors;

import de.bmoth.parser.ast.nodes.Node;

public abstract class AbstractASTTransformation {

    private boolean changed = false;

    public boolean hasChanged() {
        return changed;
    }

    protected void setChanged() {
        changed = true;
    }

    public void resetChanged() {
        this.changed = false;
    }

    public abstract boolean canHandleNode(Node node);

    public abstract Node transformNode(Node node);

}
