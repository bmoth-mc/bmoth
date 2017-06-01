package de.bmoth.parser.ast;

import de.bmoth.antlr.BMoThParser.FormulaContext;
import org.antlr.v4.runtime.Token;

import java.util.LinkedHashMap;
import java.util.Map;

public class FormulaAnalyser {
    final FormulaContext formula;
    final LinkedHashMap<String, Token> implicitDeclarations = new LinkedHashMap<>();
    private final LinkedHashMap<Token, Token> declarationReferences;

    public FormulaAnalyser(FormulaContext formula) {
        this.formula = formula;
        FormulaScopeChecker scopeChecker = new FormulaScopeChecker();
        formula.accept(scopeChecker);
        this.declarationReferences = scopeChecker.declarationReferences;
    }

    public Map<Token, Token> getDeclarationReferences() {
        return this.declarationReferences;
    }

    class FormulaScopeChecker extends ScopeChecker {

        @Override
        public void identifierNodeNotFound(Token identifierToken) {
            String name = identifierToken.getText();
            if (implicitDeclarations.containsKey(name)) {
                declarationReferences.put(identifierToken, implicitDeclarations.get(name));
            } else {
                implicitDeclarations.put(name, identifierToken);
                declarationReferences.put(identifierToken, identifierToken);
            }
        }
    }

}
