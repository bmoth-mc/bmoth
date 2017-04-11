package de.bmoth.parser.ast.types;

import de.bmoth.exceptions.UnificationException;

public class BooleanType implements Type {

	private static BooleanType instance = new BooleanType();

	public static BooleanType getInstance() {
		return instance;
	}

	@Override
	public Type unify(Type otherType) throws UnificationException {
		if (otherType == instance) {
			return instance;
		} else if (otherType instanceof UntypedType) {
			((UntypedType) otherType).notifyObservers(instance);
			return instance;
		}
		throw new UnificationException();
	}

}
