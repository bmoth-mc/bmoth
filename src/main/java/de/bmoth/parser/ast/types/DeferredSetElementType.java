package de.bmoth.parser.ast.types;

public class DeferredSetElementType implements BType {

    private final String setName;

    public DeferredSetElementType(String name) {
        this.setName = name;
    }

    public String getSetName() {
        return this.setName;
    }

    @Override
    public BType unify(BType otherType) throws UnificationException {
        if (!this.unifiable(otherType)) {
            throw new UnificationException();
        }
        if (otherType instanceof UntypedType) {
            ((UntypedType) otherType).replaceBy(this);
        }
        return this;
    }

    @Override
    public boolean unifiable(BType otherType) {
        if (otherType instanceof UntypedType) {
            return true;
        }
        return otherType == this;
    }

    @Override
    public boolean contains(BType other) {
        return false;
    }

    @Override
    public boolean isUntyped() {
        return false;
    }

    @Override
    public String toString() {
        return this.setName;
    }

}
