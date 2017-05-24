package de.bmoth.parser.ast.types;

import de.bmoth.exceptions.UnificationException;

import java.util.Observable;
import java.util.Observer;

public class SetOrIntegerType extends Observable implements Type, Observer {

    private Type argType;

    public SetOrIntegerType(Type arg) {
        setArgType(arg);
    }

    private void setArgType(Type argType) {
        this.argType = argType;
        if (argType instanceof Observable) {
            ((Observable) argType).addObserver(this);
        }
    }

    public Type getArgType() {
        return this.argType;
    }

    @Override
    public void update(Observable o, Object arg) {
        o.deleteObserver(this);
        Type newType = (Type) arg;
        if (newType instanceof IntegerType || newType instanceof SetType) {
            this.setChanged();
            this.notifyObservers(newType);
        } else {
            setArgType(newType);
        }

    }

    @Override
    public Type unify(Type otherType) throws UnificationException {
        if (otherType instanceof IntegerType || otherType instanceof SetType) {
            if (argType instanceof Observable) {
                ((Observable) argType).deleteObserver(this);
            }
            this.argType.unify(otherType);
            this.setChanged();
            this.notifyObservers(otherType);
            return otherType;
        } else if (otherType instanceof UntypedType) {
            ((UntypedType) otherType).replaceBy(this);
            return this;
        } else if (otherType instanceof SetOrIntegerType) {
            SetOrIntegerType other = (SetOrIntegerType) otherType;
            other.replaceBy(this);
            this.argType.unify(other.argType);
            return this;
        } else if (otherType instanceof IntegerOrSetOfPairs) {
            return otherType.unify(this);
        }
        throw new UnificationException();
    }

    public void replaceBy(Type otherType) {
        /*
         * unregister this instance from the sub types, i.e. it will be no
         * longer updated
         */
        if (argType instanceof Observable) {
            ((Observable) argType).deleteObserver(this);
        }
        // notify all observers of this, they should point now to the otherType
        this.setChanged();
        this.notifyObservers(otherType);
    }

    @Override
    public boolean unifiable(Type otherType) {
        return otherType instanceof SetOrIntegerType || otherType instanceof IntegerType || otherType instanceof SetType
            || otherType instanceof UntypedType || otherType instanceof IntegerOrSetOfPairs;
    }

    @Override
    public boolean contains(Type other) {
        return false;
    }

    @Override
    public boolean isUntyped() {
        return true;
    }

}
