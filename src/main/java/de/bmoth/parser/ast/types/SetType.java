package de.bmoth.parser.ast.types;

import java.util.Observable;
import java.util.Observer;

import de.bmoth.exceptions.UnificationException;

public class SetType extends Observable implements Type, Observer {

	private Type subType;

	public SetType(Type subType) {
		setSubType(subType);
	}

	private void setSubType(Type subType) {
		this.subType = subType;
		if (subType instanceof Observable) {
			((Observable) subType).addObserver(this);
		}
	}

	@Override
	public boolean unifiable(Type otherType) {
		if (otherType == this) {
			return true;
		} else if (otherType instanceof UntypedType && !this.contains(otherType)) {
			return true;
		} else if (otherType instanceof SetType) {
			SetType setType = (SetType) otherType;
			return this.subType.unifiable(setType.subType);
		}
		return false;
	}

	@Override
	public boolean contains(Type other) {
		if (this.subType == other || this.subType.contains(other)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Type unify(Type otherType) throws UnificationException {
		if (unifiable(otherType)) {
			if (otherType instanceof UntypedType) {
				((UntypedType) otherType).replaceBy(this);
				return this;
			} else {
				SetType otherSetType = (SetType) otherType;
				otherSetType.replaceBy(this);

				// unify the sub types
				this.subType.unify(otherSetType.subType);
				/*
				 * Note, if the sub type has changed this instance will be
				 * automatically updated. Hence, there is no need to store the
				 * result of the unification.
				 */
				return this;
			}
		} else {
			throw new UnificationException();
		}
	}

	public void replaceBy(Type otherType) {
		/*
		 * unregister this instance from the sub type, i.e. it will be no longer
		 * updated
		 */
		if (subType instanceof Observable) {
			((Observable) subType).deleteObserver(this);
		}
		// notify all observers of this, they should point now to the otherType
		this.setChanged();
		this.notifyObservers(otherType);
	}

	@Override
	public void update(Observable o, Object arg) {
		o.deleteObserver(this);
		setSubType((Type) arg);
	}

	public Type getSubtype() {
		return this.subType;
	}

	@Override
	public String toString() {
		return "POW(" + subType.toString() + ")";
	}

	@Override
	public boolean isUntyped() {
		return this.subType.isUntyped();
	}

}
