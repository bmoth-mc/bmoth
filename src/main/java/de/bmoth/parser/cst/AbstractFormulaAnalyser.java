package de.bmoth.parser.cst;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.bmoth.parser.cst.ScopeChecker.ScopeCheckerVisitorException;

public abstract class AbstractFormulaAnalyser {

    private final LinkedHashMap<String, TerminalNode> implicitDeclarations = new LinkedHashMap<>();
    private final LinkedHashMap<TerminalNode, TerminalNode> declarationReferences;

    public AbstractFormulaAnalyser(ParserRuleContext node) throws ScopeException {
        try {
            FormulaScopeChecker scopeChecker = new FormulaScopeChecker();
            node.accept(scopeChecker);
            this.declarationReferences = scopeChecker.declarationReferences;
        } catch (ScopeCheckerVisitorException e) {
            final Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "SCOPE_ERROR", e);
            throw e.getScopeException();
        }
    }

    public Map<String, TerminalNode> getImplicitDeclarations() {
        return new LinkedHashMap<>(this.implicitDeclarations);
    }

    public Map<TerminalNode, TerminalNode> getDeclarationReferences() {
        return this.declarationReferences;
    }

    class FormulaScopeChecker extends ScopeChecker {

        @Override
        public void identifierNodeNotFound(TerminalNode terminalNode) {
            String name = terminalNode.getSymbol().getText();
            if (implicitDeclarations.containsKey(name)) {
                declarationReferences.put(terminalNode, implicitDeclarations.get(name));
            } else {
                implicitDeclarations.put(name, terminalNode);
                declarationReferences.put(terminalNode, terminalNode);
            }
        }
    }
}
