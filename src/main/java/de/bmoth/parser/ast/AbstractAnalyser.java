package de.bmoth.parser.ast;

import java.util.LinkedHashMap;

import org.antlr.v4.runtime.Token;

public abstract class AbstractAnalyser {

	final LinkedHashMap<Token, Token> declarationReferences = new LinkedHashMap<>();

	public void addDeclarationReference(Token identifierToken, Token declarationToken) {
		this.declarationReferences.put(identifierToken, declarationToken);
	}

	public abstract void identifierNodeFound(Token identifierToken);

}
