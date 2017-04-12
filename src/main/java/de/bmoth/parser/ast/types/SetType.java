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
	public Type unify(Type otherType) throws UnificationException {
		if (otherType instanceof UntypedType) {
			((UntypedType) otherType).replaceBy(this);
			return this;
		} else if (otherType instanceof SetType) {
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
		throw new UnificationException();
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
		this.hasChanged();
		this.notifyObservers(otherType);
	}

	@Override
	public void update(Observable o, Object arg) {
		o.deleteObserver(this);
		setSubType((Type) arg);
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
