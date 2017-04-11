package de.bmoth.parser.ast.types;

import java.util.Observable;
import java.util.Observer;

import de.bmoth.exceptions.UnificationException;

public class SetType extends Observable implements Type, Observer {

	private Type subType;

	public SetType(Type subType) {

	}

	private void setSubType(Type subType) {
		this.subType = subType;
		if (subType instanceof Observable) {
			((Observable) subType).addObserver(this);
		}
	}

	@Override
	public Type unify(Type otherType) throws UnificationException {
		if (otherType instanceof UntypedType) {
			((UntypedType) otherType).notifyObservers(this);
			return this;
		} else if (otherType instanceof SetType) {
			SetType otherSetType = (SetType) otherType;
			otherSetType.notifyObservers(this);
			if (otherSetType.subType instanceof Observable) {
				Observable observable = (Observable) otherSetType.subType;
				observable.deleteObserver(otherSetType);
			}
			this.subType.unify(otherSetType.subType);
			return this;
		}
		throw new UnificationException();
	}

	@Override
	public void update(Observable o, Object arg) {
		o.deleteObserver(this);
		setSubType((Type) arg);
	}

}
