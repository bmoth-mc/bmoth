package de.bmoth.parser.ast.types;

import java.util.Observable;
import java.util.Observer;

public class SubTypedObservable extends Observable implements Observer {
    private Type subType;

    void setSubType(Type subType) {
        this.subType = subType;
        if (subType instanceof Observable) {
            ((Observable) subType).addObserver(this);
        }
    }

    public Type getSubType() {
        return this.subType;
    }

    public void replaceBy(Type otherType) {
        /*
         * unregister this instance from the sub type, i.e. it will be no longer
         * updated
         */
        if (subType instanceof Observable) {
            ((Observable) subType).deleteObserver(this);
        }
        // notify all observers of this, they should point now to the otherType
        this.setChanged();
        this.notifyObservers(otherType);
    }

    @Override
    public void update(Observable o, Object arg) {
        o.deleteObserver(this);
        setSubType((Type) arg);
    }
}
