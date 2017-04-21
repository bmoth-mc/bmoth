package de.bmoth.backend;

import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.nodes.FormulaNode;

public class FormulaTranslator {

	public static void translateFormula(String formula) {
		FormulaNode node = Parser.getFormulaAsSemanticAst(formula);
		FormulaTranslator formulaTranslator = new FormulaTranslator();
	}
}
