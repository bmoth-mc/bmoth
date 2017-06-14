package de.bmoth.parser.cst;

import de.bmoth.antlr.BMoThParser.FormulaContext;

public class FormulaAnalyser extends AbstractFormulaAnalyser {
    private FormulaContext formula;

    public FormulaAnalyser(FormulaContext formula) throws ScopeException {
        super(formula);
        this.formula = formula;
    }

    public FormulaContext getFormula() {
        return this.formula;
    }

}
