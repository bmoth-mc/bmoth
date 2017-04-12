package de.bmoth.parser.ast.types;

import de.bmoth.exceptions.UnificationException;

public class IntegerType implements Type {

	private static IntegerType instance = new IntegerType();

	public static IntegerType getInstance() {
		return instance;
	}

	@Override
	public Type unify(Type otherType) throws UnificationException {
		if (otherType == instance) {
			return instance;
		} else if (otherType instanceof UntypedType) {
			((UntypedType) otherType).replaceBy(this);
			return instance;
		}
		throw new UnificationException();
	}

	@Override
	public String toString() {
		return "INTEGER";
	}

	@Override
	public boolean isUntyped() {
		return false;
	}
}
