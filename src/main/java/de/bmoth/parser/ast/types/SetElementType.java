package de.bmoth.parser.ast.types;

public class SetElementType implements BType {
    private final String setName;

    public SetElementType(String name) {
        this.setName = name;
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

    public String getSetName() {
        return setName;
    }

    @Override
    public String toString() {
        return this.setName;
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
}
