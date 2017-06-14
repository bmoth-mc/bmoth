package de.bmoth.parser.cst;


import de.bmoth.antlr.BMoThParser.LtlStartContext;

public class LTLFormulaAnalyser extends AbstractFormulaAnalyser {

    private LtlStartContext context;

    public LTLFormulaAnalyser(LtlStartContext context) throws ScopeException {
        super(context);
        this.context = context;
    }

    public LtlStartContext getLTLStartContext() {
        return this.context;
    }

}
