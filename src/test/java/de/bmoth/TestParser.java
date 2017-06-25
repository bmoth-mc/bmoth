package de.bmoth;

import de.bmoth.parser.Parser;
import de.bmoth.parser.ParserException;
import de.bmoth.parser.ast.nodes.FormulaNode;
import de.bmoth.parser.ast.nodes.MachineNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    public class MachineBuilder {
        private String name = "";
        private String sets = "";
        private String variables = "";
        private String properties = "";
        private String invariant = "";
        private String initialization = "";
        private List<String> operations = new ArrayList<>();

        public MachineBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public MachineBuilder setVariables(String variables) {
            this.variables = variables;
            return this;
        }

        public MachineBuilder setInvariant(String invariant) {
            this.invariant = invariant;
            return this;
        }

        public MachineBuilder setInitialization(String initialization) {
            this.initialization = initialization;
            return this;
        }

        public MachineBuilder addOperation(String operation) {
            this.operations.add(operation);
            return this;
        }


        public MachineNode build() {
            return parseMachine(this.toString());
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("MACHINE ").append(name).append("\n");
            if (!sets.isEmpty()) {
                sb.append("SETS ").append(sets).append("\n");
            }
            if (!variables.isEmpty()) {
                sb.append("VARIABLES ").append(variables).append("\n");
            }
            if (!properties.isEmpty()) {
                sb.append("PROPERTIES ").append(properties).append("\n");
            }
            if (!invariant.isEmpty()) {
                sb.append("INVARIANT ").append(invariant).append("\n");
            }
            if (!initialization.isEmpty()) {
                sb.append("INITIALISATION ").append(initialization).append("\n");
            }

            if (!operations.isEmpty()) {
                sb.append("OPERATIONS\n");
                for (Iterator<String> op = operations.iterator(); op.hasNext(); ) {
                    String operation = op.next();
                    sb.append("\t").append(operation).append(op.hasNext() ? ";\n" : "\n");
                }
            }
            sb.append("END");
            return sb.toString();
        }

        public MachineBuilder setSets(String sets) {
            this.sets = sets;
            return this;
        }

        public MachineBuilder setProperties(String properties) {
            this.properties = properties;
            return this;
        }
    }

}
