package de.bmoth.parser.ast;

import de.bmoth.antlr.BMoThParser;
import de.bmoth.antlr.BMoThParserBaseVisitor;
import de.bmoth.exceptions.ScopeException;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class ScopeChecker extends BMoThParserBaseVisitor<Void> {
    final LinkedList<LinkedHashMap<String, Token>> scopeTable = new LinkedList<>();
    final LinkedHashMap<Token, Token> declarationReferences = new LinkedHashMap<>();

    @Override
    public Void visitIdentifierExpression(BMoThParser.IdentifierExpressionContext ctx) {
        Token identifierToken = ctx.IDENTIFIER().getSymbol();
        lookUpToken(identifierToken);
        return null;
    }

    @Override
    public Void visitPredicateIdentifier(BMoThParser.PredicateIdentifierContext ctx) {
        Token identifierToken = ctx.IDENTIFIER().getSymbol();
        lookUpToken(identifierToken);
        return null;
    }

    @Override
    public Void visitSetComprehensionExpression(BMoThParser.SetComprehensionExpressionContext ctx) {
        List<Token> identifiers = ctx.identifier_list().identifiers;
        visitQuantifiedFormula(identifiers, ctx.predicate());
        return null;
    }

    @Override
    public Void visitQuantifiedExpression(BMoThParser.QuantifiedExpressionContext ctx) {
        List<Token> identifiers = ctx.quantified_variables_list().identifier_list().identifiers;
        visitQuantifiedFormula(identifiers, ctx.predicate(), ctx.expression());
        return null;
    }

    @Override
    public Void visitQuantifiedPredicate(BMoThParser.QuantifiedPredicateContext ctx) {
        List<Token> identifiers = ctx.quantified_variables_list().identifier_list().identifiers;
        visitQuantifiedFormula(identifiers, ctx.predicate());
        return null;
    }

    private void visitQuantifiedFormula(List<Token> identifiers, ParserRuleContext... contexts) {
        LinkedHashMap<String, Token> localIdentifiers = new LinkedHashMap<>();
        for (Token token : identifiers) {
            localIdentifiers.put(token.getText(), token);
        }
        scopeTable.add(localIdentifiers);
        for (ParserRuleContext node : contexts) {
            node.accept(this);
        }
        scopeTable.removeLast();
    }

    @Override
    public Void visitAssignSubstitution(BMoThParser.AssignSubstitutionContext ctx) {
        List<Token> identifiers = ctx.identifier_list().identifiers;
        for (Token token : identifiers) {
            lookUpToken(token);
        }
        ctx.expression_list().accept(this);
        return null;
    }

    @Override
    public Void visitAnySubstitution(BMoThParser.AnySubstitutionContext ctx) {
        LinkedHashMap<String, Token> localIdentifiers = new LinkedHashMap<>();
        for (Token token : ctx.identifier_list().identifiers) {
            localIdentifiers.put(token.getText(), token);
        }
        scopeTable.add(localIdentifiers);
        ctx.predicate().accept(this);
        ctx.substitution().accept(this);
        scopeTable.removeLast();
        return null;
    }

    public void addDeclarationReference(Token identifierToken, Token declarationToken) {
        this.declarationReferences.put(identifierToken, declarationToken);
    }

    public void lookUpToken(Token identifierToken) {
        String name = identifierToken.getText();
        for (int i = scopeTable.size() - 1; i >= 0; i--) {
            LinkedHashMap<String, Token> map = scopeTable.get(i);
            if (map.containsKey(name)) {
                Token declarationToken = map.get(name);
                addDeclarationReference(identifierToken, declarationToken);
                return;
            }
        }
        identifierNodeNotFound(identifierToken);
    }

    public void identifierNodeNotFound(Token identifierToken) {
        throw new ScopeException("Unknown identifier: " + identifierToken.getText());
    }

}
