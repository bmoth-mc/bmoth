package de.bmoth.parser.ast.types;

import de.bmoth.parser.ast.UnificationException;

public interface Type {

    public Type unify(Type otherType) throws UnificationException;

    public boolean unifiable(Type otherType);

    public boolean contains(Type other);

    public boolean isUntyped();

}
