package de.bmoth.parser.ast.types;

import java.util.Observable;
import java.util.Observer;

public class SetType extends Observable implements BType, Observer {

    private BType subType;

    public SetType(BType subType) {
        setSubType(subType);
    }

    private void setSubType(BType subType) {
        this.subType = subType;
        if (subType instanceof Observable) {
            ((Observable) subType).addObserver(this);
        }
    }

    public BType getSubType() {
        return this.subType;
    }

    @Override
    public boolean unifiable(BType otherType) {
        if (otherType == this) {
            return true;
        } else if (otherType instanceof UntypedType && !this.contains(otherType)) {
            return true;
        } else if (otherType instanceof SetOrIntegerType) {
            return true;
        } else if (otherType instanceof SetType) {
            SetType setType = (SetType) otherType;
            return getSubType().unifiable(setType.getSubType());
        } else if (otherType instanceof IntegerOrSetOfPairs) {
            return true;
        }
        return false;
    }

    @Override
    public boolean contains(BType other) {
        return this.subType == other || this.subType.contains(other);
    }

    @Override
    public BType unify(BType otherType) throws UnificationException {
        if (unifiable(otherType)) {
            if (otherType instanceof UntypedType) {
                ((UntypedType) otherType).replaceBy(this);
                return this;
            } else if (otherType instanceof SetOrIntegerType) {
                return otherType.unify(this);
            } else if (otherType instanceof IntegerOrSetOfPairs) {
                return otherType.unify(this);
            } else {
                SetType otherSetType = (SetType) otherType;
                otherSetType.replaceBy(this);

                // unify the sub types
                this.subType.unify(otherSetType.subType);
                /*
                 * Note, if the sub type has changed this instance will be
                 * automatically updated. Hence, there is no need to store the
                 * result of the unification.
                 */
                return this;
            }
        } else {
            throw new UnificationException();
        }
    }

    public void replaceBy(BType otherType) {
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
        setSubType((BType) arg);
    }
    
    @Override
    public String toString() {
        return "POW(" + getSubType().toString() + ")";
    }

    @Override
    public boolean isUntyped() {
        return this.subType.isUntyped();
    }

}
