package de.bmoth.exceptions;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class ScopeException extends RuntimeException {
	private static final long serialVersionUID = 6584928829237049955L;

	public ScopeException(ParserRuleContext ctx, String message) {
		super(message);
	}

	public ScopeException(Token identifierToken, String message) {
		super(message);
	}
}
