package de.bmoth.parser.ast.nodes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bmoth.antlr.BMoThParser;
import de.bmoth.antlr.BMoThParser.PredicateOperatorContext;

public class PredicateOperatorNode extends PredicateNode {
	public enum PredicateOperator {
		AND,
	}

	private static final Map<Integer, PredicateOperator> map = new HashMap<>();
	static {
		map.put(BMoThParser.AND, PredicateOperator.AND);
	}

	private final List<PredicateNode> predicateArguments;

	public PredicateOperatorNode(PredicateOperatorContext ctx, List<PredicateNode> predicateArguments) {
		this.predicateArguments = predicateArguments;
	}

	public List<PredicateNode> getPredicateArguments() {
		return predicateArguments;
	}

}
