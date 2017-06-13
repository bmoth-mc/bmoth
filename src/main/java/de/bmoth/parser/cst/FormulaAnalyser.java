package de.bmoth.parser.cst;

import de.bmoth.antlr.BMoThParser.FormulaContext;
import de.bmoth.parser.cst.ScopeChecker.ScopeCheckerVisitorException;

import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FormulaAnalyser {
    private final FormulaContext formula;
    private final LinkedHashMap<String, TerminalNode> implicitDeclarations = new LinkedHashMap<>();
    private final LinkedHashMap<TerminalNode, TerminalNode> declarationReferences;

    public FormulaAnalyser(FormulaContext formula) throws ScopeException {
        this.formula = formula;
        try {
            FormulaScopeChecker scopeChecker = new FormulaScopeChecker();
            formula.accept(scopeChecker);
            this.declarationReferences = scopeChecker.declarationReferences;
        } catch (ScopeCheckerVisitorException e) {
            final Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "SCOPE_ERROR", e);
            throw e.getScopeException();
        }

    }

    public FormulaContext getFormula() {
        return this.formula;
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
