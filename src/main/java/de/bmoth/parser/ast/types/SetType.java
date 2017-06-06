package de.bmoth.parser.ast.types;

public class SetType extends SubTypedObservable implements Type {

    public SetType(Type subType) {
        setSubType(subType);
    }

    @Override
    public boolean unifiable(Type otherType) {
        if (otherType == this) {
            return true;
        } else if (otherType instanceof UntypedType && !this.contains(otherType)) {
            return true;
        } else if (otherType instanceof SetOrIntegerType) {
            return true;
        } else if (otherType instanceof SetType) {
            SetType setType = (SetType) otherType;
            return getSubtype().unifiable(setType.getSubtype());
        } else if (otherType instanceof IntegerOrSetOfPairs) {
            return true;
        }
        return false;
    }

    @Override
    public boolean contains(Type other) {
        return getSubtype() == other || getSubtype().contains(other);
    }

    @Override
    public Type unify(Type otherType) throws UnificationException {
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
                getSubtype().unify(otherSetType.getSubtype());
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

    @Override
    public String toString() {
        return "POW(" + getSubtype().toString() + ")";
    }

    @Override
    public boolean isUntyped() {
        return getSubtype().isUntyped();
    }

}
