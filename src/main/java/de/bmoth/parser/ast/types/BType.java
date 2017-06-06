package de.bmoth.parser.ast.types;

public interface BType {

    public BType unify(BType otherType) throws UnificationException;

    public boolean unifiable(BType otherType);

    public boolean contains(BType other);

    public boolean isUntyped();

}
