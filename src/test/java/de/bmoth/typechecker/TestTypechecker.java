package de.bmoth.typechecker;

import de.bmoth.parser.Parser;
import de.bmoth.parser.ast.nodes.DeclarationNode;
import de.bmoth.parser.ast.nodes.FormulaNode;
import de.bmoth.parser.ast.nodes.MachineNode;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class TestTypechecker {

    public static final Hashtable<String, String> constants = new Hashtable<>();
    public static final Hashtable<String, String> variables = new Hashtable<>();

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

}
