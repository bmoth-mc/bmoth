package de.bmoth.parser.ast.nodes;

import org.antlr.v4.runtime.tree.ParseTree;

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
    private final List<EnumeratedSet> setEnumerations = new ArrayList<>();
    private final List<DeclarationNode> deferredSets = new ArrayList<>();

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

    public void addSetEnumeration(EnumeratedSet setEnumeration) {
        this.setEnumerations.add(setEnumeration);
    }

    public List<EnumeratedSet> getEnumaratedSets() {
        return this.setEnumerations;
    }

    public void addDeferredSet(DeclarationNode setDeclNode) {
        this.deferredSets.add(setDeclNode);
    }

    public List<DeclarationNode> getDeferredSets() {
        return this.deferredSets;
    }

}
