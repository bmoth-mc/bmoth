package de.bmoth.parser.ast.types;

import de.bmoth.parser.ast.UnificationException;

public class UserDefinedElementType implements Type {

    private String setName;

    public UserDefinedElementType(String name) {
        this.setName = name;
    }

    public String getSetName() {
        return this.setName;
    }

    @Override
    public Type unify(Type otherType) throws UnificationException {
        if (!this.unifiable(otherType)) {
            throw new UnificationException();
        }
        if (otherType instanceof UntypedType) {
            ((UntypedType) otherType).replaceBy(this);
        }
        return this;
    }

    @Override
    public boolean unifiable(Type otherType) {
        if (otherType instanceof UntypedType) {
            return true;
        }
        return otherType == this;
    }

    @Override
    public boolean contains(Type other) {
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
