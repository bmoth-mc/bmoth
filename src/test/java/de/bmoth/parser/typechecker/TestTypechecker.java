package de.bmoth.parser.typechecker;

import de.bmoth.parser.Parser;
import de.bmoth.parser.ParserException;
import de.bmoth.parser.ast.nodes.DeclarationNode;
import de.bmoth.parser.ast.nodes.FormulaNode;
import de.bmoth.parser.ast.nodes.MachineNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class TestTypechecker {

    private final Map<String, String> constants = new HashMap<>();
    private final Map<String, String> variables = new HashMap<>();

    public TestTypechecker(String machine) {
        MachineNode semanticAst;
        try {
            semanticAst = Parser.getMachineAsSemanticAst(machine);
            List<DeclarationNode> constantsDecls = semanticAst.getConstants();
            for (DeclarationNode declarationNode : constantsDecls) {
                constants.put(declarationNode.getName(), declarationNode.getType().toString());

            }

            List<DeclarationNode> variablesDecls = semanticAst.getVariables();
            for (DeclarationNode declarationNode : variablesDecls) {
                variables.put(declarationNode.getName(), declarationNode.getType().toString());

            }
        } catch (ParserException e) {
            fail(e.getMessage());
        }

    }

    public static String typeCheckMachineAndGetErrorMessage(String machine) {
        try {
            Parser.getMachineAsSemanticAst(machine);
            fail("Expected a type error exception.");
            return null;
        } catch (ParserException e) {
            return e.getMessage();
        }
    }

    public static String typeCheckFormulaAndGetErrorMessage(String formula) {
        try {
            Parser.getFormulaAsSemanticAst(formula);
            fail("Expected a type error exception.");
            return null;
        } catch (ParserException e) {
            return e.getMessage();
        }
    }

    public static Map<String, String> getFormulaTypes(String formula) {
        FormulaNode formulaNode;
        try {
            formulaNode = Parser.getFormulaAsSemanticAst(formula);
            HashMap<String, String> map = new HashMap<>();
            for (DeclarationNode decl : formulaNode.getImplicitDeclarations()) {
                map.put(decl.getName(), decl.getType().toString());
            }
            return map;
        } catch (ParserException e) {
            fail(e.getMessage());
            return null;
        }

    }

    public Map<String, String> getConstants() {
        return constants;
    }

    public Map<String, String> getVariables() {
        return variables;
    }
}
