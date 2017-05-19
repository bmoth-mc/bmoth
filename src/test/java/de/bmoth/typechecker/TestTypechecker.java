package de.bmoth.typechecker;

import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.nodes.DeclarationNode;
import de.bmoth.parser.ast.nodes.FormulaNode;
import de.bmoth.parser.ast.nodes.MachineNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestTypechecker {

    private final Map<String, String> constants = new HashMap<>();
    private final Map<String, String> variables = new HashMap<>();

    public TestTypechecker(String machine) {
        MachineNode semanticAst = Parser.getMachineAsSemanticAst(machine);

        List<DeclarationNode> constantsDecls = semanticAst.getConstants();
        for (DeclarationNode declarationNode : constantsDecls) {
            constants.put(declarationNode.getName(), declarationNode.getType().toString());

        }

        List<DeclarationNode> variablesDecls = semanticAst.getVariables();
        for (DeclarationNode declarationNode : variablesDecls) {
            variables.put(declarationNode.getName(), declarationNode.getType().toString());

        }
    }

    public static HashMap<String, String> getFormulaTypes(String formula) {
        FormulaNode formulaNode = Parser.getFormulaAsSemanticAst(formula);
        HashMap<String, String> map = new HashMap<>();
        for (DeclarationNode decl : formulaNode.getImplicitDeclarations()) {
            map.put(decl.getName(), decl.getType().toString());
        }
        return map;
    }

    public Map<String, String> getConstants() {
        return constants;
    }

    public Map<String, String> getVariables() {
        return variables;
    }
}
