package de.bmoth.backend.translator;

import de.be4.classicalb.core.parser.analysis.DepthFirstAdapter;
import de.be4.classicalb.core.parser.node.ASeqExpression;
import de.prob.typechecker.Typechecker;

public class BInputSupportedChecker extends DepthFirstAdapter {

	private Typechecker t;

	public BInputSupportedChecker(Typechecker t) {
		this.t = t;
	}

	public void caseASeqExpression(ASeqExpression node) {
		throw new UnsupportedInputException(node.toString());
	}

}
