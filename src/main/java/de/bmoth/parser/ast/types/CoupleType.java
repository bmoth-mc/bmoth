package de.bmoth.parser.ast.types;

import de.bmoth.exceptions.UnificationException;

import java.util.Observable;
import java.util.Observer;

public class CoupleType extends Observable implements Type, Observer {

    private Type left;
    private Type right;

    public CoupleType(Type left, Type right) {
        setLeftType(left);
        setRightType(right);
    }

    private void setLeftType(Type leftType) {
        this.left = leftType;
        if (left instanceof Observable) {
            ((Observable) left).addObserver(this);
        }
    }

    private void setRightType(Type rightType) {
        this.right = rightType;
        if (right instanceof Observable) {
            ((Observable) right).addObserver(this);
        }
    }

    public Type getLeft() {
        return this.left;
    }

    public Type getRight() {
        return this.right;
    }

    @Override
    public void update(Observable o, Object arg) {
        o.deleteObserver(this);
        if (this.left == o) {
            setLeftType((Type) arg);
        } else {
            setRightType((Type) arg);
        }
    }

    @Override
    public CoupleType unify(Type otherType) throws UnificationException {
        if (unifiable(otherType)) {
            if (otherType instanceof UntypedType) {
                ((UntypedType) otherType).replaceBy(this);
                return this;
            } else if (otherType instanceof CoupleType) {
                CoupleType other = (CoupleType) otherType;
                other.replaceBy(this);

                this.left.unify(other.left);
                this.right.unify(other.right);
                return this;
            }
        }
        throw new UnificationException();
    }

    public void replaceBy(Type otherType) {
        /*
         * unregister this instance from the sub type, i.e. it will be no longer
		 * updated
		 */
        if (left instanceof Observable) {
            ((Observable) left).deleteObserver(this);
        }
        if (right instanceof Observable) {
            ((Observable) right).deleteObserver(this);
        }
        // notify all observers of this, they should point now to the otherType
        this.setChanged();
        this.notifyObservers(otherType);
    }

    @Override
    public boolean unifiable(Type otherType) {
        if (otherType == this) {
            return true;
        } else if (otherType instanceof UntypedType && !this.contains(otherType)) {
            return true;
        } else if (otherType instanceof CoupleType) {
            CoupleType pair = (CoupleType) otherType;
            return this.left.unifiable(pair.left) && this.right.unifiable(pair.right);
        }
        return false;
    }

    @Override
    public boolean contains(Type other) {
        return this.left == other || this.right == other || this.left.contains(other) || this.right.contains(other);
    }

    @Override
    public boolean isUntyped() {
        return this.left.isUntyped() || this.right.isUntyped();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.left.toString());
        sb.append("*");
        if (this.right instanceof CoupleType) {
            sb.append("(").append(this.right).append(")");
        } else {
            sb.append(this.right);
        }
        return sb.toString();

    }

}
