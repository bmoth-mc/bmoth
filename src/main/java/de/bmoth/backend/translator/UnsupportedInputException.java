package de.bmoth.backend.translator;

import de.prob.typechecker.exceptions.AbstractException;

public class UnsupportedInputException extends AbstractException {
	private static final long serialVersionUID = -5034231873364875543L;

	public UnsupportedInputException(String string) {
		super(string + " is not supported at the moment");
	}

	@Override
	public String getError() {
		return super.getMessage();
	}

}
