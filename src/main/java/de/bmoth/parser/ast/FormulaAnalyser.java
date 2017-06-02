package de.bmoth.parser.ast;

import de.bmoth.antlr.BMoThParser.FormulaContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.LinkedHashMap;
import java.util.Map;

public class FormulaAnalyser {
    final FormulaContext formula;
    final LinkedHashMap<String, TerminalNode> implicitDeclarations = new LinkedHashMap<>();
    private final LinkedHashMap<TerminalNode, TerminalNode> declarationReferences;

    public FormulaAnalyser(FormulaContext formula) {
        this.formula = formula;
        FormulaScopeChecker scopeChecker = new FormulaScopeChecker();
        formula.accept(scopeChecker);
        this.declarationReferences = scopeChecker.declarationReferences;
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
