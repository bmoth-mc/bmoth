package de.bmoth.parser.ast.types;

import de.bmoth.exceptions.UnificationException;

public class IntegerType implements Type {

	private static IntegerType instance = new IntegerType();

	public static IntegerType getInstance() {
		return instance;
	}

	@Override
	public boolean unifiable(Type otherType) {
		if (otherType == this || otherType instanceof UntypedType) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Type unify(Type otherType) throws UnificationException {
		if (unifiable(otherType)) {
			if (otherType == instance) {
				return instance;
			} else {
				((UntypedType) otherType).replaceBy(this);
				return instance;
			}
		} else {
			throw new UnificationException();
		}

	}

	@Override
	public String toString() {
		return "INTEGER";
	}

	@Override
	public boolean isUntyped() {
		return false;
	}

	@Override
	public boolean contains(Type other) {
		return false;
	}
}
