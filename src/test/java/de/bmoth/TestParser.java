package de.bmoth;

import de.bmoth.parser.Parser;
import de.bmoth.parser.ParserException;
import de.bmoth.parser.ast.nodes.FormulaNode;
import de.bmoth.parser.ast.nodes.MachineNode;

import static org.junit.Assert.fail;

public class TestParser {
    protected TestParser() {

    }

    public static FormulaNode parseFormula(String formula) {
        try {
            return Parser.getFormulaAsSemanticAst(formula);
        } catch (ParserException e) {
            fail(e.getMessage());
            return null;
        }
    }

    public static MachineNode parseMachine(String machine) {
        try {
            return Parser.getMachineAsSemanticAst(machine);
        } catch (ParserException e) {
            fail(e.getMessage());
            return null;
        }
    }

    public static MachineNode parseMachineFromFile(String file) {
        try {
            return Parser.getMachineFileAsSemanticAst(file);
        } catch (ParserException e) {
            fail(e.getMessage());
            return null;
        }
    }

}
