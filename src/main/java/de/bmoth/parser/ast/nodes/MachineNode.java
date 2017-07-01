package de.bmoth.parser.ast.nodes;

import org.antlr.v4.runtime.tree.ParseTree;

import de.bmoth.parser.ast.nodes.ltl.LTLFormula;

import java.util.ArrayList;
import java.util.List;

public class MachineNode implements Node {

    private List<DeclarationNode> constants;
    private List<DeclarationNode> variables;
    private PredicateNode properties;
    private PredicateNode invariant;
    private SubstitutionNode initialisation;
    private List<OperationNode> operations;
    private final List<String> warnings = new ArrayList<>();
    private final List<EnumeratedSetDeclarationNode> setEnumerations = new ArrayList<>();
    private final List<DeclarationNode> deferredSets = new ArrayList<>();
    private final List<LTLFormula> ltlFormulas = new ArrayList<>();

    public List<DeclarationNode> getVariables() {
        return variables;
    }

    public void setVariables(List<DeclarationNode> variables) {
        this.variables = variables;
    }

    public MachineNode(ParseTree parseTree, String name) {

    }

    public List<DeclarationNode> getConstants() {
        return constants;
    }

    public void setConstants(List<DeclarationNode> constants) {
        this.constants = constants;
    }

    public void addLTLFormula(LTLFormula ltlFormula) {
        this.ltlFormulas.add(ltlFormula);
    }

    public SubstitutionNode getInitialisation() {
        return initialisation;
    }

    public void setInitialisation(SubstitutionNode initialisation) {
        this.initialisation = initialisation;
    }

    public List<OperationNode> getOperations() {
        return operations;
    }

    public void setOperations(List<OperationNode> operations) {
        this.operations = operations;
    }

    public PredicateNode getInvariant() {
        return invariant;
    }

    public void setInvariant(PredicateNode invariant) {
        this.invariant = invariant;
    }

    public PredicateNode getProperties() {
        return properties;
    }

    public void setProperties(PredicateNode properties) {
        this.properties = properties;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings.addAll(warnings);
    }

    public List<String> getWarnings() {
        return this.warnings;
    }

    public void addSetEnumeration(EnumeratedSetDeclarationNode setEnumeration) {
        this.setEnumerations.add(setEnumeration);
    }

    public List<EnumeratedSetDeclarationNode> getEnumaratedSets() {
        return new ArrayList<>(this.setEnumerations);
    }

    public void addDeferredSet(DeclarationNode setDeclNode) {
        this.deferredSets.add(setDeclNode);
    }

    public List<DeclarationNode> getDeferredSets() {
        return new ArrayList<>(this.deferredSets);
    }

    public List<LTLFormula> getLTLFormulas() {
        return new ArrayList<>(this.ltlFormulas);
    }

    @Override
    public boolean equalAst(Node other) {
        if (!NodeUtil.isSameClass(this, other)) {
            return false;
        }

        MachineNode that = (MachineNode) other;
        return NodeUtil.equalAst(this.setEnumerations, that.setEnumerations)
            && NodeUtil.equalAst(this.deferredSets, that.deferredSets)
            && NodeUtil.equalAst(this.constants, that.constants)
            && NodeUtil.equalAst(this.variables, that.variables)
            && NodeUtil.equalAst(this.operations, that.operations)
            && this.properties.equalAst(that.properties)
            && this.initialisation.equalAst(that.initialisation)
            && this.invariant.equalAst(that.invariant);

    }
}
