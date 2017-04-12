package de.bmoth.parser.ast.types;

import java.util.Observable;

import de.bmoth.exceptions.UnificationException;

public class UntypedType extends Observable implements Type {

	@Override
	public boolean unifiable(Type otherType) {
		if (otherType instanceof UntypedType) {
			return true;
		} else {
			return otherType.unifiable(this);
		}
	}

	@Override
	public Type unify(Type otherType) throws UnificationException {
		if (unifiable(otherType)) {
			if (otherType instanceof UntypedType) {
				((UntypedType) otherType).replaceBy(this);
				return this;
			} else {
				return otherType.unify(this);
			}
		} else {
			throw new UnificationException();
		}
	}

	public void replaceBy(Type otherType) {
		this.setChanged();
		this.notifyObservers(otherType);
	}

	@Override
	public boolean isUntyped() {
		return true;
	}

	@Override
	public boolean contains(Type other) {
		return false;
	}

	@Override
	public String toString() {
		int shortenedHashCode = this.hashCode() / 10000;
		return "_Type" + shortenedHashCode + "_";
	}
}
