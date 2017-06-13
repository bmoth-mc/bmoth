package de.bmoth.parser;

import de.bmoth.antlr.BMoThLexer;
import de.bmoth.antlr.BMoThParser;
import de.bmoth.antlr.BMoThParser.FormulaContext;
import de.bmoth.antlr.BMoThParser.StartContext;
import de.bmoth.parser.ast.SemanticAstCreator;
import de.bmoth.parser.ast.TypeChecker;
import de.bmoth.parser.ast.nodes.FormulaNode;
import de.bmoth.parser.ast.nodes.MachineNode;
import de.bmoth.parser.cst.CSTAnalyser;
import de.bmoth.parser.cst.FormulaAnalyser;
import de.bmoth.parser.cst.MachineAnalyser;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

public class Parser {

    private BMoThParser getParser(String inputString) {
        CodePointCharStream fromString = CharStreams.fromString(inputString);
        final BMoThLexer lexer = new BMoThLexer(fromString);
        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        BMoThParser bMoThParser = new BMoThParser(tokens);
        bMoThParser.removeErrorListeners();
        ErrorListener errorListener = new ErrorListener();
        bMoThParser.addErrorListener(errorListener);
        return bMoThParser;
    }

    private StartContext parseMachine(String inputString) {
        BMoThParser parser = getParser(inputString);
        return parser.start();
    }

    private FormulaContext parseFormula(String inputString) {
        BMoThParser parser = getParser(inputString);
        return parser.formula();
    }

    private MachineNode getMachineAst(StartContext start) {
        MachineAnalyser machineAnalyser = new MachineAnalyser(start);
        SemanticAstCreator astCreator = new SemanticAstCreator(machineAnalyser);
        return (MachineNode) astCreator.getAstNode();
    }

    private FormulaNode getFormulaAst(FormulaContext formula) {
        FormulaAnalyser formulaAnalyser = new FormulaAnalyser(formula);
        SemanticAstCreator astCreator = new SemanticAstCreator(formulaAnalyser);
        return (FormulaNode) astCreator.getAstNode();
    }

    public static MachineNode getMachineFileAsSemanticAst(String file) throws IOException {
        String fileContent = readFile(new File(file));
        return getMachineAsSemanticAst(fileContent);
    }

    public static MachineNode getMachineAsSemanticAst(String inputString) {
        Parser parser = new Parser();
        StartContext start = parser.parseMachine(inputString);
        List<String> warnings = CSTAnalyser.analyseConcreteSyntaxTree(start);
        MachineNode machineNode = parser.getMachineAst(start);
        machineNode.setWarnings(warnings);
        TypeChecker.typecheckMachineNode(machineNode);

        return machineNode;
    }

    public static FormulaNode getFormulaAsSemanticAst(String inputString) {
        Parser parser = new Parser();
        FormulaContext formulaContext = parser.parseFormula(inputString);
        List<String> warnings = CSTAnalyser.analyseConcreteSyntaxTree(formulaContext);
        FormulaNode formulaNode = parser.getFormulaAst(formulaContext);
        formulaNode.setWarnings(warnings);
        TypeChecker.typecheckFormulaNode(formulaNode);
        return formulaNode;
    }

    public static final String readFile(final File file) throws IOException {
        try (InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file),
                Charset.forName("UTF-8"))) {

            final StringBuilder builder = new StringBuilder();
            final char[] buffer = new char[1024];
            int read;
            while ((read = inputStreamReader.read(buffer)) >= 0) {
                builder.append(String.valueOf(buffer, 0, read));
            }
            String content = builder.toString();

            inputStreamReader.close();

            if (!content.isEmpty()) {
                // remove utf-8 byte order mark
                // replaceAll \uFEFF did not work for some reason
                // apparently, unix like systems report a single character with
                // the
                // code
                // below
                if (content.startsWith("\uFEFF")) {
                    content = content.substring(1);
                }
                // while windows splits it up into three characters with the
                // codes
                // below
                else if (content.startsWith("\u00EF\u00BB\u00BF")) {
                    content = content.substring(3);
                }
            }

            return content.replaceAll("\r\n", "\n");
        }
    }

}
