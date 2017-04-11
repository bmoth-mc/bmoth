package de.bmoth.parser.ast.nodes;

import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;

public class MachineNode extends Node {

	private List<DeclarationNode> constants;
	private List<DeclarationNode> variables;
	private PredicateNode properties;
	private PredicateNode invariant;
	private SubstitutionNode initialisation;
	private List<OperationNode> operations;

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


}
