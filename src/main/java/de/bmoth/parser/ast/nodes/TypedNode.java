package de.bmoth.parser.ast.nodes;

import java.util.Observable;
import java.util.Observer;

import de.bmoth.parser.ast.types.Type;

public abstract class TypedNode extends Node implements Observer {

	private Type type;

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
		if (type instanceof Observable) {
			((Observable) type).addObserver(this);
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		o.deleteObserver(this);
		setType((Type) arg);
	}
}
