package de.bmoth.exceptions;

public class TypeErrorException extends RuntimeException {
	private static final long serialVersionUID = -5344167922965323221L;

	public TypeErrorException(String string) {
		super(string);
	}

	public TypeErrorException() {
	}
}
