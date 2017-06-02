package de.bmoth.parser.ast.nodes;

import de.bmoth.parser.ast.types.Type;

import java.util.Observable;
import java.util.Observer;

import org.antlr.v4.runtime.tree.ParseTree;

public abstract class TypedNode implements Node, Observer {

    private Type type;
    private final ParseTree parseTree;

    public TypedNode(ParseTree parseTree) {
        this.parseTree = parseTree;
    }

    public ParseTree getParseTree() {
        return this.parseTree;
    }

    public Type getType() {
        return type;
    }

    public boolean isUntyped() {
        return type.isUntyped();
    }

    public void setType(Type type) {
        if (type != null && type instanceof Observable) {
            ((Observable) type).deleteObserver(this);
        }
        this.type = type;
        if (type instanceof Observable) {
            ((Observable) type).addObserver(this);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        o.deleteObserver(this);
        setType((Type) arg);
    }
}
