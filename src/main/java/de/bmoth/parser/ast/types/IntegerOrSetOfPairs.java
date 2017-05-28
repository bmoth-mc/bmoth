package de.bmoth.parser.ast.types;

import java.util.Observable;
import java.util.Observer;

import de.bmoth.parser.ast.UnificationException;

public class IntegerOrSetOfPairs extends Observable implements Type, Observer {

    private Type left;
    private Type right;

    public IntegerOrSetOfPairs(Type left, Type right) {
        setLeftType(left);
        setRightType(right);
    }

    private void setLeftType(Type left) {
        this.left = left;
        if (left instanceof Observable) {
            ((Observable) left).addObserver(this);
        }
    }

    private void setRightType(Type right) {
        this.right = right;
        if (right instanceof Observable) {
            ((Observable) right).addObserver(this);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        o.deleteObserver(this);
        Type newType = (Type) arg;
        if (newType instanceof IntegerType) {
            this.setChanged();
            this.notifyObservers(newType);
            if (o == getLeft()) {
                try {
                    getRight().unify(IntegerType.getInstance());
                } catch (UnificationException e) {
                    // should not happen
                }
            } else {
                try {
                    getLeft().unify(IntegerType.getInstance());
                } catch (UnificationException e) {
                    // should not happen
                }
            }
        } else if (newType instanceof SetType) {
            this.setChanged();
            if (o == getLeft()) {
                // left is a set
                if (right instanceof Observable) {
                    ((Observable) right).deleteObserver(this);
                }
                SetType r = null;
                try {
                    r = (SetType) right.unify(new SetType(new UntypedType()));
                } catch (UnificationException e) {
                    // should not happen
                }
                this.notifyObservers(new SetType(new CoupleType(((SetType) newType).getSubtype(), r.getSubtype())));
            } else {
                // right is a set
                if (left instanceof Observable) {
                    ((Observable) left).deleteObserver(this);
                }
                SetType l = null;
                try {
                    l = (SetType) left.unify(new SetType(new UntypedType()));
                } catch (UnificationException e) {
                    // should not happen
                }
                this.notifyObservers(new SetType(new CoupleType(l.getSubtype(), ((SetType) newType).getSubtype())));
            }
        } else if (o == getLeft()) {
            setLeftType(newType);
        } else {
            setRightType(newType);
        }

    }

    @Override
    public Type unify(Type otherType) throws UnificationException {
        if (otherType instanceof SetType) {
            if (left instanceof Observable) {
                ((Observable) left).deleteObserver(this);
            }
            if (right instanceof Observable) {
                ((Observable) right).deleteObserver(this);
            }
            SetType l = (SetType) left.unify(new SetType(new UntypedType()));
            SetType r = (SetType) right.unify(new SetType(new UntypedType()));
            SetType found = new SetType(new CoupleType(l.getSubtype(), r.getSubtype()));
            found = (SetType) found.unify(otherType);
            this.setChanged();
            this.notifyObservers(found);
            return found;
        } else if (otherType instanceof UntypedType) {
            ((UntypedType) otherType).replaceBy(this);
            return this;
        } else if (otherType instanceof IntegerOrSetOfPairs) {
            IntegerOrSetOfPairs other = (IntegerOrSetOfPairs) otherType;
            other.replaceBy(this);
            this.getLeft().unify(other.getLeft());
            this.getRight().unify(other.getRight());
            return this;
        } else if (otherType instanceof SetOrIntegerType) {
            SetOrIntegerType other = (SetOrIntegerType) otherType;
            other.replaceBy(this);
            return this;
        } else if (otherType instanceof IntegerType) {
            this.replaceBy(IntegerType.getInstance());
            this.left.unify(IntegerType.getInstance());
            this.right.unify(IntegerType.getInstance());
            return IntegerType.getInstance();
        }
        throw new UnificationException();
    }

    public void replaceBy(Type otherType) {
        /*
         * unregister this instance from the sub types, i.e. it will be no
         * longer updated
         */
        if (getLeft() instanceof Observable) {
            ((Observable) getLeft()).deleteObserver(this);
        }
        if (getRight() instanceof Observable) {
            ((Observable) getRight()).deleteObserver(this);
        }
        // notify all observers of this, they should point now to the otherType
        this.setChanged();
        this.notifyObservers(otherType);
    }

    @Override
    public boolean unifiable(Type otherType) {
        if (otherType instanceof SetOrIntegerType || otherType instanceof IntegerType
            || otherType instanceof IntegerOrSetOfPairs || otherType instanceof UntypedType) {
            return true;
        } else if (otherType instanceof SetType) {
            SetType setType = (SetType) otherType;
            return setType.getSubtype() instanceof CoupleType;
        } else {
            return false;
        }
    }

    @Override
    public boolean contains(Type other) {
        return other == getLeft() || other == getRight();
    }

    @Override
    public boolean isUntyped() {
        return true;
    }

    public Type getLeft() {
        return left;
    }

    public Type getRight() {
        return right;
    }

}
