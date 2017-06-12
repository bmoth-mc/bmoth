package de.bmoth.parser.ast.types;

public abstract class BasicType implements BType {
    private String stringRepresentation;

    BasicType(String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    @Override
    public String toString() {
        return stringRepresentation;
    }

    @Override
    public boolean isUntyped() {
        return false;
    }

    @Override
    public boolean contains(BType other) {
        return false;
    }
}
