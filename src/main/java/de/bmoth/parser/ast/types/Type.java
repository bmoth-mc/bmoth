package de.bmoth.parser.ast.types;

public interface Type {

    Type unify(Type otherType) throws UnificationException;

    boolean unifiable(Type otherType);

    boolean contains(Type other);

    boolean isUntyped();

}
