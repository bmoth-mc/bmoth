package de.bmoth.parser.ast.types;

import de.bmoth.exceptions.UnificationException;

public class BoolType implements Type {

	private static BoolType instance = new BoolType();

	public static BoolType getInstance() {
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
		return "BOOL";
	}

}
