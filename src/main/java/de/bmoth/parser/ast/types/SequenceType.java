package de.bmoth.parser.ast.types;

public class SequenceType extends SubTypedObservable implements Type {

    public SequenceType(Type subType) {
        setSubType(subType);
    }

    @Override
    public boolean unifiable(Type otherType) {
        if (otherType == this) {
            return true;
        } else if (otherType instanceof UntypedType && !this.contains(otherType)) {
            return true;
        } else if (otherType instanceof SequenceType) {
            SequenceType seqType = (SequenceType) otherType;
            return getSubType().unifiable(seqType.getSubType());
        }
        return false;
    }

    @Override
    public boolean contains(Type other) {
        return getSubType() == other || getSubType().contains(other);
    }

    @Override
    public Type unify(Type otherType) throws UnificationException {
        if (unifiable(otherType)) {
            if (otherType instanceof UntypedType) {
                ((UntypedType) otherType).replaceBy(this);
                return this;
            } else {
                SequenceType otherSeqType = (SequenceType) otherType;
                otherSeqType.replaceBy(this);

                // unify the sub types
                getSubType().unify(otherSeqType.getSubType());
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
        return "SEQUENCE(" + getSubType().toString() + ")";
    }

    @Override
    public boolean isUntyped() {
        return getSubType().isUntyped();
    }

}
