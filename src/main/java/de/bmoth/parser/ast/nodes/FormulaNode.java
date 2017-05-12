package de.bmoth.parser.ast.nodes;

import java.util.List;

public class FormulaNode implements Node {

    public static enum FormulaType {
        EXPRESSION_FORMULA, PREDICATE_FORMULA
    }

    private final FormulaType type;
    private List<DeclarationNode> implicitDeclarations;
    private Node formula;

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

}
