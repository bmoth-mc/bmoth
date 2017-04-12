package de.bmoth.parser.ast.types;

import java.util.Observable;

import de.bmoth.exceptions.UnificationException;

public class UntypedType extends Observable implements Type {

	@Override
	public Type unify(Type otherType) throws UnificationException {
		if (otherType instanceof UntypedType) {
			((UntypedType) otherType).replaceBy(this);
			return this;
		} else {
			return otherType.unify(this);
		}
	}

	public void replaceBy(Type otherType) {
		this.setChanged();
		this.notifyObservers(otherType);
	}
}
