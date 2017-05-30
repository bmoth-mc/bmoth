package de.bmoth.parser.ast.nodes;

import de.bmoth.parser.ast.types.Type;

import java.util.Observable;
import java.util.Observer;

public abstract class TypedNode implements Node, Observer {

    private Type type;

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
