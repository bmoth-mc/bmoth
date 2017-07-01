package de.bmoth.parser.ast.nodes;

import java.util.List;

public class FormulaNode implements Node {

    public enum FormulaType {
        EXPRESSION_FORMULA, PREDICATE_FORMULA
    }

    private final FormulaType type;
    private List<DeclarationNode> implicitDeclarations;
    private Node formula;
    private List<String> warnings;

    public FormulaNode(FormulaType type) {
        this.type = type;
    }

    public void setImplicitDeclarations(List<DeclarationNode> implicitDeclarations) {
        this.implicitDeclarations = implicitDeclarations;
    }

    public List<DeclarationNode> getImplicitDeclarations() {
        return implicitDeclarations;
    }

    public Node getFormula() {
        return formula;
    }

    public void setFormula(Node formula) {
        this.formula = formula;
    }

    public FormulaType getFormulaType() {
        return type;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    public List<String> getWarnings() {
        return this.warnings;
    }

    @Override
    public boolean equalAst(Node other) {
        if (!NodeUtil.isSameClass(this, other)) {
            return false;
        }

        FormulaNode that = (FormulaNode) other;
        return this.type.equals(that.type)
            && this.formula.equalAst(that.formula)
            && NodeUtil.equalAst(this.implicitDeclarations, that.implicitDeclarations);
    }

}
