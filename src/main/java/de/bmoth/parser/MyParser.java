package de.bmoth.parser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import de.bmoth.antlr.BMoThLexer;
import de.bmoth.antlr.BMoThParser;

public class MyParser {

	public static ParseTree parse(String inputString) {
		ANTLRInputStream inputStream = new ANTLRInputStream(inputString);
		final BMoThLexer mylexer = new BMoThLexer(inputStream);
		// create a buffer of tokens pulled from the lexer
		CommonTokenStream tokens = new CommonTokenStream(mylexer);
		BMoThParser parser = new BMoThParser(tokens);

		parser.addErrorListener(new DiagnosticErrorListener());
		MyErrorListener myErrorListener = new MyErrorListener();
		parser.addErrorListener(myErrorListener);
		ParseTree tree = parser.start();
		return tree;
	}

}
