package de.bmoth.parser.ast;

import org.antlr.v4.runtime.Token;

import java.util.LinkedHashMap;

public abstract class AbstractAnalyser {

    final LinkedHashMap<Token, Token> declarationReferences = new LinkedHashMap<>();

    public void addDeclarationReference(Token identifierToken, Token declarationToken) {
        this.declarationReferences.put(identifierToken, declarationToken);
    }

    public abstract void identifierNodeFound(Token identifierToken);

}
