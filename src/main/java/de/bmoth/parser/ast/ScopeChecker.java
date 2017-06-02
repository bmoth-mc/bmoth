package de.bmoth.parser.ast;

import de.bmoth.antlr.BMoThParser;
import de.bmoth.antlr.BMoThParserBaseVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public abstract class ScopeChecker extends BMoThParserBaseVisitor<Void> {
    final LinkedList<LinkedHashMap<String, TerminalNode>> scopeTable = new LinkedList<>();
    final LinkedHashMap<TerminalNode, TerminalNode> declarationReferences = new LinkedHashMap<>();

    @Override
    public Void visitIdentifierExpression(BMoThParser.IdentifierExpressionContext ctx) {
        lookUpTerminalNode(ctx.IDENTIFIER());
        return null;
    }

    @Override
    public Void visitPredicateIdentifier(BMoThParser.PredicateIdentifierContext ctx) {
        lookUpTerminalNode(ctx.IDENTIFIER());
        return null;
    }

    @Override
    public Void visitSetComprehensionExpression(BMoThParser.SetComprehensionExpressionContext ctx) {
        visitQuantifiedFormula(ctx.identifier_list().IDENTIFIER(), ctx.predicate());
        return null;
    }

    @Override
    public Void visitQuantifiedExpression(BMoThParser.QuantifiedExpressionContext ctx) {
        visitQuantifiedFormula(ctx.quantified_variables_list().identifier_list().IDENTIFIER(), ctx.predicate(),
                ctx.expression());
        return null;
    }

    @Override
    public Void visitQuantifiedPredicate(BMoThParser.QuantifiedPredicateContext ctx) {
        visitQuantifiedFormula(ctx.quantified_variables_list().identifier_list().IDENTIFIER(), ctx.predicate());
        return null;
    }

    private void visitQuantifiedFormula(List<TerminalNode> identifiers, ParserRuleContext... contexts) {
        LinkedHashMap<String, TerminalNode> localIdentifiers = new LinkedHashMap<>();
        for (TerminalNode terminalNode : identifiers) {
            localIdentifiers.put(terminalNode.getSymbol().getText(), terminalNode);
        }
        scopeTable.add(localIdentifiers);
        for (ParserRuleContext node : contexts) {
            node.accept(this);
        }
        scopeTable.removeLast();
    }

    @Override
    public Void visitAssignSubstitution(BMoThParser.AssignSubstitutionContext ctx) {
        ctx.identifier_list().IDENTIFIER().stream().forEach(this::lookUpTerminalNode);
        ctx.expression_list().accept(this);
        return null;
    }

    @Override
    public Void visitBecomesElementOfSubstitution(BMoThParser.BecomesElementOfSubstitutionContext ctx) {
        ctx.identifier_list().IDENTIFIER().stream().forEach(this::lookUpTerminalNode);
        ctx.expression().accept(this);
        return null;
    }

    @Override
    public Void visitBecomesSuchThatSubstitution(BMoThParser.BecomesSuchThatSubstitutionContext ctx) {
        ctx.identifier_list().IDENTIFIER().stream().forEach(this::lookUpTerminalNode);
        ctx.predicate().accept(this);
        return null;
    }

    public void lookUpTerminalNode(TerminalNode terminalNode) {
        Token identifierToken = terminalNode.getSymbol();
        String name = identifierToken.getText();
        for (int i = scopeTable.size() - 1; i >= 0; i--) {
            LinkedHashMap<String, TerminalNode> map = scopeTable.get(i);
            if (map.containsKey(name)) {
                TerminalNode declarationToken = map.get(name);
                addDeclarationReference(terminalNode, declarationToken);
                return;
            }
        }
        identifierNodeNotFound(terminalNode);
    }

    @Override
    public Void visitAnySubstitution(BMoThParser.AnySubstitutionContext ctx) {
        LinkedHashMap<String, TerminalNode> localIdentifiers = new LinkedHashMap<>();
        for (TerminalNode terminalNode : ctx.identifier_list().IDENTIFIER()) {
            localIdentifiers.put(terminalNode.getSymbol().getText(), terminalNode);
        }
        scopeTable.add(localIdentifiers);
        ctx.predicate().accept(this);
        ctx.substitution().accept(this);
        scopeTable.removeLast();
        return null;
    }

    public void addDeclarationReference(TerminalNode identifierToken, TerminalNode declarationToken) {
        this.declarationReferences.put(identifierToken, declarationToken);
    }

    public void identifierNodeNotFound(TerminalNode terminalNode) {
        throw new ScopeException("Unknown identifier: " + terminalNode.getSymbol().getText());
    }

}
