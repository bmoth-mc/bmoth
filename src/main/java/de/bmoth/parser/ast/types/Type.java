package de.bmoth.parser.ast.types;

import de.bmoth.exceptions.UnificationException;

public interface Type {

	public Type unify(Type otherType) throws UnificationException;
}
