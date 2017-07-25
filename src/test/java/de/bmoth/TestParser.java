package de.bmoth;

import de.bmoth.parser.Parser;
import de.bmoth.parser.ParserException;
import de.bmoth.parser.ast.nodes.FormulaNode;
import de.bmoth.parser.ast.nodes.MachineNode;
import de.bmoth.parser.ast.nodes.ltl.LTLFormula;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.fail;

public class TestParser {
    protected TestParser() {

    }

    public static LTLFormula parseLtlFormula(String formula) {
        try {
            return Parser.getLTLFormulaAsSemanticAst(formula);
        } catch (ParserException e) {
            fail(e.getMessage());
            return null;
        }
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
        private String definitions = "";
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
            if (!definitions.isEmpty()) {
                sb.append("DEFINITIONS ").append(definitions).append("\n");
            }
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

        public MachineBuilder setDefinitions(String definitions) {
            this.definitions = definitions;
            return this;
        }
    }

    public class CycleComparator<E extends Object> {
        private final Set<Set<String>> expected;
        private final Set<Set<String>> actual;


        public CycleComparator() {
            expected = new HashSet<>();
            actual = new HashSet<>();
        }

        public void addExpectedCycle(String... values) {
            expected.add(new HashSet<>(Arrays.asList(values)));
        }

        public void addActualCycle(Collection<? extends E> values) {
            actual.add(values.stream().map(E::toString).collect(Collectors.toSet()));
        }

        public void compare() {
            Set<Set<String>> unvisitedExpexted = new HashSet<>(expected);

            for (Set<String> currentActual : actual) {
                boolean found = false;
                for (Set<String> currentExpected : expected) {
                    if (currentActual.size() == currentExpected.size()) {
                        if (currentActual.containsAll(currentExpected)) {
                            found = true;
                            unvisitedExpexted.remove(currentExpected);
                            break;
                        }
                    }
                }
                if (!found) {
                    fail("Didn't find: " + currentActual);
                }
            }

            if (!unvisitedExpexted.isEmpty()) {
                fail("Didn't visit expected: " + unvisitedExpexted.toString());
            }
        }
    }
}
